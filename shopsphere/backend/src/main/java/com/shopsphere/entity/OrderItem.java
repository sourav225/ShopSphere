package com.shopsphere.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Order order;
    private Long productId;
    private String productName;
    @Column(precision = 12, scale = 2) private BigDecimal unitPrice;
    private int quantity;
}
