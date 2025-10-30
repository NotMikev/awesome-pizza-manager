package com.awesome.pizza.order.manager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.awesome.pizza.order.manager.dto.purchase.PurchaseDto;

import com.awesome.pizza.order.manager.repository.PurchaseRepository;
import com.awesome.pizza.order.manager.service.PurchaseService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PurchaseFlowTest {

    private final TestRestTemplate restTemplate;
    private final PurchaseRepository purchaseRepository;

    private String purchaseCode;

    public PurchaseFlowTest(
            @Autowired TestRestTemplate restTemplate,
            @Autowired PurchaseRepository purchaseRepository) {
        this.restTemplate = restTemplate;
        this.purchaseRepository = purchaseRepository;
    }

    @BeforeEach
    void setUp() {
        purchaseRepository.deleteAll();
        purchaseCode = null;
    }

    @Test
    void completePurchaseFlowTest() {

        // Step 1: Creo nuovo purchase
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("pizza", "Margherita");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<PurchaseDto> createResponse = restTemplate.postForEntity(
                "/api/purchase",
                request,
                PurchaseDto.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getStatus()).isEqualTo("NEW");

        purchaseCode = createResponse.getBody().getCode();

        // Step 2: Verifica stato iniziale
        ResponseEntity<PurchaseDto> statusResponse = restTemplate.getForEntity(
                "/api/purchase/" + purchaseCode,
                PurchaseDto.class
        );

        assertThat(statusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(statusResponse.getBody()).isNotNull();
        assertThat(statusResponse.getBody().getStatus()).isEqualTo("NEW");

        // Step 3: Prendi il prossimo ordine
        ResponseEntity<PurchaseDto> nextOrderResponse = restTemplate.postForEntity(
                "/api/purchase/next",
                null,
                PurchaseDto.class
        );

        assertThat(nextOrderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(nextOrderResponse.getBody()).isNotNull();
        assertThat(nextOrderResponse.getBody().getCode()).isEqualTo(purchaseCode);
        assertThat(nextOrderResponse.getBody().getStatus()).isEqualTo("IN_PROGRESS");

        // Step 4: Verifica stato IN_PROGRESS
        statusResponse = restTemplate.getForEntity(
                "/api/purchase/" + purchaseCode,
                PurchaseDto.class
        );

        assertThat(statusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(statusResponse.getBody()).isNotNull();
        assertThat(statusResponse.getBody().getStatus()).isEqualTo("IN_PROGRESS");

        // Step 5: Emetti ordine come pronto
        ResponseEntity<PurchaseDto> readyResponse = restTemplate.postForEntity(
                "/api/purchase/" + purchaseCode + "/ready",
                null,
                PurchaseDto.class
        );

        assertThat(readyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readyResponse.getBody()).isNotNull();
        assertThat(readyResponse.getBody().getStatus()).isEqualTo("READY");

        // Step 6: Verifica stato finale
        statusResponse = restTemplate.getForEntity(
                "/api/purchase/" + purchaseCode,
                PurchaseDto.class
        );

        assertThat(statusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(statusResponse.getBody()).isNotNull();
        assertThat(statusResponse.getBody().getStatus()).isEqualTo("READY");
    }
}
