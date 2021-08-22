package com.zero.support.compat.util;

import androidx.annotation.NonNull;

import com.zero.support.compat.vo.Resource;
import com.zero.support.util.Observable;
import com.zero.support.work.AppExecutor;
import com.zero.support.work.Response;
import com.zero.support.work.SerialExecutor;
import com.zero.support.work.WorkExceptionConverter;

import java.util.concurrent.Executor;

public abstract class ResourceRequest<Param, T> {
    private final Observable<Resource<T>> resource = new Observable<>();
    private T data;
    private final Executor executor = new SerialExecutor(1);
    private boolean initialize = true;

    public Observable<Resource<T>> resource() {
        return resource;
    }

    public void notifyDataSetChanged(Param param) {
        dispatchRefresh(getBackgroundExecutor(), AppExecutor.main(), param, data);
    }

    public void notifyDataSetChanged(Executor executor, Executor postExecutor, Param param) {
        dispatchRefresh(executor, postExecutor, param, data);
    }

    private void dispatchRefresh(Executor executor, Executor postExecutor, Param param, T data) {
        postExecutor.execute(() -> {
            Resource<T> resource = Resource.from(initialize);
            onResourceChanged(resource);
            ResourceRequest.this.resource.setValue(resource);
        });
        executor.execute(() -> {
            Response<T> response = dispatchPerformExecute(param);
            onReceiveResponse(response);
            postExecutor.execute(() -> {
                ResourceRequest.this.data = response.data();
                Resource<T> resource = covertToResource(response);
                if (initialize && resource.isSuccess()) {
                    initialize = false;
                }
                onResourceChanged(resource);
                ResourceRequest.this.resource.setValue(resource);
            });
        });
    }


    protected void onResourceChanged(Resource<T> resource) {

    }

    protected void onReceiveResponse(Response<T> response) {

    }

    private Resource<T> covertToResource(Response<T> response) {
        return Resource.from(response,initialize);
    }

    public boolean isInitialize() {
        return initialize;
    }

    protected Executor getBackgroundExecutor() {
        return executor;
    }

    protected @NonNull
    Response<T> dispatchPerformExecute(Param param) {
        try {
            return Response.success(performExecute(param));
        } catch (Throwable e) {
            return Response.error(WorkExceptionConverter.convert(e), e);
        }
    }

    protected abstract T performExecute(Param param) throws Throwable;
}
