package com.example.ticket.service;

import com.example.ticket.dto.ticket.request.TicketRequest;
import com.example.ticket.dto.ticket.response.TicketResponse;
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

    private Long id = null;

    @BeforeEach
    public void before() {
        TicketRequest ticket = new TicketRequest("ticket1", 100, 0);
        id = ticketService.save(ticket);
    }

    @Test
    void 동시성_문제_step_0() throws InterruptedException {
        int peopleCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch tt = new CountDownLatch(peopleCount);

        for (int i = 0; i < peopleCount; i++) {
            executorService.execute(() -> {
                ticketService.reserveTicket(id);
                tt.countDown();
            });
        }

        tt.await();

        TicketResponse ticket = ticketService.findById(id);
        Assertions.assertThat(ticket.getTicketCurrentCount()).isEqualTo(peopleCount);
    }
}