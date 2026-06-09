/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.burnout.visualizer.repository;

import com.burnout.visualizer.entity.GenerationRequest;
import com.burnout.visualizer.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author aleksandra
 */
public interface GenerationRequestRepository extends JpaRepository<GenerationRequest, Long>{
    List<GenerationRequest> findByUserOrderByCreatedAtDesc(User user);
    long countByUserIdAndCreatedAtAfter(Long userId, LocalDateTime dateTime);
}
