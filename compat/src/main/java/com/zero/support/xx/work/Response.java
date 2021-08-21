package com.zero.support.xx.work;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Response<T> implements Parcelable {
    private int code;
    private T data;
    private String message;
    private transient Throwable throwable;
    private static final String SUCCESS_MESSAGE = "success";
    private static final String CANCELED_MESSAGE = "cancel";
    private static final String UNKNOWN_MESSAGE = "unknown";


    private Response() {
    }

    protected Response(Parcel in) {
        code = in.readInt();
        message = in.readString();
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

    public static <T> Response<T> error(int code, String message) {
        return Response.error(code, message, null);
    }

    public static <T> Response<T> error(int code) {
        return Response.error(code, UNKNOWN_MESSAGE, null);
    }

    public static <T> Response<T> error(int code, Throwable throwable) {
        return Response.error(code, String.valueOf(throwable), throwable);
    }

    public static <T> Response<T> error(ResponseException exception) {
        return Response.error(exception.code(), exception.message(), exception);
    }

    public static <T> Response<T> error(int code, String message, Throwable throwable) {
        Response<T> response = new Response<>();
        response.code = code;
        response.message = message;
        response.throwable = throwable;
        return response;
    }

    public static <T> Response<T> error(int code, String message, T data) {
        Response<T> response = new Response<>();
        response.code = code;
        response.message = message;
        response.data = data;
        return response;
    }

    public static <T> Response<T> error(Response<?> error) {
        Response<T> response = new Response<>();
        response.code = error.code;
        response.message = error.message;
        response.throwable = error.throwable;
        return response;
    }

    public static <T> Response<T> cancel(T data) {
        Response<T> response = new Response<>();
        response.code = 1;
        response.message = CANCELED_MESSAGE;
        response.data = data;
        return response;
    }

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.code = 0;
        response.message = SUCCESS_MESSAGE;
        response.data = data;
        return response;
    }


    public static Response<Bundle> from(Bundle bundle) {
        int code = bundle.getInt("code");
        String message = bundle.getString("message");
        Bundle data = bundle.getBundle("data");
        return Response.error(code, message, data);
    }

    public T get() throws Throwable {
        if (throwable != null) {
            throw throwable;
        }
        if (isSuccessful()) {
            return data;
        }
        throw new ResponseException(code, message);
    }

    public void printStackTrace() {
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    public void printStackTrace(PrintWriter writer) {
        if (throwable != null) {
            throwable.printStackTrace(writer);
        }
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", throwable=" + throwable +
                '}';
    }

    public boolean isSuccessful() {
        return code == 0 || code == 200;
    }

    public T data() {
        return data;
    }

    public String message() {
        return message;
    }

    public int code() {
        return code;
    }


    public String stackTrace() {
        StringWriter stringWriter = new StringWriter();
        printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.getBuffer().toString();
    }

    public Throwable getCause() {
        return throwable;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(message);
    }
}
