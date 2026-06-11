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
public class MinimalisticPromptBuilderTest {
    @Test
    void buildPromptShouldIncludeStyleAndParams() {
        MinimalisticPromptBuilder builder = new MinimalisticPromptBuilder();
        GenerationRequestDto dto = new GenerationRequestDto();
        dto.setFatigueLevel(2);
        dto.setStressDuration(1);
        dto.setCoffeeAmount(0);

        String prompt = builder.buildPrompt(dto);

        assertTrue(prompt.contains("минималистичную инфографику"));
        assertTrue(prompt.contains("простые линии"));
        assertTrue(prompt.contains("усталость 2/10"));
        assertEquals(VisualStyle.MINIMALISTIC, builder.getStyle());

    }
}
