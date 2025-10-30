package com.awesome.pizza.order.manager.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.awesome.pizza.order.manager.dto.purchase.PurchaseDto;
import com.awesome.pizza.order.manager.entity.Purchase;
import com.awesome.pizza.order.manager.exception.PurchaseNotFoundException;
import com.awesome.pizza.order.manager.mapper.purchase.PurchaseMapper;
import com.awesome.pizza.order.manager.repository.PurchaseRepository;

@Service
public class PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;

    public PurchaseService(PurchaseRepository purchaseRepository, PurchaseMapper purchaseMapper) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseMapper = purchaseMapper;
    }

    public PurchaseDto createPurchase(String pizza) {

        logger.debug("createPurchase called with pizza={}", pizza);

        Purchase purchase = new Purchase();

        purchase.setCode(UUID.randomUUID().toString());
        purchase.setPizza(pizza);
        purchase.setStatus("NEW");
        purchase.setCreatedAt(LocalDateTime.now());
        purchase.setUpdatedAt(LocalDateTime.now());

        Purchase saved = purchaseRepository.save(purchase);
        PurchaseDto dto = purchaseMapper.toDto(saved);

        logger.debug("createPurchase returning={}", dto);

        return dto;
    }

    public PurchaseDto takeNextPurchase() {

        logger.debug("takeNextPurchase called");

        Purchase purchase = purchaseRepository
                .findFirstByStatusOrderByCreatedAtAsc("NEW")
                .orElseThrow(() -> new PurchaseNotFoundException("No purchase in NEW status found"));

        purchase.setStatus("IN_PROGRESS");
        purchase.setUpdatedAt(LocalDateTime.now());

        Purchase saved = purchaseRepository.save(purchase);
        PurchaseDto dto = purchaseMapper.toDto(saved);

        logger.debug("takeNextPurchase returning={}", dto);

        return dto;
    }

    public PurchaseDto takeNextPurchaseByCode(String code) {

        logger.debug("takeNextPurchaseByCode called with code={}", code);

        Purchase purchase = purchaseRepository
                .findByCodeAndStatus(code, "NEW")
                .orElseThrow(() -> new PurchaseNotFoundException("No purchase in NEW status found with code: " + code));

        purchase.setStatus("IN_PROGRESS");
        purchase.setUpdatedAt(LocalDateTime.now());

        Purchase saved = purchaseRepository.save(purchase);
        PurchaseDto dto = purchaseMapper.toDto(saved);

        logger.debug("takeNextPurchaseByCode returning={}", dto);

        return dto;
    }

    public PurchaseDto markPurchaseReady(String code) {

        logger.debug("markPurchaseReady called with code={}", code);

        Purchase purchase = purchaseRepository
                .findByCodeAndStatus(code, "IN_PROGRESS")
                .orElseThrow(() -> new PurchaseNotFoundException("No purchase with status IN_PROGRESS found with code: " + code));

        purchase.setStatus("READY");
        purchase.setUpdatedAt(LocalDateTime.now());

        Purchase saved = purchaseRepository.save(purchase);
        PurchaseDto dto = purchaseMapper.toDto(saved);

        logger.debug("markPurchaseReady returning={}", dto);

        return dto;
    }

    public PurchaseDto checkPurchaseStatusByCode(String code) {

        logger.debug("checkPurchaseStatusByCode called with code={}", code);

        Purchase purchase = purchaseRepository
                .findByCode(code)
                .orElseThrow(() -> new PurchaseNotFoundException("No purchase found by code: " + code));
        PurchaseDto dto = purchaseMapper.toDto(purchase);

        logger.debug("checkPurchaseStatusByCode returning={}", dto);

        return dto;
    }
}
