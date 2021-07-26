package com.zero.support.work;

public enum Status {
    /**
     * Indicates that the task has not been executed yet.
     */
    PENDING,

    ENQUEUED,
    /**
     * Indicates that the task is running.
     */
    RUNNING,

    PROGRESS,
    /**
     * Indicates that has finished.
     */
    FINISHED,
    COMPLETED,
    FAILED,
    CANCELED
}
