package com.bornfire.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {
    @JsonProperty("Status")
    private String status;

    @JsonProperty("Message")
    private String message;

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
