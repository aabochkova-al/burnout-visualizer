/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 *
 * @author aleksandra
 */
@Entity
@Table(name = "generation_requests")
public class GenerationRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "fatigue_level", nullable = false)
    private Integer fatigueLevel;
    
    @Column(name = "stress_duration", nullable = false)
    private Integer stressDuration;
    
    @Column(name = "coffee_amount", nullable = false)
    private Integer coffeeAmount;
    
    @Column(name = "visual_style", nullable = false)
    private String visualStyle;
    
    @Column(nullable = false)
    private String status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public GenerationRequest() {}

    public GenerationRequest(User user, Integer fatigueLevel, Integer stressDuration,
                             Integer coffeeAmount, String visualStyle, String status) {
        this.user = user;
        this.fatigueLevel = fatigueLevel;
        this.stressDuration = stressDuration;
        this.coffeeAmount = coffeeAmount;
        this.visualStyle = visualStyle;
        this.status = status;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Integer getFatigueLevel() { return fatigueLevel; }
    public void setFatigueLevel(Integer fatigueLevel) { this.fatigueLevel = fatigueLevel; }
    public Integer getStressDuration() { return stressDuration; }
    public void setStressDuration(Integer stressDuration) { this.stressDuration = stressDuration; }
    public Integer getCoffeeAmount() { return coffeeAmount; }
    public void setCoffeeAmount(Integer coffeeAmount) { this.coffeeAmount = coffeeAmount; }
    public String getVisualStyle() { return visualStyle; }
    public void setVisualStyle(String visualStyle) { this.visualStyle = visualStyle; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
