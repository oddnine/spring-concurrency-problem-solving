package com.example.ticket.kafka;

import com.example.ticket.dto.ticket.request.TicketReserveRequest;
import com.example.ticket.kafka.producer.ReserveTicketProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketKafkaService {
    private final ReserveTicketProducer reserveTicketProducer;

    public void sendTicketReserve(Long ticketId) {
        TicketReserveRequest ticket = new TicketReserveRequest();
        ticket.setTicketId(ticketId);
        reserveTicketProducer.send(ticket);
    }
}
