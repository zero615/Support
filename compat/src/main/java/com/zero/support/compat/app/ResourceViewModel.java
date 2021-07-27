package com.zero.support.compat.app;


import com.zero.support.compat.vo.Resource;
import com.zero.support.work.Observable;
import com.zero.support.work.Response;

public abstract class ResourceViewModel<Param, Result> extends SupportViewModel {
    private final ResourceRequest<Param, Result> request = new ResourceRequest<Param, Result>() {
        @Override
        protected Response<Result> performExecute(Param param) {
            return ResourceViewModel.this.performExecute(param);
        }

        @Override
        protected void onReceiveResponse(Response<Result> response) {
            ResourceViewModel.this.onReceiveResponse(response);
        }
    };

    protected void onReceiveResponse(Response<Result> response) {
    }

    public Observable<Resource<Result>> resource() {
        return request.resource();
    }

    public void notifyDataSetChanged(Param param) {
        request.notifyDataSetChanged(param);
    }

    protected abstract Response<Result> performExecute(Param param);

}
