package com.awesome.pizza.order.manager.constants;

public final class AwesomeConstants {

    private AwesomeConstants() {
        // Previene l'istanziazione, questa è una classe di sole costanti
    }

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String FORM_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_ATTR = "CORRELATION_ID";
    public static final String REQUEST_BODY_ATTR = "REQUEST_BODY";
    public static final String RESPONSE_BODY_ATTR = "RESPONSE_BODY";
}
