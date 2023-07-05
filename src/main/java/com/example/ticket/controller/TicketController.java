package com.example.ticket.controller;

import com.example.ticket.dto.ticket.request.TicketRequest;
import com.example.ticket.dto.ticket.request.TicketReserveRequest;
import com.example.ticket.dto.ticket.response.TicketResponse;
import com.example.ticket.service.TicketService;
import com.example.ticket.util.exception.TicketSoldOutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket")
@Slf4j
public class TicketController {
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<String> reserveTicket(@RequestBody TicketReserveRequest ticketReserveRequest) throws TicketSoldOutException {
        log.info("POST " + ticketReserveRequest.getTicketId() + ", " + LocalDateTime.now());
        TicketResponse ticketResponse = ticketService.reserveTicket(ticketReserveRequest.getTicketId());
        return ResponseEntity.ok("티켓 예약 성공");
    }

    @GetMapping("/autoCreateTicket")
    private ResponseEntity<String> autoCreateTicket() {
        ticketService.save(new TicketRequest("티켓1", 2, 0));
        return ResponseEntity.ok("티켓 생성 성공");
    }
}
