package com.zero.support.xx.work.exception;

import java.io.IOException;

public class StorageOverFlowException extends IOException {
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
}
