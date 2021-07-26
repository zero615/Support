package com.zero.support.work;

public class ResponseException extends Exception {
    private final int code;
    private final String message;

    public ResponseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return "ResponseException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
