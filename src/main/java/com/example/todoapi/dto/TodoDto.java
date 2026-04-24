package com.example.todoapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class TodoDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be less than 255 characters")
        private String title;

        @Size(max = 1000, message = "Description must be less than 1000 characters")
        private String description;
    }

    @Data
    public static class UpdateRequest {
        @Size(max = 255, message = "Title must be less than 255 characters")
        private String title;

        @Size(max = 1000, message = "Description must be less than 1000 characters")
        private String description;

        private Boolean completed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private boolean completed;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagedResponse {
        private List<Response> data;
        private int page;
        private int limit;
        private long total;
    }
}