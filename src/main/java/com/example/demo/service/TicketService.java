package com.example.demo.service;

import com.example.demo.dto.TicketRequest;
import com.example.demo.dto.TicketResponse;
import com.example.demo.dto.TicketStatusRequest;
import com.example.demo.exception.ApiException;
import com.example.demo.model.Ticket;
import com.example.demo.model.TicketStatus;
import com.example.demo.model.User;
import com.example.demo.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public TicketResponse createTicket(TicketRequest request, User customer) {
        log.info("Customer {} creating ticket: {}", customer.getUsername(), request.getTitle());
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCustomer(customer);

        ticketRepository.save(ticket);
        log.info("Ticket {} created successfully", ticket.getId());
        return toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getMyTickets(User customer) {
        log.info("Customer {} fetching their tickets", customer.getUsername());
        return ticketRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> getMyTicketsPaginated(User customer, Pageable pageable) {
        log.info("Customer {} fetching tickets page {}", customer.getUsername(), pageable.getPageNumber());
        return ticketRepository.findByCustomerId(customer.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByAgent(User agent) {
        log.info("Agent {} fetching all their customers tickets", agent.getUsername());
        return ticketRepository.findByCustomerAgentId(agent.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> getTicketsByAgentPaginated(User agent, Pageable pageable) {
        log.info("Agent {} fetching tickets page {}", agent.getUsername(), pageable.getPageNumber());
        return ticketRepository.findByCustomerAgentId(agent.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional
    public TicketResponse updateTicketStatus(Long ticketId, TicketStatusRequest request, User agent) {
        log.info("Agent {} updating ticket {} status to {}", agent.getUsername(), ticketId, request.getStatus());

        Ticket ticket = ticketRepository.findByCustomerAgentId(agent.getId())
                .stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new ApiException("Ticket not found or not accessible", HttpStatus.NOT_FOUND));

        ticket.setStatus(request.getStatus());
        ticketRepository.save(ticket);
        log.info("Ticket {} status updated to {}", ticketId, request.getStatus());
        return toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long ticketId, User customer) {
        Ticket ticket = ticketRepository.findByIdAndCustomerId(ticketId, customer.getId())
                .orElseThrow(() -> new ApiException("Ticket not found", HttpStatus.NOT_FOUND));
        return toResponse(ticket);
    }

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