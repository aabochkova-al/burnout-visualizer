/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author aleksandra
 */
public class GigaChatTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("expires_at")
    private String expiresAt;
    
    private String scope;
    
        public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
        
}
