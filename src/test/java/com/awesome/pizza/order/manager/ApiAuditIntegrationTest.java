package com.awesome.pizza.order.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.awesome.pizza.order.manager.entity.ApiAuditLog;
import com.awesome.pizza.order.manager.repository.ApiAuditLogRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiAuditIntegrationTest {

    private final TestRestTemplate restTemplate;
    private final ApiAuditLogRepository auditRepo;

    public ApiAuditIntegrationTest(
            @Autowired TestRestTemplate restTemplate,
            @Autowired ApiAuditLogRepository auditRepo) {
        this.restTemplate = restTemplate;
        this.auditRepo = auditRepo;
    }

    @Test
    void auditPersistsRequestAndResponse() throws Exception {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("pizza", "TestPizza");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity("/api/purchase", request, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String correlationId = resp.getHeaders().getFirst("X-Correlation-Id");
        assertThat(correlationId).isNotEmpty();

        Optional<ApiAuditLog> auditOpt = auditRepo.findByCorrelationId(correlationId);
        assertThat(auditOpt).isPresent();

        ApiAuditLog audit = auditOpt.get();
        assertThat(audit.getRequestBody()).isNotNull();
        assertThat(audit.getRequestBody()).contains("TestPizza");
        assertThat(audit.getResponseBody()).isNotNull();
    }
}
