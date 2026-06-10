/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author aleksandra
 */
public class VisualStyleTest {
    @Test
    void fromStringShouldReturnCorrectEnum() {
        assertEquals(VisualStyle.ABSTRACT, VisualStyle.fromString("ABSTRACT"));
        assertEquals(VisualStyle.ABSTRACT, VisualStyle.fromString("abstract"));
        assertEquals(VisualStyle.COMIC, VisualStyle.fromString("COMIC"));
        assertEquals(VisualStyle.MINIMALISTIC, VisualStyle.fromString("MINIMALISTIC"));
    }
    
    @Test
    void fromStringShouldReturnAbstractWhenUnknown() {
        assertEquals(VisualStyle.ABSTRACT, VisualStyle.fromString("UNKNOWN"));
        assertEquals(VisualStyle.ABSTRACT, VisualStyle.fromString(""));
        assertEquals(VisualStyle.ABSTRACT, VisualStyle.fromString(null));
    }
    
    @Test
    void valuesShouldContainAllStyles() {
        VisualStyle[] values = VisualStyle.values();
        assertEquals(3, values.length);
        assertTrue(contains(values, VisualStyle.ABSTRACT));
        assertTrue(contains(values, VisualStyle.COMIC));
        assertTrue(contains(values, VisualStyle.MINIMALISTIC));
    }
    
    private boolean contains(VisualStyle[] arr, VisualStyle style) {
        for (VisualStyle s : arr) if (s == style) return true;
        return false;
    }
    
}
