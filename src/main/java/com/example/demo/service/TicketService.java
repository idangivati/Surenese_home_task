package com.example.demo.service;

import com.example.demo.dto.TicketRequest;
import com.example.demo.dto.TicketResponse;
import com.example.demo.model.Ticket;
import com.example.demo.model.TicketStatus;
import com.example.demo.model.User;
import com.example.demo.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    // CUSTOMER creates a ticket
    public TicketResponse createTicket(TicketRequest request, User customer) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCustomer(customer);

        ticketRepository.save(ticket);
        return toResponse(ticket);
    }

    // CUSTOMER gets their own tickets
    public List<TicketResponse> getMyTickets(User customer) {
        return ticketRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // AGENT gets all tickets from their customers
    public List<TicketResponse> getTicketsByAgent(User agent) {
        return ticketRepository.findByCustomerAgentId(agent.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Convert Ticket to TicketResponse DTO
    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getCreatedAt(),
                ticket.getCustomer().getUsername()
        );
    }
}