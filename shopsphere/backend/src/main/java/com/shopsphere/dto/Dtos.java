package com.shopsphere.dto;

import com.shopsphere.entity.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class Dtos {
    private Dtos() {}

    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email, @Size(min = 6) String password) {}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record AuthResponse(String token, String email, String name, String role) {}

    public record ProductRequest(@NotBlank String name, String description,
                                 @NotNull @DecimalMin("0.0") BigDecimal price,
                                 @Min(0) int stock, String category, String imageUrl) {}
    public record ProductDto(Long id, String name, String description, BigDecimal price, int stock,
                             String category, String imageUrl) implements Serializable {}
    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages)
            implements Serializable {}

    public record CartAddRequest(@NotNull Long productId, @Min(1) int quantity) {}
    public record CartItemDto(Long productId, String name, String imageUrl, BigDecimal price, int quantity, BigDecimal subtotal) {}
    public record CartDto(List<CartItemDto> items, BigDecimal total) {}

    public record OrderItemDto(Long productId, String productName, BigDecimal unitPrice, int quantity) {}
    public record OrderDto(Long id, String userEmail, BigDecimal total, String status, String paymentStatus,
                           Instant createdAt, List<OrderItemDto> items) {}
    public record StatusRequest(@NotBlank String status) {}
    public record StatsDto(long orders, long products, long users, BigDecimal revenue, Map<String, Long> ordersByStatus) {}

    public static ProductDto toDto(Product p) {
        return new ProductDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(),
                p.getCategory(), p.getImageUrl());
    }

    public static OrderDto toDto(Order o) {
        return new OrderDto(o.getId(), o.getUser().getEmail(), o.getTotal(), o.getStatus().name(),
                o.getPaymentStatus().name(), o.getCreatedAt(),
                o.getItems().stream()
                        .map(i -> new OrderItemDto(i.getProductId(), i.getProductName(), i.getUnitPrice(), i.getQuantity()))
                        .toList());
    }
}
