/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.service;

import com.burnout.visualizer.client.GigaChatRestClient;
import com.burnout.visualizer.dto.GenerationRequestDto;
import com.burnout.visualizer.entity.GenerationRequest;
import com.burnout.visualizer.entity.Image;
import com.burnout.visualizer.entity.User;
import com.burnout.visualizer.repository.GenerationRequestRepository;
import com.burnout.visualizer.repository.ImageRepository;
import com.burnout.visualizer.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author aleksandra
 */
@Service
public class GenerationService {
    
 
    private final GenerationRequestRepository requestRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final GigaChatRestClient gigaChatClient;
    private final String uploadDir;

    public GenerationService(GenerationRequestRepository requestRepository,
                             ImageRepository imageRepository,
                             UserRepository userRepository,
                             GigaChatRestClient gigaChatClient,
                             @Value("${app.upload.dir}") String uploadDir) {
        this.requestRepository = requestRepository;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.gigaChatClient = gigaChatClient;
        this.uploadDir = uploadDir;
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку для загрузок: " + uploadDir, e);
        }
    }

    @Transactional
    public void generateImage(Long userId, GenerationRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        GenerationRequest request = new GenerationRequest();
        request.setUser(user);
        request.setFatigueLevel(dto.getFatigueLevel());
        request.setStressDuration(dto.getStressDuration());
        request.setCoffeeAmount(dto.getCoffeeAmount());
        request.setVisualStyle(dto.getVisualStyle());
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());
        request = requestRepository.save(request);

        try {
            String prompt = buildPrompt(dto);
            System.out.println("Prompt: " + prompt);

            String fileId = gigaChatClient.generateImage(prompt);
            System.out.println("fileId: " + fileId);

            byte[] imageBytes = gigaChatClient.downloadFile(fileId);
            String fileName = UUID.randomUUID() + ".jpg";
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, imageBytes);

            Image image = new Image();
            image.setGenerationRequest(request);
            image.setFileName(fileName);
            image.setFileUrl("/uploads/" + fileName);
            image.setIsFavorite(false);
            imageRepository.save(image);

            request.setStatus("SUCCESS");
            request.setUpdatedAt(LocalDateTime.now());
            requestRepository.save(request);
        } catch (Exception e) {
            request.setStatus("ERROR");
            request.setUpdatedAt(LocalDateTime.now());
            requestRepository.save(request);
            throw new RuntimeException("Ошибка при генерации изображения: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(GenerationRequestDto dto) {
        String styleText;
        switch (dto.getVisualStyle().toUpperCase()) {
            case "ABSTRACT":
                styleText = "абстрактная композиция, яркие цвета, хаотичные формы";
                break;
            case "COMIC":
                styleText = "комикс-стиль, жирные линии, яркие контрасты, карикатурный персонаж";
                break;
            case "MINIMALISTIC":
                styleText = "минималистичная инфографика, простые линии, сдержанные тона";
                break;
            default:
                styleText = "абстрактная композиция";
        }
        return String.format(
                "Создай изображение, отражающее выгорание человека: уровень усталости %d из 10, " +
                "напряжённый период длится %d дней, количество чашек кофе в день - %d. " +
                "Стиль: %s.",
                dto.getFatigueLevel(), dto.getStressDuration(), dto.getCoffeeAmount(), styleText
        );
    }
    
}
