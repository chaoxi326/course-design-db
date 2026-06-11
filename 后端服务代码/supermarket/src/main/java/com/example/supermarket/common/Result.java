package com.example.supermarket.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result() {}

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.msg = ResultCode.SUCCESS.getMsg();
        r.data = data;
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> r = new Result<>();
        r.code = ResultCode.FAIL.getCode();
        r.msg = msg;
        return r;
    }

    public static <T> Result<T> fail(ResultCode code, String msg) {
        Result<T> r = new Result<>();
        r.code = code.getCode();
        r.msg = msg;
        return r;
    }

    public static <T> Result<T> unauthorized(String msg) {
        Result<T> r = new Result<>();
        r.code = ResultCode.UNAUTHORIZED.getCode();
        r.msg = msg;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.code = ResultCode.INTERNAL_ERROR.getCode();
        r.msg = msg;
        return r;
    }
}
