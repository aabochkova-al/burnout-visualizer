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

/**
 *
 * @author aleksandra
 */
@Controller
public class DashboardController {
    
    private final GenerationService generationService;
    private final UserRepository userRepository;
    private final GenerationRequestRepository requestRepository;
    private final ImageRepository imageRepository;
    
    
    public DashboardController(GenerationService generationService,
                               UserRepository userRepository,
                               GenerationRequestRepository requestRepository,
                               ImageRepository imageRepository) {
        this.generationService = generationService;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.imageRepository = imageRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<GenerationRequest> history = requestRepository.findByUserOrderByCreatedAtDesc(user);
        List<Image> favorites = imageRepository.findAllFavoritesByUserId(user.getId());
        model.addAttribute("requestDto", new GenerationRequestDto());
        model.addAttribute("history", history);
        model.addAttribute("favorites", favorites);
        return "dashboard";
    }
    
    @PostMapping("/generate")
    public String generate(@Valid @ModelAttribute("requestDto") GenerationRequestDto dto,
                           BindingResult result,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        if (result.hasErrors()) {
            return "dashboard";
        }
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        try {
            generationService.generateImage(user.getId(), dto);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "dashboard";
        }
        return "redirect:/dashboard?success";
    }
    
}
