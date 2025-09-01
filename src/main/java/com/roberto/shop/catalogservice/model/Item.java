package com.roberto.shop.catalogservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=160)
    private String name;

    @Column(length=500)
    private String description;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal price;

    @Column(nullable=false)
    private Integer stock;

    @Column(nullable=false, length=100)
    private String category;

    @Column(nullable=false)
    private Instant updatedAt;

    @PrePersist @PreUpdate
    public void touch() { this.updatedAt = Instant.now(); }
}
