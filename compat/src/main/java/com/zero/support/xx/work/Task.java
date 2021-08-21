package com.zero.support.xx.work;

import android.os.Binder;
import android.os.ConditionVariable;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Task<Param, Result> {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_ENQUEUED = 1;

    public static final int STATUS_RUNNING = 2;

    public static final int STATUS_COMPLETED = 3;

    public static final int STATUS_FAILED = 4;

    public static final int STATUS_CANCELED = 5;

    public static final int REQUEST_SCHEDULE = -1;

    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();
    private final ConditionVariable mVariable = new ConditionVariable();
    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private Executor dispatchExecutor = AppExecutor.current();
    private volatile int status;
    private final Observable<Result> result = new TaskObservable<>();
    private final Observable<Progress> progress = new TaskObservable<>();
    private volatile Throwable throwable;
    private final WorkerRunnable runnable = new WorkerRunnable();
    private volatile Param param;
    private final List<OnTaskEventListener<Param, Result>> onTaskEventListeners = new CopyOnWriteArrayList<>();

    public interface OnTaskEventListener<Param, Result> {
        void onStatusChanged(Task<Param, Result> task, int status);
    }

    public interface OnProgressListener {
        void onProgress(Progress progress);
    }

    public Task<Param, Result> addOnTaskEventListener(final OnTaskEventListener<Param, Result> listener) {
        onTaskEventListeners.add(listener);
        if (isExecuted()) {
            final int status = this.status;
            schedule(new Runnable() {
                @Override
                public void run() {
                    if (status == Task.this.status) {
                        listener.onStatusChanged(Task.this, status);
                    }
                }
            });
        }
        return this;
    }

    public Task<Param, Result> removeOnTaskEventListener(OnTaskEventListener<Param, Result> listener) {
        onTaskEventListeners.remove(listener);
        return this;
    }

    public Task() {
        progress.observe(new Observer<Progress>() {
            @Override
            public void onChanged(Progress progress) {
                onProgressChanged(progress);
            }
        });
    }

    public Task<Param, Result> observerOn(Executor executor) {
        dispatchExecutor = executor;
        return this;
    }

    public void schedule(Runnable runnable) {
        dispatchExecutor.execute(runnable);
    }

    public int getStatus() {
        return status;
    }


    public boolean cancel(boolean interrupt) {
        if (mCancelled.get()) {
            if (interrupt) {
                runnable.cancel();
            }
            return false;
        }
        mCancelled.set(true);
        if (interrupt) {
            runnable.cancel();
        }
        return true;
    }

    public boolean isExecuted() {
        return mTaskInvoked.get();
    }


    public boolean isCanceled() {
        return mCancelled.get();
    }


    public final Result awaitDone() throws Exception {
        if (isExecuted()) {
            mVariable.block();
            if (isCanceled()) {
                throw new CancellationException();
            }
            if (throwable != null) {
                throw new ExecutionException(throwable);
            }
            return result.getValue();
        } else {
            throw new IllegalStateException("task is not execute");
        }
    }

    public Result getResult() {
        mVariable.block();
        return result.getValue();
    }

    public Observable<Result> result() {
        return result;
    }

    public Observable<Progress> progress() {
        return progress;
    }

    public Result peek() {
        return result.getValue();
    }

    public Param getInput() {
        return param;
    }

    public Task<Param, Result> input(Param param) {
        this.param = param;
        return this;
    }


    public void execute(Executor executor) {
        if (mTaskInvoked.get()) {
            return;
        }
        mTaskInvoked.set(true);
        dispatchExecutor.execute(new PostRunnable(STATUS_ENQUEUED, null));
        executor.execute(runnable);
    }


    public Task<Param, Result> run() {
        execute(AppExecutor.async());
        return this;
    }

    public Task<Param, Result> run(Executor executor) {
        execute(executor);
        return this;
    }


    public abstract Result doWork(Param param);

    private class WorkerRunnable implements Runnable {
        private volatile Thread thread;

        @Override
        public void run() {
            thread = Thread.currentThread();
            mTaskInvoked.set(true);
            if (isCanceled()) {
                return;
            }
            dispatchExecutor.execute(new PostRunnable(STATUS_RUNNING, null));
            Result result = null;
            Throwable throwable = null;
            try {
                result = doWork(param);
                mVariable.open();
                Binder.flushPendingCommands();
            } catch (Throwable tr) {
                throwable = tr;
            } finally {
                if (isCanceled()) {
                    dispatchExecutor.execute(new PostRunnable(STATUS_CANCELED, result));
                } else if (throwable != null) {
                    dispatchExecutor.execute(new PostRunnable(STATUS_FAILED, throwable));
                } else {
                    dispatchExecutor.execute(new PostRunnable(STATUS_COMPLETED, result));
                }
            }
        }

        public void cancel() {
            if (thread != null) {
                thread.interrupt();
            }
        }
    }

    public void onCancel(Result result) {

    }

    public void onError(Throwable throwable) {

    }

    public void onSuccess(Result result) {

    }

    @SuppressWarnings("ALL")
    public void dispatchChanged(int status, Object result) {
        if (status != REQUEST_SCHEDULE) {
            this.status = status;
        }
        if (status == STATUS_COMPLETED) {
            this.result.setValue((Result) result);
            mVariable.open();
            onSuccess((Result) result);
        } else if (status == STATUS_CANCELED) {
            this.result.setValue((Result) result);
            mVariable.open();
            onCancel((Result) result);
        } else if (status == STATUS_FAILED) {
            this.throwable = (Throwable) result;
            mVariable.open();
            onError(throwable);
        }
        if (status != REQUEST_SCHEDULE) {
            for (OnTaskEventListener listener : onTaskEventListeners) {
                listener.onStatusChanged(this, status);
            }
            onStatusChanged(status);
        }
    }


    public boolean isFinished() {
        return status >= STATUS_COMPLETED;
    }

    public void onStatusChanged(int status) {

    }

    public void onProgressChanged(Progress progress) {

    }

    public void publishProgressChanged(Progress progress) {
        this.progress.setValue(progress);
    }

    private class PostRunnable implements Runnable {
        private final int status;
        private final Object value;

        public PostRunnable(int status, Object value) {
            this.status = status;
            this.value = value;
        }

        @Override
        public void run() {
            dispatchChanged(status, value);
        }
    }


    private class TaskObservable<T> extends Observable<T> {
        @Override
        protected synchronized void performObserve(final Observer<T> observer) {
            if (dispatchExecutor != AppExecutor.current()) {
                dispatchExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        dispatchObserver(observer);
                    }
                });
            }else {
                dispatchObserver(observer);
            }
        }
    }

}
