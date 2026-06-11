/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.entity;

import jakarta.persistence.*;

/**
 *
 * @author aleksandra
 */
@Entity
@Table(name = "images")
public class Image {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private GenerationRequest generationRequest;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = false;

    public Image() {}
    
    public Image(GenerationRequest generationRequest, String fileName, String fileUrl) {
        this.generationRequest = generationRequest;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GenerationRequest getGenerationRequest() { return generationRequest; }
    public void setGenerationRequest(GenerationRequest generationRequest) { this.generationRequest = generationRequest; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
}
