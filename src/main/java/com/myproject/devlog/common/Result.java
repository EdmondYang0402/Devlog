package com.myproject.devlog.common;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;

    private String message;

    private T data;

    private Long timestamp;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        r.setTimestamp(System.currentTimeMillis());
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(null);
        r.setTimestamp(System.currentTimeMillis());
        return r;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}
