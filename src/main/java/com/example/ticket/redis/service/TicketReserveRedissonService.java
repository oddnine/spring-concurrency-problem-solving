package com.example.ticket.redis.service;

import com.example.ticket.dto.ticket.request.TicketReserveRequest;
import com.example.ticket.kafka.producer.ReserveTicketProducer;
import com.example.ticket.redis.repository.TicketRedisRepository;
import com.example.ticket.service.TicketService;
import com.example.ticket.util.exception.LockException;
import com.example.ticket.util.exception.TicketSoldOutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class TicketReserveRedissonService {

    private final RedissonClient redissonClient;
    private final TicketService ticketService;
    private final ReserveTicketProducer reserveTicketProducer;
    private final TicketRedisRepository ticketRedisRepository;

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

    // step 4) 레디스 분산 락을 사용해서 레디스에서 잔여 표 확인 및 카운팅 후 카프카 메시징 처리
    public void sendReserveTicketWithRedisOnKafka(Long ticketId) {
        RLock lock = redissonClient.getLock("ticket-reserve-" + ticketId);

        try {
            boolean isLocked = lock.tryLock(3, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new LockException();
            }

            List<Integer> maxAndCurrentTicketCount = ticketRedisRepository.getMaxAndCurrentTicketCount(ticketId);
            log.info("Redis ticket " + ticketId + ": maxCount: " + maxAndCurrentTicketCount.get(0) + ", currentCount: " + maxAndCurrentTicketCount.get(1));

            if (maxAndCurrentTicketCount.get(0) <= maxAndCurrentTicketCount.get(1)) {
                throw new TicketSoldOutException();
            }

            ticketRedisRepository.increaseTicketCount(ticketId);

            TicketReserveRequest ticket = new TicketReserveRequest();
            ticket.setTicketId(ticketId);
            reserveTicketProducer.send(ticket);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
