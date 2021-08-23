package com.zero.support.core;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class AppExecutor {
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    //    private static final int MAXIMUM_POOL_SIZE = /*CPU_COUNT * 2 + 1*/128;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "IntentRequest #" + mCount.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new SynchronousQueue<>();
    private static MainExecutor sMainExecutor = new MainExecutor();
    private static Executor sContextExecutor = new ContextExecutor();
    private static Executor sBackgroundExecutor = new BackgroundExecutor();
    private static Executor sPostMainExecutor = new PostMainExecutor();
    private static Executor sAsyncMainExecutor = new AsyncExecutor();

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, Integer.MAX_VALUE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    private AppExecutor() {

    }

    public static Handler getMainHandler() {
        return sMainExecutor;
    }

    /**
     * 主线程执行器，在主线程执行时等价于Runnable.run();
     */
    public static Executor main() {
        return sMainExecutor;
    }


    /**
     * 上下文执行器，等价于Runnable.run();
     */
    public static Executor current() {
        return sContextExecutor;
    }

    public static Executor async() {
        return sAsyncMainExecutor;
    }

    public static Executor background() {
        return sBackgroundExecutor;
    }

    /**
     * 使用 postMain 需要确保不会在主线程执行wait方法
     */
    public static Executor postMain() {
        return sPostMainExecutor;
    }


    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }


    public static class ContextExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            if (command != null) {
                command.run();
            }
        }
    }

    public static class MainExecutor extends Handler implements Executor {

        public MainExecutor() {
            super(Looper.getMainLooper());
        }

        @Override
        public void execute(Runnable command) {
            if (isMainThread()) {
                command.run();
            } else {
                post(command);
            }
        }

    }

    public static class PostMainExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            getMainHandler().post(command);
        }
    }

    public static class BackgroundExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            if (!isMainThread()) {
                command.run();
            } else {
                THREAD_POOL_EXECUTOR.execute(command);
            }
        }
    }

    public static class AsyncExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            THREAD_POOL_EXECUTOR.execute(command);
        }
    }
}
