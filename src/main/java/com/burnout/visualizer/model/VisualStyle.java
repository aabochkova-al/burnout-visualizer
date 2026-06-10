/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.burnout.visualizer.model;

/**
 *
 * @author aleksandra
 */
public enum VisualStyle {
    ABSTRACT,
    COMIC,
    MINIMALISTIC;
    
    public static VisualStyle fromString(String value) {
        if (value == null) {
            return ABSTRACT;
        }
        try {
            return VisualStyle.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ABSTRACT;
        }
    }
}
