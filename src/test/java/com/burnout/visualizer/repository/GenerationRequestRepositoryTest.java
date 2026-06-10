/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.repository;

import com.burnout.visualizer.entity.GenerationRequest;
import com.burnout.visualizer.entity.User;
import com.burnout.visualizer.model.RequestStatus;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 *
 * @author aleksandra
 */
@DataJpaTest
public class GenerationRequestRepositoryTest {
    @Autowired
    private GenerationRequestRepository requestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User("user1", "user1@example.com", "pass");
        entityManager.persistAndFlush(testUser);
    }
    
    @Test
    void findByUserOrderByCreatedAtDescShouldReturnSorted() {
        GenerationRequest old = new GenerationRequest();
        old.setUser(testUser);
        old.setFatigueLevel(5);
        old.setStressDuration(10);
        old.setCoffeeAmount(2);
        old.setVisualStyle("ABSTRACT");
        old.setStatus(RequestStatus.PENDING);
        old.setCreatedAt(LocalDateTime.now().minusDays(1));
        entityManager.persist(old);
        
        GenerationRequest recent = new GenerationRequest();
        recent.setUser(testUser);
        recent.setFatigueLevel(7);
        recent.setStressDuration(20);
        recent.setCoffeeAmount(3);
        recent.setVisualStyle("COMIC");
        recent.setStatus(RequestStatus.SUCCESS);
        recent.setCreatedAt(LocalDateTime.now());
        entityManager.persist(recent);
        entityManager.flush();
        
        List<GenerationRequest> result = requestRepository.findByUserOrderByCreatedAtDesc(testUser);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCreatedAt()).isAfterOrEqualTo(result.get(1).getCreatedAt());
    }
    
    @Test
    void countByUserIdAndCreatedAtAfterShouldCountOnlyAfter() {
        GenerationRequest oldReq = new GenerationRequest();
        oldReq.setUser(testUser);
        oldReq.setFatigueLevel(1);
        oldReq.setStressDuration(1);
        oldReq.setCoffeeAmount(1);
        oldReq.setVisualStyle("ABSTRACT");
        oldReq.setStatus(RequestStatus.PENDING);
        oldReq.setCreatedAt(LocalDateTime.now().minusHours(2));
        entityManager.persistAndFlush(oldReq);
        
        GenerationRequest newReq = new GenerationRequest();
        newReq.setUser(testUser);
        newReq.setFatigueLevel(2);
        newReq.setStressDuration(2);
        newReq.setCoffeeAmount(2);
        newReq.setVisualStyle("COMIC");
        newReq.setStatus(RequestStatus.PENDING);
        newReq.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        entityManager.persistAndFlush(newReq);
        
        LocalDateTime since = LocalDateTime.now().minusMinutes(30);
        long count = requestRepository.countByUserIdAndCreatedAtAfter(testUser.getId(), since);
        assertThat(count).isEqualTo(1);
    }
    
}
