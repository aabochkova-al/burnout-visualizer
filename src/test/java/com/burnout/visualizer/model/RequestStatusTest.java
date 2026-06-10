/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author aleksandra
 */
public class RequestStatusTest {
    @Test
    void enumValuesShouldBeCorrect() {
        assertEquals(3, RequestStatus.values().length);
        assertNotNull(RequestStatus.PENDING);
        assertNotNull(RequestStatus.SUCCESS);
        assertNotNull(RequestStatus.ERROR);
    }
}
