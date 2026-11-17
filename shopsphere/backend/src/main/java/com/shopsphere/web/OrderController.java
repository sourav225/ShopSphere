package com.shopsphere.web;

import com.shopsphere.dto.Dtos.OrderDto;
import com.shopsphere.service.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderCommandService command;
    private final OrderQueryService query;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public OrderDto place(Authentication a) { return command.placeOrder(a.getName()); }

    @GetMapping public List<OrderDto> mine(Authentication a) { return query.mine(a.getName()); }

    @PostMapping("/{id}/pay")
    public OrderDto pay(Authentication a, @PathVariable Long id, @RequestParam(defaultValue = "true") boolean success) {
        return command.pay(id, a.getName(), success);
    }
}
