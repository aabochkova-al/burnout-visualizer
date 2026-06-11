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
public class AbstractPromptBuilder implements PromptBuilder {

    @Override
    public String buildPrompt(GenerationRequestDto dto) {
        return String.format(
            "Создай абстрактную композицию, отражающую выгорание: уровень усталости %d из 10, " +
            "напряжённый период %d дней, кофе %d чашек в день. Яркие цвета, хаотичные формы.",
            dto.getFatigueLevel(), dto.getStressDuration(), dto.getCoffeeAmount()
        );
    }

    @Override
    public VisualStyle getStyle() {
        return VisualStyle.ABSTRACT;
    }
    
}
