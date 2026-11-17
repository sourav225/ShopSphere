package com.shopsphere.service;

import com.shopsphere.dto.Dtos.*;
import com.shopsphere.entity.User;
import com.shopsphere.repo.UserRepository;
import com.shopsphere.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthResponse register(RegisterRequest r) {
        if (users.existsByEmail(r.email())) throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        User u = users.save(User.builder().name(r.name()).email(r.email())
                .password(encoder.encode(r.password())).role(User.Role.USER).build());
        return toResponse(u);
    }

    public AuthResponse login(LoginRequest r) {
        User u = users.findByEmail(r.email())
                .filter(x -> encoder.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return toResponse(u);
    }

    private AuthResponse toResponse(User u) {
        return new AuthResponse(jwt.generate(u.getEmail(), u.getRole().name()), u.getEmail(), u.getName(), u.getRole().name());
    }
}
