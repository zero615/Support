package com.zero.support.work;

import java.util.concurrent.Executor;

public class TaskManager<K, T extends Task> extends ObjectManager<K, T> {
    private final Executor executor;
    private final Executor dispatchExecutor;

    @SuppressWarnings("ALL")
    private Task.OnTaskEventListener listener = new Task.OnTaskEventListener<K, Object>() {
        @Override
        public void onStatusChanged(Task<K, Object> task, int status) {
            if (task.isFinished()) {
                remove(task.getInput());
            }
        }
    };


    public class ClassTaskCreator implements Creator<K, T> {
        private Class<T> task;

        public ClassTaskCreator(Class<T> task) {
            this.task = task;

        }

        @Override
        public T creator(K key) {
            return createTask(key, task);
        }

        private T createTask(K key, Class<T> task) {
            try {
                return task.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public TaskManager(Executor executor, Executor dispatchTask, Creator<K, T> creator) {
        this.executor = executor;
        this.dispatchExecutor = dispatchTask;
        this.creator = creator;
    }

    public TaskManager(Class<T> task) {
        this.executor = AppExecutor.async();
        this.dispatchExecutor = AppExecutor.main();
        this.creator = new ClassTaskCreator(task);
    }

    public TaskManager(Creator<K, T> creator) {
        this.executor = AppExecutor.async();
        this.dispatchExecutor = AppExecutor.main();
        this.creator = creator;
    }

    @Override
    @SuppressWarnings("ALL")
    protected T onCreateValue(K key) {
        return (T) super.onCreateValue(key).input(key).addOnTaskEventListener(listener).observerOn(dispatchExecutor);
    }

    @Override
    protected void onBindValue(T value, Object extra) {
        value.run(executor);
    }
}
