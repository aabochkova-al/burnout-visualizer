/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aleksandra
 */
@Component
public class GigaChatResponseParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String extractFileIdFromResponse(String responseBody) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode message = jsonNode.path("choices").path(0).path("message");
        if (message.has("function_call") ||
            message.path("content").asText().contains("<img") ||
            message.path("content").asText().contains("data:image")) {

            Pattern pattern = Pattern.compile("<img\\s+src=\"([a-f0-9-]+)\"");
            Matcher matcher = pattern.matcher(message.path("content").asText());
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        String errorText = message.path("content").asText();
        throw new RuntimeException("GigaChat не сгенерировал изображение: " + errorText);
    }
}
