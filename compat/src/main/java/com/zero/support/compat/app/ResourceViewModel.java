package com.zero.support.compat.app;


import com.zero.support.compat.util.ResourceRequest;
import com.zero.support.compat.vo.Resource;
import com.zero.support.work.Observable;
import com.zero.support.work.Response;

public abstract class ResourceViewModel<Param, Result> extends SupportViewModel {
    private final ResourceRequest<Param, Result> request = new ResourceRequest<Param, Result>() {
        @Override
        protected Result performExecute(Param param) {
            return ResourceViewModel.this.performExecute(param);
        }

        @Override
        protected void onReceiveResponse(Response<Result> response) {
            ResourceViewModel.this.onReceiveResponse(response);
        }

        @Override
        protected void onResourceChanged(Resource<Result> resource) {
            ResourceViewModel.this.onResourceChanged(resource);
        }
    };

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

    protected abstract Result performExecute(Param param);

}
