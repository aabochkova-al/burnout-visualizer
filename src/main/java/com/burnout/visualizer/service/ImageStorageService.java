/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 *
 * @author aleksandra
 */
@Component
public class ImageStorageService {
    private final String uploadDir;

    public ImageStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadDir = uploadDir;
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку для загрузок: " + uploadDir, e);
        }
    }
    
    public String saveImage(byte[] imageBytes) throws IOException {
        String fileName = UUID.randomUUID() + ".jpg";
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, imageBytes);
        return "/uploads/" + fileName;
    }
    
}
