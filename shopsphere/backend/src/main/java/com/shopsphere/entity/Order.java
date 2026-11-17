package com.shopsphere.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity @Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    public enum Status { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
    public enum PaymentStatus { PENDING, PAID, FAILED, REFUNDED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private User user;
    @Column(precision = 12, scale = 2) private BigDecimal total;
    @Enumerated(EnumType.STRING) private Status status;
    @Enumerated(EnumType.STRING) private PaymentStatus paymentStatus;
    private Instant createdAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<OrderItem> items = new ArrayList<>();
}
