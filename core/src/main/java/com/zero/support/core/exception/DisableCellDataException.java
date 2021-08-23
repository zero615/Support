package com.zero.support.core.exception;

import com.zero.support.core.WorkException;
import com.zero.support.core.task.WorkErrorCode;

public class DisableCellDataException extends Exception implements WorkException {
    public DisableCellDataException() {
    }

    public DisableCellDataException(String message) {
        super(message);
    }

    public DisableCellDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DisableCellDataException(Throwable cause) {
        super(cause);
    }


    @Override
    public int getErrorCode() {
        return WorkErrorCode.DISABLE_CELL_DATA;
    }

}
