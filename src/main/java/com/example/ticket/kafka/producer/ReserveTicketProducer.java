package com.example.ticket.kafka.producer;

import com.example.ticket.dto.ticket.request.TicketReserveRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReserveTicketProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(TicketReserveRequest ticketRequest) {
        try {
            String jsonObject = objectMapper.writeValueAsString(ticketRequest);

            kafkaTemplate.send("reserve_ticket", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
