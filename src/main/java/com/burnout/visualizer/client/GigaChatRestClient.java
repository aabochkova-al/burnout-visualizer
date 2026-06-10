/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.client;

import org.springframework.stereotype.Component;

/**
 *
 * @author aleksandra
 */
@Component
public class GigaChatRestClient {

    private final GigaChatTokenManager tokenManager;
    private final GigaChatHttpClient httpClient;
    private final GigaChatResponseParser responseParser;

    public GigaChatRestClient(GigaChatTokenManager tokenManager,
                              GigaChatHttpClient httpClient,
                              GigaChatResponseParser responseParser) {
        this.tokenManager = tokenManager;
        this.httpClient = httpClient;
        this.responseParser = responseParser;
    }

    public String generateImage(String prompt) {
        try {
            String token = tokenManager.getAccessToken();
            String responseBody = httpClient.sendGenerationRequest(prompt, token);
            return responseParser.extractFileIdFromResponse(responseBody);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации изображения: " + e.getMessage(), e);
        }
    }

    public byte[] downloadFile(String fileId) {
        try {
            String token = tokenManager.getAccessToken();
            return httpClient.downloadFile(fileId, token);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка скачивания файла: " + fileId, e);
        }
    }
}
