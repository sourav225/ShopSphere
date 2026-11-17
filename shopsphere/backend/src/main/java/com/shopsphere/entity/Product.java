package com.shopsphere.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(length = 2000) private String description;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    private int stock;
    private String category;
    private String imageUrl;
    @Builder.Default private boolean active = true;
}
