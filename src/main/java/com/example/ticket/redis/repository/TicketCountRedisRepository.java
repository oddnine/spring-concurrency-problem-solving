package com.example.ticket.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketCountRedisRepository {
    private static final String TICKET_COUNT_KEY = "ticket:count";

    private final RedisTemplate<String, String> redisTemplate;

    public Integer getTicketCurrentCount(Long ticketId) {
        return (Integer) redisTemplate.opsForHash().get(TICKET_COUNT_KEY, ticketId.toString());
    }

    public void updateTicketCurrentCount(Long ticketId, Integer newCount) {
        redisTemplate.opsForHash().put(TICKET_COUNT_KEY, ticketId.toString(), newCount.toString());
    }
}
