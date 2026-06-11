/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.service;

import com.burnout.visualizer.dto.UserRegistrationDto;
import com.burnout.visualizer.entity.User;
import com.burnout.visualizer.repository.UserRepository;
import jakarta.transaction.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author aleksandra
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void registerNewUserShouldSaveUser() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("integration_user");
        dto.setEmail("integration@example.com");
        dto.setPassword("secret");
        
        userService.registerNewUser(dto);
        
        User saved = userRepository.findByUsername("integration_user").orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("integration@example.com");
        assertThat(saved.getPassword()).isNotEqualTo("secret");
    }
    
    @Test
    void registerNewUserShouldThrowWhenUsernameExists() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("existing_user");
        dto.setEmail("existing@example.com");
        dto.setPassword("pass");
        userService.registerNewUser(dto);
        
        UserRegistrationDto duplicate = new UserRegistrationDto();
        duplicate.setUsername("existing_user");
        duplicate.setEmail("another@example.com");
        duplicate.setPassword("pass");
        
        assertThrows(RuntimeException.class, () -> userService.registerNewUser(duplicate));
    } 
}
