/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.burnout.visualizer.repository;

import com.burnout.visualizer.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author aleksandra
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByGenerationRequest_User_IdAndIsFavoriteTrue(Long userId);
}
