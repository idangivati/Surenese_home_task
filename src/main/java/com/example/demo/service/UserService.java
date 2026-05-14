package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.exception.ApiException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createCustomer(RegisterRequest request, User agent) {
        log.info("Agent {} creating customer {}", agent.getUsername(), request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username {} already exists", request.getUsername());
            throw new ApiException("Username already exists", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email {} already exists", request.getEmail());
            throw new ApiException("Email already exists", HttpStatus.CONFLICT);
        }

        User customer = new User();
        customer.setUsername(request.getUsername());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setEmail(request.getEmail());
        customer.setRole(Role.CUSTOMER);
        customer.setAgent(agent);

        userRepository.save(customer);
        log.info("Customer {} created successfully under agent {}", request.getUsername(), agent.getUsername());
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getMyCustomers(User agent) {
        log.info("Agent {} fetching their customers", agent.getUsername());
        return userRepository.findByAgentId(agent.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getMyCustomersPaginated(User agent, Pageable pageable) {
        log.info("Agent {} fetching customers page {}", agent.getUsername(), pageable.getPageNumber());
        return userRepository.findByAgentId(agent.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional
    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        log.info("User {} updating their profile", user.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        log.info("User {} profile updated successfully", user.getUsername());
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User {} not found", username);
                    return new ApiException("User not found", HttpStatus.NOT_FOUND);
                });
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}