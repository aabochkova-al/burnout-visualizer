/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.controller;

import com.burnout.visualizer.service.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author aleksandra
 */
@Controller
public class FavoriteController {
    
    private final ImageService imageService;

    public FavoriteController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/favorite/{id}")
    public String toggleFavorite(@PathVariable Long id) {
        imageService.toggleFavorite(id);
        return "redirect:/dashboard";
    }
}
