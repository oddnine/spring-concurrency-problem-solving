package com.example.ticket.service;

import com.example.ticket.dto.ticket.request.TicketRequest;
import com.example.ticket.dto.ticket.response.TicketResponse;
import com.example.ticket.kafka.TicketKafkaService;
import com.example.ticket.redis.service.TicketReserveRedissonService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
class TicketServiceTest {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private TicketReserveRedissonService ticketReserveRedissonService;
    @Autowired
    private TicketKafkaService ticketKafkaService;

    private Long id1 = null;
    private Long id2 = null;
    private final Integer maxCount = 100;

    @BeforeEach
    public void before() {
        TicketRequest ticket1 = new TicketRequest("ticket1", maxCount);
        id1 = ticketService.save(ticket1);
        TicketRequest ticket2 = new TicketRequest("ticket2", maxCount);
        id2 = ticketService.save(ticket2);
    }

    @Test
    void 동시성_문제_LOCK() throws InterruptedException {
        int peopleCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch tt = new CountDownLatch(peopleCount);

        for (int i = 0; i < peopleCount; i++) {
            executorService.execute(() -> {
                ticketService.reverseTicketLock(id1);
                tt.countDown();
            });
        }

        tt.await();

        TicketResponse ticket = ticketService.findById(id1);
        Assertions.assertThat(ticket.getTicketCurrentCount()).isEqualTo(maxCount);
    }

    @Test
    void 동시성_문제_with_Redis() throws InterruptedException {
        int peopleCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch tt = new CountDownLatch(peopleCount);

        for (int i = 0; i < peopleCount; i++) {
            executorService.execute(() -> {
                ticketReserveRedissonService.reverseTicket(id1);
                ticketReserveRedissonService.reverseTicket(id2);
                tt.countDown();
            });
        }

        tt.await();

        TicketResponse ticket = ticketService.findById(id1);
        Assertions.assertThat(ticket.getTicketCurrentCount()).isEqualTo(maxCount);
    }

    @Test
    void 동시성_문제_with_Kafka() throws InterruptedException {
        int peopleCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch tt = new CountDownLatch(peopleCount);

        for (int i = 0; i < peopleCount; i++) {
            executorService.execute(() -> {
                ticketKafkaService.sendReserveTicket(id1);
                tt.countDown();
            });
        }

        tt.await();

        Thread.sleep(2000);

        TicketResponse ticket = ticketService.findById(id1);
        Assertions.assertThat(ticket.getTicketCurrentCount()).isEqualTo(maxCount);
    }

    @Test
    void 동시성_문제_with_Redis_And_Kafka() throws InterruptedException {
        int peopleCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch tt = new CountDownLatch(peopleCount);

        for (int i = 0; i < peopleCount; i++) {
            executorService.execute(() -> {
                ticketReserveRedissonService.sendReserveTicketWithRedisOnKafka(id1);
                tt.countDown();
            });
        }

        tt.await();

        Thread.sleep(2000);

        TicketResponse ticket = ticketService.findById(id1);
        Assertions.assertThat(ticket.getTicketCurrentCount()).isEqualTo(maxCount);
    }
}