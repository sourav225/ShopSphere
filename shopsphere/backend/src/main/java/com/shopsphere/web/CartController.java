package com.shopsphere.web;

import com.shopsphere.dto.Dtos.*;
import com.shopsphere.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cart;

    @GetMapping public CartDto get(Authentication a) { return cart.get(a.getName()); }
    @PostMapping public CartDto add(Authentication a, @Valid @RequestBody CartAddRequest r) { return cart.add(a.getName(), r); }
    @DeleteMapping("/{productId}") public CartDto remove(Authentication a, @PathVariable Long productId) { return cart.remove(a.getName(), productId); }
}
