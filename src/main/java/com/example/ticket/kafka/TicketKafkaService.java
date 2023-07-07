package com.example.ticket.kafka;

import com.example.ticket.dto.ticket.request.TicketReserveRequest;
import com.example.ticket.kafka.producer.ReserveTicketProducer;
import com.example.ticket.redis.repository.TicketRedisRepository;
import com.example.ticket.util.exception.TicketSoldOutException;
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
    private final TicketRedisRepository ticketRedisRepository;

    public void sendReserveTicket(Long ticketId) {
        reserveTicketProducer.send(new TicketReserveRequest(ticketId));
    }

    public void sendReserveTicketWithRedis(Long ticketId) {
        Long remainTicketCount = ticketRedisRepository.decreaseTicketCount(ticketId);

        log.info("remain count: " + remainTicketCount);

        if (remainTicketCount < 0) {
            throw new TicketSoldOutException();
        }

        reserveTicketProducer.send(new TicketReserveRequest(ticketId));
    }
}
