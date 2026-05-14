package com.example.demo.controller;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/customers")
    public ResponseEntity<UserResponse> createCustomer(
            @Valid @RequestBody RegisterRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createCustomer(request, currentUser));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<UserResponse>> getMyCustomers(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getMyCustomers(currentUser));
    }

    @PutMapping("/agents/me")
    public ResponseEntity<UserResponse> updateAgentProfile(
            @Valid @RequestBody RegisterRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.updateProfile(currentUser, request));
    }

    @GetMapping("/customers/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(new UserResponse(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getRole()
        ));
    }

    @PutMapping("/customers/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody RegisterRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.updateProfile(currentUser, request));
    }
}