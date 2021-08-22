package com.zero.support.util;

import androidx.lifecycle.LiveData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Observable<T> {
    private static final Object NOT_SET = new Object();
    private static final int START_VERSION = -1;
    private Object mValue = NOT_SET;
    private final Map<Observer<T>, ObserverWrapper> mObservers = new LinkedHashMap<>();
    private int mVersion = START_VERSION;

    public Map<Observer<T>, ObserverWrapper> peekObservers() {
        return mObservers;
    }

    private volatile ObservableLiveData<T> liveData;
    private volatile ObservableFuture<T> future;

    @SuppressWarnings("ALL")
    public synchronized T getValue() {
        if (mValue == NOT_SET) {
            return null;
        }
        return (T) mValue;
    }

    public synchronized void setValue(T value) {
        mVersion++;
        performDispatch(null, value, mVersion);
    }

    protected void performDispatch(Observer<T> observer, Object value, int version) {
        dispatchValue(observer, value, version);
    }

    protected void performObserve(Observer<T> observer) {
        dispatchObserver(observer);
    }

    protected final void dispatchObserver(Observer<T> observer) {
        ObserverWrapper wrapper = mObservers.get(observer);
        if (wrapper == null) {
            wrapper = new ObserverWrapper(observer);
            mObservers.put(observer, wrapper);
        } else {
            //ignore
            return;
        }
        performDispatch(observer, mValue, mVersion);
    }

    protected final void dispatchValue(Observer<T> observer, Object value, int version) {
        if (observer == null) {
            mValue = value;
            notifyChange(version);
        } else {
            if (value != NOT_SET) {
                final ObserverWrapper wrapper = mObservers.get(observer);
                if (wrapper != null) {
                    wrapper.dispatchValue(observer, (T) value, version);
                }
            }
        }
    }

    public ObservableFuture<T> getFuture() {
        if (future == null) {
            future = new ObservableFuture<>(this);
        }
        return future;
    }

    /**
     * 该方法内部使用的是弱引用，所以不支持链式调用
     */
    public final LiveData<T> asLive() {
        if (liveData == null) {
            liveData = new ObservableLiveData<>(this, NOT_SET);
        }
        return liveData;
    }

    @SuppressWarnings("ALL")
    private void notifyChange(int version) {
        final Object value = mValue;
        if (value == NOT_SET) {
            return;
        }
        Set<Observer<T>> observers = mObservers.keySet();
        for (Observer observer : observers) {
            ObserverWrapper wrapper = mObservers.get(observer);
            if (wrapper != null) {
                wrapper.dispatchValue(observer, (T) value, version);
            }
        }
    }

    public int getVersion() {
        return mVersion;
    }

    public synchronized void reset() {
        mValue = NOT_SET;
        if (liveData != null) {
            liveData.reset();
        }
        if (future!=null){
            future.reset();
        }
    }

    public final synchronized void observe(Observer<T> observer) {
        performObserve(observer);
    }


    public synchronized void observeOnce(final Observer<T> observer) {
        observe(new Observer<T>() {
            boolean observe;

            @Override
            public void onChanged(T t) {
                remove(observer);
                if (!observe) {
                    observe = true;
                    observer.onChanged(t);
                }

            }
        });
    }

    public synchronized void remove(Observer<T> observer) {
        mObservers.remove(observer);
    }

    public class ObserverWrapper {
        private int mLastVersion = START_VERSION;
        private final Observer<T> mObserver;

        public ObserverWrapper(Observer<T> observer) {
            mObserver = observer;
        }

        public void dispatchValue(Observer<T> observer, T value, int version) {
            if (mLastVersion == version) {
                return;
            }
            mLastVersion = version;
            observer.onChanged(value);
        }
    }


}