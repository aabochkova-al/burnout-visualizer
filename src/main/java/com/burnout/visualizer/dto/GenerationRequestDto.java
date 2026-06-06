/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author aleksandra
 */
public class GenerationRequestDto {
    
    @NotNull(message = "Уровень усталости обязателен")
    @Min(1)
    @Max(10)
    private Integer fatigueLevel;
    
    @NotNull(message = "Длительность напряжённого периода обязательна")
    @Min(1)
    private Integer stressDuration; //в днях
    
    @NotNull(message = "Количество кофе обязательно")
    @Min(0)
    private Integer coffeeAmount; //чашек в день
    
    @NotNull(message = "Стиль визуализации обязателен")
    private String visualStyle;
    
    public GenerationRequestDto() {}
    
    public Integer getFatigueLevel() { return fatigueLevel; }
    public void setFatigueLevel(Integer fatigueLevel) { this.fatigueLevel = fatigueLevel; }

    public Integer getStressDuration() { return stressDuration; }
    public void setStressDuration(Integer stressDuration) { this.stressDuration = stressDuration; }

    public Integer getCoffeeAmount() { return coffeeAmount; }
    public void setCoffeeAmount(Integer coffeeAmount) { this.coffeeAmount = coffeeAmount; }

    public String getVisualStyle() { return visualStyle; }
    public void setVisualStyle(String visualStyle) { this.visualStyle = visualStyle; }  
}
