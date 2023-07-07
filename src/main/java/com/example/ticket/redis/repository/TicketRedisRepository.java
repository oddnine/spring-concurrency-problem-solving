package com.example.ticket.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void createTicket(Long ticketId, int maxCount) {
        String key = getKey(ticketId);

        redisTemplate.opsForValue().set(key, String.valueOf(maxCount));
    }

    public Long decreaseTicketCount(Long ticketId) {
        String key = getKey(ticketId);

        return redisTemplate.opsForValue().decrement(key);
    }

    private String getKey(Long ticketId) {
        return "ticket-currentCount-" + ticketId;
    }
}
