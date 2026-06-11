/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.burnout.visualizer.service.prompt;

import com.burnout.visualizer.dto.GenerationRequestDto;
import com.burnout.visualizer.model.VisualStyle;

/**
 *
 * @author aleksandra
 */
public interface PromptBuilder {
    String buildPrompt(GenerationRequestDto dto);
    VisualStyle getStyle();
}
