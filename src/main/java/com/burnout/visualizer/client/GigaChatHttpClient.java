/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author aleksandra
 */
@Component
public class GigaChatHttpClient {
    private final RestTemplate restTemplate;

    public GigaChatHttpClient(@Qualifier("restTemplateWithDisabledSSL") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String sendGenerationRequest(String prompt, String token) {
        String requestBody = String.format("""
            {
                "model": "GigaChat",
                "messages": [
                    { "role": "system", "content": "Ты — талантливый художник. Всегда генерируй изображение, когда пользователь просит визуализацию." },
                    { "role": "user", "content": "%s" }
                ],
                "function_call": "auto",
                "n": 1,
                "temperature": 0.7
            }
            """, prompt.replace("\"", "\\\""));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            "https://gigachat.devices.sberbank.ru/api/v1/chat/completions",
            HttpMethod.POST,
            entity,
            String.class
        );
        return response.getBody();
    }

    public byte[] downloadFile(String fileId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(
            "https://gigachat.devices.sberbank.ru/api/v1/files/{fileId}/content",
            HttpMethod.GET,
            entity,
            byte[].class,
            fileId
        );
        return response.getBody();
    }
}
