package com.example.ticket.kafka;

import com.example.ticket.dto.ticket.request.TicketReserveRequest;
import com.example.ticket.kafka.producer.ReserveTicketProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TicketKafkaService {
    private final ReserveTicketProducer reserveTicketProducer;

    public void sendReserveTicket(Long ticketId) {
        TicketReserveRequest ticket = new TicketReserveRequest();
        ticket.setTicketId(ticketId);
        reserveTicketProducer.send(ticket);
    }
}
