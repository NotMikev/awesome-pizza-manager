package com.awesome.pizza.order.manager.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.awesome.pizza.order.manager.entity.ApiAuditLog;
import com.awesome.pizza.order.manager.service.ApiAuditLogService;
import com.awesome.pizza.order.manager.constants.AwesomeConstants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro per l'audit delle chiamate API. Registra dettagli di request/response
 * per tutte le chiamate agli endpoint con base path configurato.
 */
@Component
@Order(1)
public class ApiAuditFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuditFilter.class);

    private final ApiAuditLogService auditLogService;
    private final String apiPathPrefix;

    public ApiAuditFilter(ApiAuditLogService auditLogService, @Value("${server.servlet.context-path:/awesome}/api/") String apiPathPrefix) {
        this.auditLogService = auditLogService;
        this.apiPathPrefix = apiPathPrefix;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        if (!isApiRequest(request)) {
            chain.doFilter(request, response);
            return;
        }

        String correlationId = generateCorrelationId();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        setCorrelationId(wrappedRequest, wrappedResponse, correlationId);

        Exception chainException = null;
        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } catch (ServletException | IOException e) {
            chainException = e;
            throw e;
        } finally {
            try {
                performAuditLogging(wrappedRequest, wrappedResponse, correlationId, chainException);
            } finally {
                wrappedResponse.copyBodyToResponse();
            }
        }
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith(apiPathPrefix);
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    private void setCorrelationId(HttpServletRequest request, HttpServletResponse response, String correlationId) {
        request.setAttribute(AwesomeConstants.CORRELATION_ID_ATTR, correlationId);
        response.setHeader(AwesomeConstants.CORRELATION_ID_HEADER, correlationId);
    }

    private void performAuditLogging(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            String correlationId,
            Exception chainException) {

        String responseBody = extractResponseBody(response);
        String requestBody = extractRequestBody(request);

        request.setAttribute(AwesomeConstants.RESPONSE_BODY_ATTR, responseBody);
        request.setAttribute(AwesomeConstants.REQUEST_BODY_ATTR, requestBody);

        try {
            ApiAuditLog auditLog = new ApiAuditLog(
                    correlationId,
                    LocalDateTime.now(),
                    request.getMethod(),
                    request.getRequestURI(),
                    requestBody,
                    response.getStatus(),
                    responseBody,
                    chainException != null ? chainException.getMessage() : null
            );

            auditLogService.log(auditLog);
            logger.debug("Saved audit log for correlationId={}", correlationId);
        } catch (Exception e) {
            logger.error("Failed to save audit log for correlationId={}", correlationId, e);
        }
    }

    private String extractResponseBody(ContentCachingResponseWrapper response) {

        try {
            byte[] buf = response.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, getCharset(response.getCharacterEncoding()));
            }
        } catch (Exception e) {
            logger.warn("Failed to extract response body", e);
        }

        return null;

    }

    private String extractRequestBody(ContentCachingRequestWrapper request) {

        try {

            String body = extractRawRequestBody(request);
            if (body != null) {
                return isFormUrlEncoded(request) ? decodeFormData(body, request) : body;
            }

            return extractFromQueryStringOrParams(request);

        } catch (Exception e) {
            logger.warn("Failed to extract request body", e);
            return null;
        }
    }

    private String extractRawRequestBody(ContentCachingRequestWrapper request) {

        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            return new String(buf, getCharset(request.getCharacterEncoding()));
        }
        return null;
    }

    private boolean isFormUrlEncoded(HttpServletRequest request) {

        String contentType = request.getContentType();
        return contentType != null && contentType.contains(AwesomeConstants.FORM_URLENCODED_CONTENT_TYPE);

    }

    private String decodeFormData(String body, HttpServletRequest request) {

        try {
            return java.net.URLDecoder.decode(body, getCharset(request.getCharacterEncoding()));
        } catch (Exception e) {
            return body; // Ritorno comunque il body raw in caso di errore
        }

    }

    private String extractFromQueryStringOrParams(HttpServletRequest request) {

        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            return decodeFormData(queryString, request);
        }

        return extractFromParameterMap(request);
    }

    private String extractFromParameterMap(HttpServletRequest request) {

        Map<String, String[]> params = request.getParameterMap();
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringJoiner joiner = new StringJoiner("&");
        params.forEach((key, values) -> {
            for (String value : values) {
                joiner.add(key + "=" + value);
            }
        });

        return joiner.toString();
    }

    private Charset getCharset(String encoding) {
        return encoding != null ? Charset.forName(encoding) : Charset.forName(AwesomeConstants.DEFAULT_CHARSET);
    }
}
