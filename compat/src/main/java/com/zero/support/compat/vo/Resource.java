/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zero.support.compat.vo;


import androidx.annotation.NonNull;

import com.zero.support.core.task.Response;

import java.util.Collection;


/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T>
 */
public class Resource<T> {
    public static final int SUCCESS = 0;
    public static final int EMPTY = 1;
    public static final int LOADING = 2;
    public static final int ERROR = 3;
    @NonNull
    public final int status;

    public final Response<T> response;

    public boolean initialize;


    public boolean isInitialize() {
        return initialize;
    }

    public Resource(Response<T> response, boolean initialize) {
        this.status = response.isSuccessful() ? (isEmpty(response.data()) ? EMPTY : SUCCESS) : ERROR;
        this.response = response;
        this.initialize = initialize;
    }

    public Resource(Response<T> response) {
        this(response, false);
    }

    public Resource(boolean initialize) {
        this.status = LOADING;
        this.response = null;
        this.initialize = initialize;
    }

    public Resource() {
        this(false);
    }

    public static <T> Resource<T> from(Response<T> response) {
        return new Resource<>(response);
    }
    public static <T> Resource<T> from(Response<T> response,boolean initialize) {
        return new Resource<>(response,initialize);
    }

    public static <T> Resource<T> from(boolean initialize) {
        return new Resource<>(initialize);
    }

    public boolean isLoading() {
        return status == LOADING;
    }

    public boolean isSuccess() {
        return status == SUCCESS;
    }


    public boolean isError() {
        return status == ERROR;
    }

    public boolean isEmpty() {
        return status == EMPTY;
    }

    public Resource(int status, Response<T> response, boolean initialize) {
        this.status = status;
        this.response = response;
        this.initialize = initialize;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", response=" + response +
                ", initialize=" + initialize +
                '}';
    }

    @SuppressWarnings("ALL")
    private boolean isEmpty(T data) {
        return data == null || ((data instanceof Collection) && ((Collection) data).isEmpty());
    }

    public T data() {
        return response == null ? null : response.data();
    }
}
