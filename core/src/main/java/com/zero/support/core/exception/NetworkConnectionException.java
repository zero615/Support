package com.zero.support.core.exception;

import com.zero.support.core.WorkException;
import com.zero.support.core.task.WorkErrorCode;

import java.io.IOException;

public class NetworkConnectionException extends IOException implements WorkException {
    public NetworkConnectionException() {
    }

    public NetworkConnectionException(String message) {
        super(message);
    }

    public NetworkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkConnectionException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getErrorCode() {
        return WorkErrorCode.NETWORK_CONNECT_ERROR;
    }
}
