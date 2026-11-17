package com.shopsphere.service;

import com.shopsphere.dto.Dtos;
import com.shopsphere.dto.Dtos.OrderDto;
import com.shopsphere.entity.*;
import com.shopsphere.repo.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/** Write side for orders. SERIALIZABLE isolation prevents overselling under concurrent checkouts. */
@Service
@RequiredArgsConstructor
public class OrderCommandService {
    private final OrderRepository orders;
    private final CartItemRepository cart;
    private final UserRepository users;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Caching(evict = {@CacheEvict(cacheNames = "products", allEntries = true), @CacheEvict(cacheNames = "product", allEntries = true)})
    public OrderDto placeOrder(String email) {
        User u = users.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unknown user"));
        List<CartItem> items = cart.findByUser(u);
        if (items.isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, "Cart is empty");

        Order order = Order.builder().user(u).status(Order.Status.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING).createdAt(Instant.now()).total(BigDecimal.ZERO).build();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : items) {
            Product p = ci.getProduct();
            if (!p.isActive() || p.getStock() < ci.getQuantity())
                throw new ApiException(HttpStatus.CONFLICT, "Insufficient stock for " + p.getName());
            p.setStock(p.getStock() - ci.getQuantity());
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
            order.getItems().add(OrderItem.builder().order(order).productId(p.getId()).productName(p.getName())
                    .unitPrice(p.getPrice()).quantity(ci.getQuantity()).build());
        }
        order.setTotal(total);
        orders.save(order);
        cart.deleteAll(items);
        return Dtos.toDto(order);
    }

    /** Mock payment gateway callback: marks the order PAID/FAILED. */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderDto pay(Long orderId, String email, boolean success) {
        Order o = orders.findById(orderId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!o.getUser().getEmail().equals(email)) throw new ApiException(HttpStatus.FORBIDDEN, "Not your order");
        if (o.getPaymentStatus() == Order.PaymentStatus.PAID) throw new ApiException(HttpStatus.BAD_REQUEST, "Already paid");
        o.setPaymentStatus(success ? Order.PaymentStatus.PAID : Order.PaymentStatus.FAILED);
        if (success) o.setStatus(Order.Status.CONFIRMED);
        return Dtos.toDto(o);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderDto updateStatus(Long orderId, String status) {
        Order o = orders.findById(orderId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
        try { o.setStatus(Order.Status.valueOf(status.toUpperCase())); }
        catch (IllegalArgumentException e) { throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status"); }
        return Dtos.toDto(o);
    }
}
