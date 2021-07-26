package com.zero.support.work;

import java.util.concurrent.Executor;

public interface Operation<T> {


    public T get();

    public T peek();

    Operation<T> status(Observer<Status> observer);

    Operation<T> observe(Observer<T> observer);

    Operation<T> progress(Observer<Progress> observer);

    Operation<T> observeOn(Executor executor);

    void remove(Observer<T> observer);

    Status getStatus();

    boolean isFinished();

    boolean cancel(boolean mayInterruptIfRunning);

    T awaitDone() throws Exception;

    boolean isCanceled();
}
