/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aleksandra
 */
@Component
public class GigaChatRestClient {

    private final RestTemplate restTemplate;
    private final String authKey;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String cachedToken;
    private long tokenExpiryTime;

    public GigaChatRestClient(@Value("${gigachat.auth-key}") String authKey) {
        this.authKey = authKey;
        this.restTemplate = createRestTemplateWithDisabledSSL();
    }

    private RestTemplate createRestTemplateWithDisabledSSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            return new RestTemplate(factory);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось настроить RestTemplate с отключённой проверкой SSL", e);
        }
    }

    private synchronized String getAccessToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return cachedToken;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + authKey);
            headers.set("RqUID", UUID.randomUUID().toString());

            HttpEntity<String> entity = new HttpEntity<>("scope=GIGACHAT_API_PERS", headers);
            ResponseEntity<String> response = restTemplate.exchange(
                "https://ngw.devices.sberbank.ru:9443/api/v2/oauth",
                HttpMethod.POST,
                entity,
                String.class
            );
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            cachedToken = jsonNode.get("access_token").asText();
            // Устанавливаем время жизни токена (30 минут)
            tokenExpiryTime = System.currentTimeMillis() + 30 * 60 * 1000;
            return cachedToken;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения токена GigaChat: " + e.getMessage(), e);
        }
    }

    public String generateImage(String prompt) {
        String token = getAccessToken();

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

        try {
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

            String content = response.getBody();
            System.out.println("=== ПОЛНЫЙ ОТВЕТ ОТ GigaChat ===");
            System.out.println(content);
            System.out.println("=================================");

            JsonNode jsonNode = objectMapper.readTree(content);

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

        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации изображения: " + e.getMessage(), e);
        }
    }

    public byte[] downloadFile(String fileId) {
        String token = getAccessToken();
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Ошибка скачивания файла: " + fileId, e);
        }
    }
}
