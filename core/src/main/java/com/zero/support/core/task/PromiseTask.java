package com.zero.support.core.task;


public abstract class PromiseTask<Param, Result> extends Task<Param, Response<Result>> {
    @Override
    public Response<Result> doWork(Param input) {
        try {
            return Response.success(process(input));
        } catch (Throwable e) {
            e.printStackTrace();
            if (isCanceled()) {
                return Response.cancel(null);
            }
            return WorkExceptionConverter.convertToResponse(e);
        }
    }

    protected abstract Result process(Param input) throws Throwable;
}
