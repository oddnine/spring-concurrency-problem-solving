package com.example.ticket.controller;

import com.example.ticket.dto.ticket.request.TicketRequest;
import com.example.ticket.dto.ticket.request.TicketReserveRequest;
import com.example.ticket.kafka.TicketKafkaService;
import com.example.ticket.redis.service.TicketReserveRedissonService;
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
    // step 1: 데이터베이스 하나로 이 문제를 해결하기(데이터베이스의 기능을 활용) -> 데이터베이스에 많은 부담이 된다.. // 사용자가 증가할수록 데이터베이스에 CPU 터짐.. 60% & 서버에 과부하
    // step 1 answer: database lock & mvcc

    // step 2: 데이터베이스의 기능을 -> 외부 로직으로 분산시킨다 // 데이터베이스의 부담을 줄이는 과정.. & 서버에 과부하
    // step 2 answer: 레디스의 분산락(redisson) with spring boot

    // step 3: MSA 상황에서 어떻게 처리해야 하는가 // 서버의 부담을 줄이는 과정 & 데이터베이스의 과부하를 줄이는 것..
    // step 3 answer: 순서 보장은 레디스 & 카프카를 활용해서 분산처리

    // step 4: 컨트롤러에서 수량 관리(서비스 단 부담 낮추기)

    // other: 치킨 이벤트를 한다. step3까지 해도 터질 우려가 있음.. 대기열(redis)

    private final TicketService ticketService;
    private final TicketReserveRedissonService ticketReserveRedissonService;
    private final TicketKafkaService ticketKafkaService;

    // step 1) 데이터 베이스 락
    @PostMapping("/lock")
    public ResponseEntity<String> reserveTicket(@RequestBody TicketReserveRequest ticketReserveRequest) throws TicketSoldOutException {
        log.info("POST " + ticketReserveRequest.getTicketId() + ", " + LocalDateTime.now());
        ticketService.reverseTicketLock(ticketReserveRequest.getTicketId());
        return ResponseEntity.ok("티켓 예약 성공");
    }

    // step 2) 레디스 분산락(redisson)
    @PostMapping("/redis")
    public ResponseEntity<String> reserveTicketWithRedis(@RequestBody TicketReserveRequest ticketReserveRequest) throws TicketSoldOutException {
        log.info("POST " + ticketReserveRequest.getTicketId() + ", " + LocalDateTime.now());
        ticketReserveRedissonService.reverseTicket(ticketReserveRequest.getTicketId());
        return ResponseEntity.ok("티켓 예약 성공");
    }

    // step 3) 레디스 분산락 + 카프카
    @PostMapping("/kafka")
    public ResponseEntity<String> reserveTicketWithRedisAndKafka(@RequestBody TicketReserveRequest ticketReserveRequest) throws TicketSoldOutException {
        log.info("POST " + ticketReserveRequest.getTicketId() + ", " + LocalDateTime.now());
        ticketKafkaService.sendReserveTicket(ticketReserveRequest.getTicketId());
        return ResponseEntity.ok("티켓 예약 신청 완료");
    }

    // step 4) 레디스 분산 락을 사용해서 레디스에서 잔여 표 확인 및 카운팅 후 카프카 메시징 처리
    @PostMapping("/kafkaAndRedis")
    public ResponseEntity<String> reserveTicketWithRedisAndKafkaV2(@RequestBody TicketReserveRequest ticketReserveRequest) throws TicketSoldOutException {
        log.info("POST " + ticketReserveRequest.getTicketId() + ", " + LocalDateTime.now());
        ticketReserveRedissonService.sendReserveTicketWithRedisOnKafka(ticketReserveRequest.getTicketId());
        return ResponseEntity.ok("티켓 예약 신청 완료");
    }

    // 티켓 생성
    @PostMapping
    private ResponseEntity<String> autoCreateTicket(@RequestBody TicketRequest ticketRequest) {
        ticketService.save(ticketRequest);
        return ResponseEntity.ok("티켓 생성 성공");
    }
}
