package com.zero.support.core.exception;

import com.zero.support.core.WorkException;
import com.zero.support.core.task.WorkErrorCode;

public class FileVerifyException extends Exception implements WorkException {
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

    @Override
    public int getErrorCode() {
        return WorkErrorCode.FILE_VERIFY_ERROR;
    }
}
