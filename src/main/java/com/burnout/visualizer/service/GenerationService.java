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
import com.burnout.visualizer.model.VisualStyle;
import com.burnout.visualizer.repository.GenerationRequestRepository;
import com.burnout.visualizer.repository.ImageRepository;
import com.burnout.visualizer.repository.UserRepository;
import com.burnout.visualizer.service.prompt.PromptBuilder;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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
    private final Map<VisualStyle, PromptBuilder> promptBuilders;
    
    private final int rateLimitMax;
    private final int rateLimitPeriodMinutes;


    public GenerationService(GenerationRequestRepository requestRepository,
                             ImageRepository imageRepository,
                             UserRepository userRepository,
                             GigaChatRestClient gigaChatClient,
                             @Value("${app.upload.dir}") String uploadDir, List<PromptBuilder> builders,
                             @Value("${app.rate.limit.max}") int rateLimitMax,
                             @Value("${app.rate.limit.period-minutes}") int rateLimitPeriodMinutes) {
        this.requestRepository = requestRepository;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.gigaChatClient = gigaChatClient;
        this.uploadDir = uploadDir;
        this.promptBuilders = builders.stream()
                .collect(Collectors.toMap(PromptBuilder::getStyle, Function.identity()));
        this.rateLimitMax = rateLimitMax;
        this.rateLimitPeriodMinutes = rateLimitPeriodMinutes;
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку для загрузок: " + uploadDir, e);
        }
    }
    
    public void checkRateLimit(Long userId) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(rateLimitPeriodMinutes);
        long count = requestRepository.countByUserIdAndCreatedAtAfter(userId, since);
        if (count >= rateLimitMax) {
            throw new RuntimeException("Превышен лимит запросов: не более " + rateLimitMax +
                    " запросов за " + rateLimitPeriodMinutes + " минут.");
        }
    }
    
    @Transactional
    public GenerationRequest createPendingRequest(Long userId, GenerationRequestDto dto) {
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
        return requestRepository.save(request);
    }

//    @Transactional
//    public void generateImage(Long userId, GenerationRequestDto dto) {
//        checkRateLimit(userId);
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
//
//        GenerationRequest request = new GenerationRequest();
//        request.setUser(user);
//        request.setFatigueLevel(dto.getFatigueLevel());
//        request.setStressDuration(dto.getStressDuration());
//        request.setCoffeeAmount(dto.getCoffeeAmount());
//        request.setVisualStyle(dto.getVisualStyle());
//        request.setStatus("PENDING");
//        request.setCreatedAt(LocalDateTime.now());
//        request = requestRepository.save(request);
//
//        try {
//            VisualStyle style = VisualStyle.fromString(dto.getVisualStyle());
//            PromptBuilder builder = promptBuilders.get(style);
//            if (builder == null) {
//                builder = promptBuilders.get(VisualStyle.ABSTRACT);
//            }
//            
//            String prompt = builder.buildPrompt(dto);
//            System.out.println("Prompt: " + prompt);
//
//            String fileId = gigaChatClient.generateImage(prompt);
//            System.out.println("fileId: " + fileId);
//
//            byte[] imageBytes = gigaChatClient.downloadFile(fileId);
//            String fileName = UUID.randomUUID() + ".jpg";
//            Path filePath = Paths.get(uploadDir, fileName);
//            Files.write(filePath, imageBytes);
//
//            Image image = new Image();
//            image.setGenerationRequest(request);
//            image.setFileName(fileName);
//            image.setFileUrl("/uploads/" + fileName);
//            image.setIsFavorite(false);
//            imageRepository.save(image);
//
//            request.setStatus("SUCCESS");
//            request.setUpdatedAt(LocalDateTime.now());
//            requestRepository.save(request);
//        } catch (Exception e) {
//            request.setStatus("ERROR");
//            request.setUpdatedAt(LocalDateTime.now());
//            requestRepository.save(request);
//            throw new RuntimeException("Ошибка при генерации изображения: " + e.getMessage(), e);
//        }
//    }

    @Async("taskExecutor")
    public CompletableFuture<Void> generateImageAsync(GenerationRequest request, GenerationRequestDto dto) {
        try {
            // Получаем промпт через стратегию
            VisualStyle style = VisualStyle.fromString(dto.getVisualStyle());
            PromptBuilder builder = promptBuilders.get(style);
            if (builder == null) builder = promptBuilders.get(VisualStyle.ABSTRACT);
            String prompt = builder.buildPrompt(dto);
            System.out.println("Prompt: " + prompt);

            String fileId = gigaChatClient.generateImage(prompt);
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
            System.err.println("Async generation failed: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
}
