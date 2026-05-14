package com.example.demo.dto;

import com.example.demo.model.TicketStatus;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private String customerUsername;
}