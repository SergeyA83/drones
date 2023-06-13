package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ResponseDto(String result,
                          String message,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                          LocalDateTime timestamp) {
    public ResponseDto(String result,
                       String message) {
        this(result, message, LocalDateTime.now());
    }
}
