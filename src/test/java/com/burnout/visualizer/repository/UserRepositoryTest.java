/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.repository;

import com.burnout.visualizer.entity.User;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 *
 * @author aleksandra
 */
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void shouldSaveAndFindUserByUsername() {
        User user = new User("hugo", "hugo@example.com", "encoded");
        entityManager.persistAndFlush(user);
        
        User found = userRepository.findByUsername("hugo").orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("hugo@example.com");
    }
    
    @Test
    void existsByUsernameShouldReturnTrue_whenExists() {
        User user = new User("alice", "alice@example.com", "pass");
        entityManager.persistAndFlush(user);
        boolean exists = userRepository.existsByUsername("alice");
        assertThat(exists).isTrue();
    }
    
}
