/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.service;

import com.burnout.visualizer.entity.GenerationRequest;
import com.burnout.visualizer.entity.Image;
import com.burnout.visualizer.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author aleksandra
 */
@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final ImageStorageService imageStorageService;

    public ImageService(ImageRepository imageRepository, ImageStorageService imageStorageService) {
        this.imageRepository = imageRepository;
        this.imageStorageService = imageStorageService;
    }
    
    @Transactional
    public Image createImage(GenerationRequest request, byte[] imageBytes) throws Exception {
        String fileUrl = imageStorageService.saveImage(imageBytes);
        Image image = new Image();
        image.setGenerationRequest(request);
        image.setFileName(fileUrl.substring(fileUrl.lastIndexOf('/') + 1));
        image.setFileUrl(fileUrl);
        image.setIsFavorite(false);
        return imageRepository.save(image);
    }
    
    @Transactional
    public void toggleFavorite(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Изображение не найдено"));
        image.setIsFavorite(!image.getIsFavorite());
        imageRepository.save(image);
    }
    
}
