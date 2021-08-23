package com.zero.support.core.exception;

import com.zero.support.core.WorkException;
import com.zero.support.core.task.WorkErrorCode;

import java.io.IOException;

public class StorageOverFlowException extends IOException implements WorkException {
    public StorageOverFlowException() {
    }

    public StorageOverFlowException(String message) {
        super(message);
    }

    public StorageOverFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageOverFlowException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getErrorCode() {
        return WorkErrorCode.STORAGE_OVER_FLOW;
    }
}
