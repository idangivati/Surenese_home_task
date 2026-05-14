package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.exception.ApiException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", request.getUsername());
                    return new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - wrong password for user: {}", request.getUsername());
            throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        log.info("Login successful for user: {} with role: {}", user.getUsername(), user.getRole());
        return jwtService.generateToken(user);
    }
}