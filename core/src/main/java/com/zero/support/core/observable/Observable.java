package com.zero.support.core.observable;

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
    private boolean active;

    public Map<Observer<T>, ObserverWrapper> peekObservers() {
        return mObservers;
    }

    private volatile ObservableLiveData<T> liveData;

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

    protected void performObserve(Observer<T> observer, boolean removed) {
        dispatchObserver(observer, removed);
    }

    protected final synchronized void dispatchObserver(Observer<T> observer, boolean removed) {
        if (removed) {
            mObservers.remove(observer);
            if (active && !hasObservers()) {
                active = false;
                onInActive();
            }
            return;
        }
        ObserverWrapper wrapper = mObservers.get(observer);
        if (wrapper == null) {
            wrapper = new ObserverWrapper(observer);
            mObservers.put(observer, wrapper);
            if (!active && hasObservers()) {
                active = true;
                onActive();
            }
        } else {
            //ignore
            return;
        }
        performDispatch(observer, mValue, mVersion);
    }


    protected final synchronized void dispatchValue(Observer<T> observer, Object value, int version) {
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

    public final ObservableFuture<T> getFuture() {
        return new ObservableFuture<>(this);
    }

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
    }

    public final synchronized void observe(Observer<T> observer) {
        performObserve(observer, false);
    }


    public final void observeOnce(final Observer<T> observer) {
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

    public final synchronized void remove(Observer<T> observer) {
        ObserverWrapper wrapper = mObservers.get(observer);
        if (wrapper != null) {
            wrapper.removed = true;
        }
        performObserve(observer, true);
    }

    protected void onInActive() {

    }

    protected void onActive() {

    }

    public synchronized boolean hasObservers() {
        int count = 0;
        if (liveData != null) {
            if (liveData.hasObservers()) {
                return true;
            }
            count++;
        }
        return mObservers.size() > count;
    }

    public class ObserverWrapper {
        private int mLastVersion = START_VERSION;
        private final Observer<T> mObserver;
        private volatile boolean removed;

        public ObserverWrapper(Observer<T> observer) {
            mObserver = observer;
        }

        public void dispatchValue(Observer<T> observer, T value, int version) {
            if (mLastVersion == version || removed) {
                return;
            }
            mLastVersion = version;
            observer.onChanged(value);
        }
    }


}
