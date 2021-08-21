package com.zero.support.xx.work.exception;

public class FileVerifyException extends Exception {
    public FileVerifyException() {
    }

    public FileVerifyException(String message) {
        super(message);
    }

    public FileVerifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileVerifyException(Throwable cause) {
        super(cause);
    }
}
