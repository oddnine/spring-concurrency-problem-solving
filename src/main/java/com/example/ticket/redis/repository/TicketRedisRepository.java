package com.example.ticket.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TicketRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void createTicket(Long ticketId, int maxCount) {
        String maxCountKey = "ticket-" + ticketId + "-maxCount";
        String currentCountKey = "ticket-" + ticketId + "-currentCount";

        redisTemplate.opsForValue().set(maxCountKey, String.valueOf(maxCount));
        redisTemplate.opsForValue().set(currentCountKey, "0");
    }

    public void increaseTicketCount(Long ticketId) {
        String currentCountKey = "ticket-" + ticketId + "-currentCount";

        redisTemplate.opsForValue().increment(currentCountKey);
    }

    public List<Integer> getMaxAndCurrentTicketCount(Long ticketId) {
        String maxCountKey = "ticket-" + ticketId + "-maxCount";
        String currentCountKey = "ticket-" + ticketId + "-currentCount";

        return Objects.requireNonNull(redisTemplate.opsForValue().multiGet(Arrays.asList(maxCountKey, currentCountKey)))
                .stream()
                .map(value -> value != null ? Integer.parseInt(value) : 0)
                .collect(Collectors.toList());
    }
}
