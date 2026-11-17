package com.shopsphere.service;

import com.shopsphere.dto.Dtos.*;
import com.shopsphere.entity.*;
import com.shopsphere.repo.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartItemRepository cart;
    private final UserRepository users;
    private final ProductRepository products;

    @Transactional(readOnly = true)
    public CartDto get(String email) { return build(cart.findByUser(user(email))); }

    public CartDto add(String email, CartAddRequest r) {
        User u = user(email);
        Product p = products.findById(r.productId()).filter(Product::isActive)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
        CartItem item = cart.findByUserAndProduct(u, p)
                .orElseGet(() -> CartItem.builder().user(u).product(p).quantity(0).build());
        int qty = item.getQuantity() + r.quantity();
        if (qty > p.getStock()) throw new ApiException(HttpStatus.BAD_REQUEST, "Only " + p.getStock() + " in stock");
        item.setQuantity(qty);
        cart.save(item);
        return build(cart.findByUser(u));
    }

    public CartDto remove(String email, Long productId) {
        User u = user(email);
        products.findById(productId).flatMap(p -> cart.findByUserAndProduct(u, p)).ifPresent(cart::delete);
        return build(cart.findByUser(u));
    }

    private CartDto build(List<CartItem> items) {
        List<CartItemDto> dtos = items.stream().map(i -> {
            Product p = i.getProduct();
            return new CartItemDto(p.getId(), p.getName(), p.getImageUrl(), p.getPrice(), i.getQuantity(),
                    p.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
        }).toList();
        return new CartDto(dtos, dtos.stream().map(CartItemDto::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private User user(String email) {
        return users.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    }
}
