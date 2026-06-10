/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.service.prompt;

import com.burnout.visualizer.dto.GenerationRequestDto;
import com.burnout.visualizer.model.VisualStyle;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author aleksandra
 */
public class AbstractPromptBuilderTest {
    @Test
    void buildPromptShouldIncludeParametersAndStyle() {
        AbstractPromptBuilder builder = new AbstractPromptBuilder();
        GenerationRequestDto dto = new GenerationRequestDto();
        dto.setFatigueLevel(7);
        dto.setStressDuration(30);
        dto.setCoffeeAmount(4);
        
        String prompt = builder.buildPrompt(dto);
        
        assertTrue(prompt.contains("уровень усталости 7 из 10"));
        assertTrue(prompt.contains("напряжённый период 30 дней"));
        assertTrue(prompt.contains("кофе 4 чашек"));
        assertTrue(prompt.contains("абстрактную композицию"));
        assertEquals(VisualStyle.ABSTRACT, builder.getStyle());
    }
    
}
