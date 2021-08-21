package com.zero.support.xx.work;


import java.util.concurrent.Executor;

public class WorkManager {

    public static WorkManager getDefault() {
        return manager;
    }

    @SuppressWarnings("ALL")
    public <T extends Task> T optTask(Class<T> task) {
        return (T) internal.opt(task);
    }

    @SuppressWarnings("ALL")
    public <T extends Task> T getTask(Class<T> task) {
        return (T) internal.get(task);
    }


    @SuppressWarnings("ALL")
    private class Internal extends TaskManager<Class, Task> {
        private Task.OnTaskEventListener listener = new Task.OnTaskEventListener() {
            @Override
            public void onStatusChanged(Task task, int status) {
                if (task.isFinished()) {
                    remove(task.getClass());
                }
            }
        };

        public Internal() {
            super(new Creator<Class, Task>() {
                @Override
                public Task creator(Class key) {
                    try {
                        return (Task) key.newInstance();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        @Override
        protected Task onCreateValue(final Class key) {
            return creator.creator(key).observerOn(dispatchExecutor).addOnTaskEventListener(listener);
        }

        @Override
        protected void onBindValue(Task value, Object extra) {
            if (!value.isExecuted()) {
                value.input(extra).run(executor);
            }
        }
    }

    private final static WorkManager manager = new WorkManager();
    private Executor executor;
    private Executor dispatchExecutor;

    public WorkManager(Executor executor, Executor dispatchExecutor) {
        this.executor = executor;
        this.dispatchExecutor = dispatchExecutor;
    }

    public WorkManager() {
        this(AppExecutor.async(), AppExecutor.main());
    }

    private final Internal internal = new Internal();

    public <T extends Task> T enqueue(Class<T> cls, Object extra) {
        return (T) internal.opt(cls, extra);
    }
}
