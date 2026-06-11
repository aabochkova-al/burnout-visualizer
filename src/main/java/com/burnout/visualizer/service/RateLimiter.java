/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.service;

import com.burnout.visualizer.repository.GenerationRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 *
 * @author aleksandra
 */
@Component
public class RateLimiter {
    private final GenerationRequestRepository requestRepository;
    private final int max;
    private final int periodMinutes;
    
    public RateLimiter(GenerationRequestRepository requestRepository,
                       @Value("${app.rate.limit.max}") int max,
                       @Value("${app.rate.limit.period-minutes}") int periodMinutes) {
        this.requestRepository = requestRepository;
        this.max = max;
        this.periodMinutes = periodMinutes;
    }
    
    public void check(Long userId) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(periodMinutes);
        long count = requestRepository.countByUserIdAndCreatedAtAfter(userId, since);
        if (count >= max) {
            throw new RuntimeException("Превышен лимит запросов: не более " + max +
                    " запросов за " + periodMinutes + " минут.");
        }
    }
}
