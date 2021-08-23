package com.zero.support.core.observable;

import com.zero.support.core.AppExecutor;

import java.util.concurrent.Executor;

public class SerialObservable<T> extends Observable<T> {
    private Executor mExecutor;

    public SerialObservable() {
        this(AppExecutor.current());
    }

    public SerialObservable(Executor executor) {
        mExecutor = executor;
    }

    @Override
    protected void performDispatch(Observer<T> observer, Object value, int version) {
        mExecutor.execute(new PostDispatch(observer, value, version));
    }

    @Override
    protected void performObserve(Observer<T> observer, boolean removed) {
        mExecutor.execute(new PostObserve(observer, removed));
    }

    protected void onPostPerformObserver(Observer<T> observer, boolean removed) {
        dispatchObserver(observer, removed);
    }

    protected void onPostPerformDispatch(Observer<T> observer, Object value, int version) {
        dispatchValue(observer, value, version);
    }

    private class PostObserve implements Runnable {
        private final Observer<T> mObserver;
        private final boolean mRemoved;

        public PostObserve(Observer<T> observer, boolean removed) {
            mObserver = observer;
            mRemoved = removed;
        }


        @Override
        public void run() {
            onPostPerformObserver(mObserver, mRemoved);
        }
    }

    private class PostDispatch implements Runnable {
        private final Object mPostValue;
        private final Observer<T> mObserver;
        private final int mVersion;


        public PostDispatch(Observer<T> observer, Object value, int version) {
            mObserver = observer;
            mPostValue = value;
            mVersion = version;
        }


        @Override
        public void run() {
            onPostPerformDispatch(mObserver, mPostValue, mVersion);
        }
    }

}
