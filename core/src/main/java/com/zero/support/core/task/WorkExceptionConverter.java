package com.zero.support.core.task;


import com.zero.support.core.WorkException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WorkExceptionConverter {
    private WorkExceptionConverter currentWorkExceptionConverter;
    private static WorkExceptionConverter INSTANCE;

    public WorkExceptionConverter getCurrentWorkExceptionConverter() {
        return currentWorkExceptionConverter;
    }

    protected Response<?> convert(Throwable throwable) {
        final WorkExceptionConverter converter = getCurrentWorkExceptionConverter();
        if (converter != null) {
            return converter.convert(throwable);
        }
        return null;
    }

    @SuppressWarnings("all")
    public static<T> Response<T> convertToResponse(Throwable throwable) {
        Response<?> response = null;
        final WorkExceptionConverter converter = getDefault();
        if (converter != null) {
            response = converter.convert(throwable);
        }
        if (response != null) {
            return (Response<T>) response;
        }
        if (throwable instanceof WorkException) {
            return Response.error(((WorkException) throwable).getErrorCode(), throwable.getMessage(), throwable);
        } else if (throwable instanceof FileNotFoundException) {
            return Response.error(WorkErrorCode.FILE_NOT_FOUND, throwable.getMessage(), throwable);
        } else if (throwable instanceof IOException) {
            return Response.error(WorkErrorCode.IO_EXCEPTION, throwable.getMessage(), throwable);
        } else {
            return Response.error(-1, throwable.getMessage(), throwable);
        }
    }

    public void setCurrentWorkExceptionConverter(WorkExceptionConverter currentWorkExceptionConverter) {
        if (this.currentWorkExceptionConverter != null) {
            this.currentWorkExceptionConverter.setCurrentWorkExceptionConverter(currentWorkExceptionConverter);
        } else {
            this.currentWorkExceptionConverter = currentWorkExceptionConverter;
        }
    }

    public synchronized static void setDefaultWorkExceptionConverter(WorkExceptionConverter converter) {
        if (INSTANCE == null) {
            INSTANCE = converter;
        } else {
            INSTANCE.setCurrentWorkExceptionConverter(converter);
        }
    }

    private static WorkExceptionConverter getDefault() {
        return INSTANCE;
    }


}