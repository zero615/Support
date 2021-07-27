package com.zero.support.compat.app;

import android.util.SparseArray;

import com.zero.support.compat.vo.Resource;
import com.zero.support.work.AppExecutor;
import com.zero.support.work.Observable;
import com.zero.support.work.Response;
import com.zero.support.work.SerialExecutor;

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
        dispatchRefresh(param, data);
    }

    private void dispatchRefresh(Param param, T data) {
        resource.setValue(Resource.loading(data));
        getBackgroundExecutor().execute(() -> {
            Response<T> response = performExecute(param);
            onReceiveResponse(response);
            Resource<T> resource = covertToResource(response);
            AppExecutor.main().execute(() -> ResourceRequest.this.resource.setValue(resource));
        });
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

    protected abstract Response<T> performExecute(Param param);
}
