/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.controller;

import com.burnout.visualizer.entity.Image;
import com.burnout.visualizer.repository.ImageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author aleksandra
 */
@Controller
public class FavoriteController {
    
    private final ImageRepository imageRepository;
    public FavoriteController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }
    
    @PostMapping("/favorite/{id}")
    public String toggleFavorite(@PathVariable Long id) {
        Image image = imageRepository.findById(id).orElseThrow();
        image.setIsFavorite(!image.getIsFavorite());
        imageRepository.save(image);
        return "redirect:/dashboard";
    }
}
