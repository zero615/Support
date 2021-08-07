package com.zero.tools.resource.exception;

public class UndefinedResObjectException extends ResourceException {
    public UndefinedResObjectException() {
    }

    public UndefinedResObjectException(String message) {
        super(message);
    }

    public UndefinedResObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndefinedResObjectException(Throwable cause) {
        super(cause);
    }
}
