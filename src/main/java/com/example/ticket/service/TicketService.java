package com.example.ticket.service;

import com.example.ticket.dto.ticket.request.TicketRequest;
import com.example.ticket.dto.ticket.response.TicketResponse;
import com.example.ticket.entity.Ticket;
import com.example.ticket.redis.repository.TicketRedisRepository;
import com.example.ticket.repository.TicketRepository;
import com.example.ticket.util.exception.TicketNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketRedisRepository ticketRedisRepository;

    public Long save(TicketRequest ticketRequest) {
        Long ticketId = ticketRepository.save(Ticket.builder()
                .ticketName(ticketRequest.getTicketName())
                .ticketMaxCount(ticketRequest.getTicketMaxCount())
                .build()).getId();

        ticketRedisRepository.createTicket(ticketId, ticketRequest.getTicketMaxCount());
        return ticketId;
    }

    public TicketResponse findById(Long id) {
        return TicketResponse.from(ticketRepository.findById(id).orElseThrow(TicketNotFoundException::new));
    }

    // step 0) 동시성 발생
    public void reserveTicketBasic(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);

        ticket.currentCountIncrement();

        log.info("잔여 티켓: " + (ticket.getTicketMaxCount() - ticket.getTicketCurrentCount()));
    }

    // step 1) Pessimistic Lock: 데이터를 읽거나 수정하기 전에 해당 데이터를 락으로 설정하는 방식
    @PersistenceContext
    private EntityManager entityManager;

    public void reverseTicketLock(Long ticketId) {
        Ticket ticket = entityManager.find(Ticket.class, ticketId, LockModeType.PESSIMISTIC_WRITE);

        ticket.currentCountIncrement();

        log.info("잔여 티켓: " + (ticket.getTicketMaxCount() - ticket.getTicketCurrentCount()));
    }

    // step 2, 3) Redisson, Kafka
    public void reserveTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(TicketNotFoundException::new);

        ticket.currentCountIncrement();

        log.info("잔여 티켓: " + (ticket.getTicketMaxCount() - ticket.getTicketCurrentCount()));
    }
}
