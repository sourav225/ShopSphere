package com.shopsphere.config;

import com.shopsphere.entity.*;
import com.shopsphere.repo.*;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final ProductRepository products;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (!users.existsByEmail("admin@shopsphere.com")) {
            users.save(User.builder().name("Admin").email("admin@shopsphere.com")
                    .password(encoder.encode("Admin@123")).role(User.Role.ADMIN).build());
        }
        if (products.count() == 0) {
            seed("Wireless Headphones", "Noise-cancelling over-ear headphones", "129.99", 40, "Electronics");
            seed("Mechanical Keyboard", "Hot-swappable 75% keyboard", "89.50", 25, "Electronics");
            seed("Running Shoes", "Lightweight daily trainers", "74.00", 60, "Fashion");
            seed("Backpack 25L", "Water-resistant commuter backpack", "49.99", 80, "Fashion");
            seed("Coffee Grinder", "Burr grinder, 15 settings", "39.90", 35, "Home");
            seed("Desk Lamp", "LED lamp with adjustable colour temperature", "24.99", 100, "Home");
        }
    }

    private void seed(String name, String desc, String price, int stock, String category) {
        products.save(Product.builder().name(name).description(desc).price(new BigDecimal(price))
                .stock(stock).category(category).imageUrl("").build());
    }
}
