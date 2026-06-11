/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.controller;

import com.burnout.visualizer.dto.GenerationRequestDto;
import com.burnout.visualizer.entity.GenerationRequest;
import com.burnout.visualizer.entity.Image;
import com.burnout.visualizer.entity.User;
import com.burnout.visualizer.repository.GenerationRequestRepository;
import com.burnout.visualizer.repository.ImageRepository;
import com.burnout.visualizer.repository.UserRepository;
import com.burnout.visualizer.service.GenerationService;
import com.burnout.visualizer.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author aleksandra
 */
@Controller
public class DashboardController {
    private final GenerationService generationService;
    private final UserService userService;

    public DashboardController(GenerationService generationService,
                               UserService userService) {
        this.generationService = generationService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model, 
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer fatigueLevel,
            @RequestParam(required = false) Integer stressDuration,
            @RequestParam(required = false) Integer coffeeAmount,
            @RequestParam(required = false) String visualStyle) {

        User user = userService.findByUsername(userDetails.getUsername());

        GenerationRequestDto dto = new GenerationRequestDto();

        if (fatigueLevel != null) dto.setFatigueLevel(fatigueLevel);
        if (stressDuration != null) dto.setStressDuration(stressDuration);
        if (coffeeAmount != null) dto.setCoffeeAmount(coffeeAmount);
        if (visualStyle != null) dto.setVisualStyle(visualStyle);

        model.addAttribute("requestDto", dto);
        model.addAttribute("history", generationService.getUserHistory(user.getId()));
        model.addAttribute("favorites", generationService.getUserFavorites(user.getId()));

        return "dashboard";
    }

    @GetMapping("/dashboard/history")
    public String getHistoryFragment(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("history", generationService.getUserHistory(user.getId()));
        return "fragments/history :: historyList";
    }

    @PostMapping("/generate")
    public String generate(@Valid @ModelAttribute GenerationRequestDto dto,
                           BindingResult result,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        if (result.hasErrors()) {
            return "dashboard";
        }
        User user = userService.findByUsername(userDetails.getUsername());
        try {
            generationService.checkRateLimit(user.getId());
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("history", generationService.getUserHistory(user.getId()));
            model.addAttribute("favorites", generationService.getUserFavorites(user.getId()));
            return "dashboard";
        }
        GenerationRequest request = generationService.createPendingRequest(user.getId(), dto);
        generationService.generateImageAsync(request, dto);
        return "redirect:/dashboard";
    }
    
}
