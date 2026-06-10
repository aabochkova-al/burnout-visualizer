/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.service;

import com.burnout.visualizer.dto.GenerationRequestDto;
import com.burnout.visualizer.entity.GenerationRequest;
import com.burnout.visualizer.entity.User;
import com.burnout.visualizer.repository.GenerationRequestRepository;
import com.burnout.visualizer.repository.UserRepository;
import jakarta.transaction.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

/**
 *
 * @author aleksandra
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class GenerationServiceIntegrationTest {
    @Autowired
    private GenerationService generationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GenerationRequestRepository requestRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User("gen_user", "gen@example.com", "encoded");
        userRepository.save(testUser);
        
        ReflectionTestUtils.setField(generationService, "rateLimitMax", 1);
        ReflectionTestUtils.setField(generationService, "rateLimitPeriodMinutes", 60);
    }
    
    @Test
    void createPendingRequestShouldSaveRequest() {
        GenerationRequestDto dto = new GenerationRequestDto();
        dto.setFatigueLevel(5);
        dto.setStressDuration(10);
        dto.setCoffeeAmount(3);
        dto.setVisualStyle("ABSTRACT");
        
        GenerationRequest request = generationService.createPendingRequest(testUser.getId(), dto);
        
        assertThat(request).isNotNull();
        assertThat(request.getStatus()).isEqualTo("PENDING");
        assertThat(request.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(request.getFatigueLevel()).isEqualTo(5);
    }
    
    @Test
    void checkRateLimitShouldThrowWhenExceedsLimit() {
        GenerationRequestDto dto = new GenerationRequestDto();
        dto.setFatigueLevel(1);
        dto.setStressDuration(1);
        dto.setCoffeeAmount(1);
        dto.setVisualStyle("ABSTRACT");
        
        generationService.createPendingRequest(testUser.getId(), dto);
        assertThrows(RuntimeException.class, () -> generationService.checkRateLimit(testUser.getId()));
    }
}
