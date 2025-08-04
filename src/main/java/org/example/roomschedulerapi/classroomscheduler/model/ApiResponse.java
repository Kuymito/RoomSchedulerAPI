package org.example.roomschedulerapi.classroomscheduler.model;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiResponse <T> {
    private String message;
    private T payload;
    private HttpStatus status;
    private LocalDateTime time;

    public ApiResponse(String message, T payload, HttpStatus status, LocalDateTime time) {
        this.message = message;
        this.payload = payload;
        this.status = status;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public HttpStatus getStatus() {
        return status;
    }
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
