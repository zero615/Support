package com.zero.support.work;


import com.zero.support.work.exception.DisableCellDataException;
import com.zero.support.work.exception.FileVerifyException;
import com.zero.support.work.exception.NetworkConnectionException;
import com.zero.support.work.exception.StorageOverFlowException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WorkExceptionConverter {
    private static ExceptionConverter converter;


    public static int convert(Throwable throwable) {
        int ret = -1;
        final ExceptionConverter converter = WorkExceptionConverter.converter;
        if (converter != null) {
            ret = converter.convert(throwable);
        }
        Class<?> cls = throwable.getClass();
        if (ret == -1) {
            if (DisableCellDataException.class == cls) {
                return WorkErrorCode.DISABLE_CELL_DATA;
            } else if (StorageOverFlowException.class == cls) {
                return WorkErrorCode.STORAGE_OVER_FLOW;
            } else if (FileVerifyException.class == cls) {
                return WorkErrorCode.FILE_VERIFY_ERROR;
            } else if (NetworkConnectionException.class == cls) {
                return WorkErrorCode.NETWORK_CONNECT_ERROR;
            } else if (FileNotFoundException.class == cls) {
                return WorkErrorCode.FILE_NOT_FOUND;
            } else if (IOException.class == cls) {
                return WorkErrorCode.IO_EXCEPTION;
            }
        }
        return -1;
    }

    public static void setConverter(ExceptionConverter converter) {
        WorkExceptionConverter.converter = converter;
    }
}
