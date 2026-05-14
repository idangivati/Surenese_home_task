package com.example.demo.controller;

import com.example.demo.dto.TicketRequest;
import com.example.demo.dto.TicketResponse;
import com.example.demo.dto.TicketStatusRequest;
import com.example.demo.model.User;
import com.example.demo.service.TicketService;
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

    @GetMapping("/tickets/paged")
    public ResponseEntity<Page<TicketResponse>> getMyTicketsPaged(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ticketService.getMyTicketsPaginated(currentUser, pageable));
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ticketService.getTicketById(id, currentUser));
    }

    @GetMapping("/tickets/agent")
    public ResponseEntity<List<TicketResponse>> getAgentTickets(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ticketService.getTicketsByAgent(currentUser));
    }

    @GetMapping("/tickets/agent/paged")
    public ResponseEntity<Page<TicketResponse>> getAgentTicketsPaged(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ticketService.getTicketsByAgentPaginated(currentUser, pageable));
    }

    @PatchMapping("/tickets/{id}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long id,
            @Valid @RequestBody TicketStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, request, currentUser));
    }
}