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
public class ComicPromptBuilderTest {
    @Test
    void buildPromptShouldIncludeStyleAndParams() {
        ComicPromptBuilder builder = new ComicPromptBuilder();
        GenerationRequestDto dto = new GenerationRequestDto();
        dto.setFatigueLevel(8);
        dto.setStressDuration(15);
        dto.setCoffeeAmount(3);

        String prompt = builder.buildPrompt(dto);

        assertTrue(prompt.contains("комикс-стиль"));
        assertTrue(prompt.contains("жирные линии"));
        assertTrue(prompt.contains("усталость 8/10"));
        assertEquals(VisualStyle.COMIC, builder.getStyle());
    }
}
