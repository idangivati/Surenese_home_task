package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    private String password; // optional - only update if provided
}