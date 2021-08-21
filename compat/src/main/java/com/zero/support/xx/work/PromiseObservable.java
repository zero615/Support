package com.zero.support.xx.work;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 保证所有的事件都会被消费, 消费完后返回空值
 *
 * @param <T>
 */
public class PromiseObservable<T> extends SerialObservable<T> {
    private final LinkedList<T> promises = new LinkedList<>();
    private T mValue;


    public List<T> values() {
        return promises;
    }

    public PromiseObservable() {
        super(AppExecutor.main());
    }

    public PromiseObservable(Executor executor) {
        super(executor);
    }

    public synchronized boolean remove(T t) {
        if (t == mValue) {
            mValue = null;
            promiseNext();
            return true;
        }
        return promises.remove(t);
    }

    private void promiseNext() {
        if (mValue == null) {
            if (promises.size() != 0) {
                mValue = promises.removeLast();
            }
            super.setValue(mValue);
        }
    }

    @Override
    public synchronized void setValue(T value) {
        //super.setValue(value);
        promise(value,false);
    }

    public synchronized void postAtFront(T value){
        promise(value,true);
    }

    private synchronized void promise(T value,boolean front) {
        if (front){
            promises.addLast(value);
        }else {
            promises.addFirst(value);
        }
        promiseNext();
    }


    public boolean contains(T value) {
        if (value == mValue) {
            return true;
        }
        return promises.contains(value);
    }
}
