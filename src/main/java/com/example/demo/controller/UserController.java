package com.example.demo.controller;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createCustomer(request, currentUser));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<UserResponse>> getMyCustomers(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userService.getMyCustomers(currentUser));
    }

    @GetMapping("/customers/paged")
    public ResponseEntity<Page<UserResponse>> getMyCustomersPaged(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getMyCustomersPaginated(currentUser, pageable));
    }

    @PutMapping("/agents/me")
    public ResponseEntity<UserResponse> updateAgentProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userService.updateProfile(currentUser, request));
    }

    @GetMapping("/customers/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(new UserResponse(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getRole()
        ));
    }

    @PutMapping("/customers/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userService.updateProfile(currentUser, request));
    }
}