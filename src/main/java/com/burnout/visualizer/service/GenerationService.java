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
import com.burnout.visualizer.model.RequestStatus;
import com.burnout.visualizer.model.VisualStyle;
import com.burnout.visualizer.repository.GenerationRequestRepository;
import com.burnout.visualizer.repository.ImageRepository;
import com.burnout.visualizer.repository.UserRepository;
import com.burnout.visualizer.service.prompt.PromptBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author aleksandra
 */
@Service
public class GenerationService {
    
 
    private final GenerationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final GigaChatRestClient gigaChatClient;
    private final RateLimiter rateLimiter;
    private final ImageService imageService;
    private final Map<VisualStyle, PromptBuilder> promptBuilders;

    public GenerationService(GenerationRequestRepository requestRepository,
                             UserRepository userRepository,
                             ImageRepository imageRepository,
                             GigaChatRestClient gigaChatClient,
                             RateLimiter rateLimiter,
                             ImageService imageService,
                             List<PromptBuilder> builders) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.gigaChatClient = gigaChatClient;
        this.rateLimiter = rateLimiter;
        this.imageService = imageService;
        this.promptBuilders = builders.stream()
                .collect(Collectors.toMap(PromptBuilder::getStyle, Function.identity()));
    }

    public void checkRateLimit(Long userId) {
        rateLimiter.check(userId);
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
        request.setVisualStyle(VisualStyle.fromString(dto.getVisualStyle()));
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> generateImageAsync(GenerationRequest request, GenerationRequestDto dto) {
        try {
            PromptBuilder builder = promptBuilders.getOrDefault(request.getVisualStyle(), promptBuilders.get(VisualStyle.ABSTRACT));
            String prompt = builder.buildPrompt(dto);
            System.out.println("Prompt: " + prompt);

            String fileId = gigaChatClient.generateImage(prompt);
            byte[] imageBytes = gigaChatClient.downloadFile(fileId);

            var image = imageService.createImage(request, imageBytes);

            request.setStatus(RequestStatus.SUCCESS);
            request.setUpdatedAt(LocalDateTime.now());
            requestRepository.save(request);

        } catch (Exception e) {
            request.setStatus(RequestStatus.ERROR);
            request.setUpdatedAt(LocalDateTime.now());
            requestRepository.save(request);
            System.err.println("Async generation failed: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
    
    public List<GenerationRequest> getUserHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return requestRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Image> getUserFavorites(Long userId) {
        return imageRepository.findAllFavoritesByUserId(userId);
    }
}
