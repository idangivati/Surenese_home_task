package com.example.demo.dto;

import com.example.demo.model.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketStatusRequest {

    @NotNull(message = "Status is required")
    private TicketStatus status;
}