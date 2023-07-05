package com.example.ticket.service;

import com.example.ticket.dto.ticket.request.TicketRequest;
import com.example.ticket.dto.ticket.response.TicketResponse;
import com.example.ticket.entity.Ticket;
import com.example.ticket.repository.TicketRepository;
import com.example.ticket.util.exception.TicketNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketService {
    // step1: 데이터베이스 하나로 이 문제를 해결하기(데이터베이스의 기능을 활용) -> 데이터베이스에 많은 부담이 된다.. // 사용자가 증가할수록 데이터베이스에 CPU 터짐.. 60% & 서버에 과부하
    // step1 answer: database lock & mvcc
    // step2: 데이터베이스의 기능을 -> 외부 로직으로 분산시킨다  // 데이터베이스의 부담을 줄이는 과정.. & 서버에 과부하
    // step2 answer: 레디스의 분산락(redisson) with spring boot
    // step3: MSA 상황에서 어떻게 처리해야 하는가 // 서버의 부담을 줄이는 과정 & 데이터베이스의 과부하를 줄이는 것..
    // step3 answer: 순서 보장은 레디스 & 카프카를 활용해서 분산처리

    // other: 치킨 이벤트를 한다. step3까지 해도 터질 우려가 있음.. 대기열(redis)

    private final TicketRepository ticketRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public Long save(TicketRequest ticketRequest) {
        return ticketRepository.save(Ticket.builder()
                .ticketName(ticketRequest.getTicketName())
                .ticketMaxCount(ticketRequest.getTicketMaxCount())
                .ticketCurrentCount(ticketRequest.getTicketCurrentCount()).build()).getId();
    }

    public TicketResponse findById(Long id) {
        return TicketResponse.from(ticketRepository.findById(id).orElseThrow(TicketNotFoundException::new));
    }

    // step 0)
    public TicketResponse reserveTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);

        ticket.currentCountIncrement();

        log.info("잔여 티켓: " + (ticket.getTicketMaxCount() - ticket.getTicketCurrentCount()));

        return TicketResponse.from(ticket);
    }

//    public Ticket reserveTicket(Long ticketId) {
//        Ticket ticket = redisTemplate.opsForValue().get(ticketId.toString());
//
//        if (ticket == null) {
//            ticket = ticketRepository.findById(ticketId)
//                    .orElseThrow(TicketNotFoundException::new);
//
//            redisTemplate.opsForValue().set(ticketId.toString(), ticket);
//        }
//
//        ticket.currentCountIncrement();
//
//        redisTemplate.opsForValue().set(ticketId.toString(), ticket);
//
//        return ticket;
//    }

}
