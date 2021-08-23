package com.zero.support.core.task;

import com.zero.support.core.AppExecutor;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class SerialExecutor implements Executor {
    final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
    final int mTaskCount;
    Runnable mActive;
    AtomicInteger mActiveCount = new AtomicInteger();

    public SerialExecutor() {
        this(1);
    }

    public SerialExecutor(int taskCount) {
        this.mTaskCount = taskCount;
    }

    public synchronized void execute(final Runnable r) {
        mTasks.offer(new Runnable() {
            public void run() {
                try {
                    r.run();
                } finally {
                    mActiveCount.decrementAndGet();
                    scheduleNext();
                }
            }
        });
        if (mActive == null) {
            scheduleNext();
        }
    }

    protected synchronized void scheduleNext() {
        while (mActiveCount.get() < mTaskCount && (mActive = mTasks.poll()) != null) {
            mActiveCount.addAndGet(1);
            AppExecutor.THREAD_POOL_EXECUTOR.execute(mActive);
        }
    }
}
