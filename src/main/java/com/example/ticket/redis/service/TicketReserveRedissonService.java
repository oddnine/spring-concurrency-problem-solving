package com.example.ticket.redis.service;

import com.example.ticket.service.TicketService;
import com.example.ticket.util.exception.LockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class TicketReserveRedissonService {

    private final RedissonClient redissonClient;
    private final TicketService ticketService;

    // step 2, 3) redisson 분산 락
    public void reverseTicket(Long ticketId) {
        RLock lock = redissonClient.getLock("ticket-" + ticketId);

        try {
            boolean isLocked = lock.tryLock(3, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new LockException();
            }

            ticketService.reserveTicket(ticketId);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
