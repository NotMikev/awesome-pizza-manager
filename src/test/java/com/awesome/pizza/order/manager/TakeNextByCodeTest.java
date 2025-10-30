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
public class TakeNextByCodeTest {

    private final TestRestTemplate restTemplate;
    private final PurchaseRepository purchaseRepository;

    private String firstPurchaseCode;
    private String secondPurchaseCode;

    public TakeNextByCodeTest(
            @Autowired TestRestTemplate restTemplate,
            @Autowired PurchaseRepository purchaseRepository) {
        this.restTemplate = restTemplate;
        this.purchaseRepository = purchaseRepository;
    }

    @BeforeEach
    void setUp() {
        purchaseRepository.deleteAll();
        firstPurchaseCode = null;
        secondPurchaseCode = null;
    }

    @Test
    void takeSpecificPurchaseTest() {

        //Step 1: Creo due purchase
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("pizza", "Margherita");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<PurchaseDto> createFirstResponse = restTemplate.postForEntity(
                "/api/purchase",
                request,
                PurchaseDto.class
        );

        assertThat(createFirstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createFirstResponse.getBody()).isNotNull();
        assertThat(createFirstResponse.getBody().getStatus()).isEqualTo("NEW");
        firstPurchaseCode = createFirstResponse.getBody().getCode();

        form = new LinkedMultiValueMap<>();
        form.add("pizza", "Marinara");
        request = new HttpEntity<>(form, headers);

        ResponseEntity<PurchaseDto> createSecondResponse = restTemplate.postForEntity(
                "/api/purchase",
                request,
                PurchaseDto.class
        );

        assertThat(createSecondResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createSecondResponse.getBody()).isNotNull();
        assertThat(createSecondResponse.getBody().getStatus()).isEqualTo("NEW");
        secondPurchaseCode = createSecondResponse.getBody().getCode();

        // Step 2: Recupero il secondo purchase by codice
        ResponseEntity<PurchaseDto> takeSpecificResponse = restTemplate.postForEntity(
                "/api/purchase/next/" + secondPurchaseCode,
                null,
                PurchaseDto.class
        );

        assertThat(takeSpecificResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(takeSpecificResponse.getBody()).isNotNull();
        assertThat(takeSpecificResponse.getBody().getCode()).isEqualTo(secondPurchaseCode);
        assertThat(takeSpecificResponse.getBody().getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(takeSpecificResponse.getBody().getPizza()).isEqualTo("Marinara");

        // Step 4: Testo richiesta recupero purchase non esistente
        ResponseEntity<PurchaseDto> takeNonExistentResponse = restTemplate.postForEntity(
                "/api/purchase/next/non-existent-code",
                null,
                PurchaseDto.class
        );

        assertThat(takeNonExistentResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
