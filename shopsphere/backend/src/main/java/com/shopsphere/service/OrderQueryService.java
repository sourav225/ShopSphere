package com.shopsphere.service;

import com.shopsphere.dto.Dtos;
import com.shopsphere.dto.Dtos.*;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.User;
import com.shopsphere.repo.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read side for orders and the admin dashboard. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {
    private final OrderRepository orders;
    private final UserRepository users;
    private final ProductRepository products;

    public List<OrderDto> mine(String email) {
        User u = users.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unknown user"));
        return orders.findByUserOrderByCreatedAtDesc(u).stream().map(Dtos::toDto).toList();
    }

    public List<OrderDto> all() { return orders.findAllByOrderByCreatedAtDesc().stream().map(Dtos::toDto).toList(); }

    public StatsDto stats() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Order.Status s : Order.Status.values()) byStatus.put(s.name(), orders.countByStatus(s));
        return new StatsDto(orders.count(), products.countByActiveTrue(), users.count(),
                orders.revenue(Order.PaymentStatus.PAID), byStatus);
    }
}
