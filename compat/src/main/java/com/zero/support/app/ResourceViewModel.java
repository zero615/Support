package com.zero.support.app;


import androidx.databinding.ObservableBoolean;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zero.support.compat.util.ResourceRequest;
import com.zero.support.vo.Resource;
import com.zero.support.core.observable.Observable;
import com.zero.support.core.task.Response;

public abstract class ResourceViewModel<Param, Result> extends SupportViewModel implements SwipeRefreshLayout.OnRefreshListener {

    private final ObservableBoolean refreshing = new ObservableBoolean();

    public ObservableBoolean getRefreshing() {
        return refreshing;
    }

    @Override
    protected void onAttachActivity(SupportActivity activity) {
        super.onAttachActivity(activity);

    }

    @Override
    protected void onAttachFragment(SupportFragment fragment) {
        super.onAttachFragment(fragment);
    }

    private final ResourceRequest<Param, Result> request = new ResourceRequest<Param, Result>() {
        @Override
        protected Result performExecute(Param param) throws Throwable {
            return ResourceViewModel.this.performExecute(param);
        }

        @Override
        protected void onReceiveResponse(Response<Result> response) {
            ResourceViewModel.this.onReceiveResponse(response);
        }

        @Override
        protected void onResourceChanged(Resource<Result> resource) {
            refreshing.set(resource.isLoading());
            ResourceViewModel.this.onResourceChanged(resource);
        }
    };

    public boolean isRequested() {
        return request.isRequested();
    }

    public final boolean isInitialize() {
        return request.isInitialize();
    }

    protected void onReceiveResponse(Response<Result> response) {
    }

    protected void onResourceChanged(Resource<Result> resource) {

    }

    public Observable<Resource<Result>> resource() {
        return request.resource();
    }

    public void notifyDataSetChanged(Param param) {
        request.notifyDataSetChanged(param);
    }

    protected abstract Result performExecute(Param param) throws Throwable;

    @Override
    public void onRefresh() {
        notifyDataSetChanged(null);
    }
}
