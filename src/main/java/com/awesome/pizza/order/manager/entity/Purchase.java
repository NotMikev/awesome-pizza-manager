package com.awesome.pizza.order.manager.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "PURCHASE")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code; //UUID usato per semplicità
    private String pizza;
    private String status; //NEW, IN_PROGRESS, READY (soluzione più robusta mappare in Enum)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
