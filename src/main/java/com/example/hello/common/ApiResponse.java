package com.example.hello.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int code;
    private String msg;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(1, "success", data);
    }

    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(1, msg, data);
    }

    public static <T> ApiResponse<T> fail(String msg) {
        return new ApiResponse<>(0, msg, null);
    }

    public static <T> ApiResponse<T> of(int code, String msg, T data) {
        return new ApiResponse<>(code, msg, data);
    }
}

