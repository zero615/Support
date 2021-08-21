package com.zero.support.xx.work;

public class FakeProgress implements Runnable {
    private int count;
    private Progress progress = new Progress();
    private long duration = 100;
    private final Task<?, ?> task;
    private volatile boolean running;

    public FakeProgress(Task<?, ?> task) {
        this.task = task;
        progress.init(0, 0, 100, 0, 1, 1);
    }

    public FakeProgress(int type, Task<?, ?> task) {
        this.task = task;
        progress.init(type, 0, 100, 0, 1, 1);
    }

    @Override
    public void run() {
        if (count >= 100) {
            return;
        } else if (count >= 99) {
            //ignore
            return;
        }
        count++;
        if (count > 95) {
            duration = 6000;
        } else if (count > 90) {
            duration = 5000;
        } else if (count > 75) {
            duration = 3000;
        } else if (count > 50) {
            duration = 1000;
        } else if (count > 25) {
            duration = 500;
        }
        progress.init(count);
        task.publishProgressChanged(progress);
        AppExecutor.getMainHandler().postDelayed(this, duration);
    }

    public void start() {
        if (!running) {
            running = true;
            AppExecutor.getMainHandler().removeCallbacks(this);
            AppExecutor.getMainHandler().post(this);
        }

    }

    public void stop() {
        if (running) {
            running = false;
            count = 0;
            AppExecutor.getMainHandler().removeCallbacks(this);
            progress.init(100);
            task.publishProgressChanged(progress);
        }

    }

    public static FakeProgress create(Task<?, ?> task) {
        return new FakeProgress(task);
    }
}
