package com.example.demo.repository;

import com.example.demo.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCustomerId(Long customerId);

    Page<Ticket> findByCustomerId(Long customerId, Pageable pageable);

    List<Ticket> findByCustomerAgentId(Long agentId);

    Page<Ticket> findByCustomerAgentId(Long agentId, Pageable pageable);

    Optional<Ticket> findByIdAndCustomerId(Long id, Long customerId);
}