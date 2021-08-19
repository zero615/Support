package com.zero.support.compat.util;

import android.util.SparseArray;

import com.zero.support.compat.vo.Resource;
import com.zero.support.work.AppExecutor;
import com.zero.support.work.Observable;
import com.zero.support.work.Response;
import com.zero.support.work.SerialExecutor;
import com.zero.support.work.WorkExceptionConverter;

import java.util.Collection;
import java.util.concurrent.Executor;

public abstract class ResourceRequest<Param, T> {
    private final Observable<Resource<T>> resource = new Observable<>();
    private T data;
    private final Executor executor = new SerialExecutor(1);

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
            Resource<T> resource = Resource.loading(data);
            onResourceChanged(resource);
            ResourceRequest.this.resource.setValue(resource);
        });
        executor.execute(() -> {
            Response<T> response = dispatchPerformExecute(param);
            onReceiveResponse(response);
            Resource<T> resource = covertToResource(response);
            postExecutor.execute(() -> {
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
        Resource<T> resource;
        if (response.isSuccessful()) {
            resource = Resource.success(response.data());
        } else {
            resource = Resource.error(response);
        }
        return resource;
    }

    public boolean isEmptyData(T data) {
        if (data == null) {
            return true;
        }
        if (data instanceof Collection) {
            return ((Collection<?>) data).size() == 0;
        }
        if (data instanceof SparseArray) {
            return ((SparseArray<?>) data).size() == 0;
        }
        return false;
    }

    public boolean hasCache() {
        return data != null;
    }

    protected Executor getBackgroundExecutor() {
        return executor;
    }

    protected Response<T> dispatchPerformExecute(Param param) {
        try {
            return Response.success(performExecute(param));
        } catch (Throwable e) {
            return Response.error(WorkExceptionConverter.convert(e), e);
        }
    }

    protected abstract T performExecute(Param param);
}
