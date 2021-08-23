package com.zero.support.core.task;

import java.util.concurrent.Executor;

public class SimpleTaskManager extends TaskManager<Class<? extends Task<?, ?>>, Task<?, ?>> {

    public SimpleTaskManager(Executor executor, Executor dispatchTask, Class<? extends Task<?, ?>> cls) {
        super(executor, dispatchTask, new ClassTaskCreator<>(cls));
    }

    public SimpleTaskManager(Class<? extends Task<?, ?>> task) {
        super(new ClassTaskCreator<>(task));
    }

    public static class ClassTaskCreator<T> implements ObjectManager.Creator<Class<? extends Task<?, ?>>, Task<?, ?>> {
        private final Class<T> task;

        public ClassTaskCreator(Class<T> task) {
            this.task = task;
        }

        private Task<?, ?> createTask(Class<T> task) {
            try {
                return (Task<?, ?>) task.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Task<?, ?> creator(Class<? extends Task<?, ?>> key) {
            return createTask(task);
        }
    }


}