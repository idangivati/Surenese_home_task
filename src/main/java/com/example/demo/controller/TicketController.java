package com.example.demo.controller;

import com.example.demo.dto.TicketRequest;
import com.example.demo.dto.TicketResponse;
import com.example.demo.model.User;
import com.example.demo.service.TicketService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.createTicket(request, currentUser));
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponse>> getMyTickets(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ticketService.getMyTickets(currentUser));
    }

    @GetMapping("/tickets/agent")
    public ResponseEntity<List<TicketResponse>> getAgentTickets(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ticketService.getTicketsByAgent(currentUser));
    }
}