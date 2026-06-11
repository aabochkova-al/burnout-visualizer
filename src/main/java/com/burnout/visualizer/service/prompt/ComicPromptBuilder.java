/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.service.prompt;

import com.burnout.visualizer.dto.GenerationRequestDto;
import com.burnout.visualizer.model.VisualStyle;
import org.springframework.stereotype.Component;

/**
 *
 * @author aleksandra
 */
@Component
public class ComicPromptBuilder implements PromptBuilder{

    @Override
    public String buildPrompt(GenerationRequestDto dto) {
        return String.format(
            "Создай комикс-стиль, жирные линии, яркие контрасты, карикатурный персонаж, " +
            "отражающий выгорание: усталость %d/10, период %d дней, кофе %d чашек.",
            dto.getFatigueLevel(), dto.getStressDuration(), dto.getCoffeeAmount()
        );
    }

    @Override
    public VisualStyle getStyle() {
        return VisualStyle.COMIC;
    }
    
}
