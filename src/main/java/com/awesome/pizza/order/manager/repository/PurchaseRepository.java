package com.awesome.pizza.order.manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awesome.pizza.order.manager.entity.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findFirstByStatusOrderByCreatedAtAsc(String status);

    Optional<Purchase> findByCodeAndStatus(String code, String status);

    Optional<Purchase> findByCode(String code);

    Optional<List<Purchase>> findByStatusOrderByCreatedAtAsc(String status);

}
