package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class ApiResponse<T> {

    private String status;
    private String message;
    private T result;

    public ApiResponse() {
    }

    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
    public ApiResponse(String status, String message, T result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

}
