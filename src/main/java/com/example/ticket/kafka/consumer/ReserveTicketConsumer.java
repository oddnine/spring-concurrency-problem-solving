package com.example.ticket.kafka.consumer;

import com.example.ticket.dto.ticket.request.TicketReserveRequest;
import com.example.ticket.redis.service.TicketReserveRedissonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveTicketConsumer {
    private final TicketReserveRedissonService ticketReserveRedissonService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "reserve_ticket", groupId = "ticket")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            var ticket = objectMapper.readValue(record.value(), TicketReserveRequest.class);

            ticketReserveRedissonService.ticketReserve(ticket.getTicketId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
