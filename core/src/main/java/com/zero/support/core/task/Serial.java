package com.zero.support.core.task;

public class Serial<X, Y> {
    private Class<? extends Task<X, Y>> task;
    private final X param;
    private final Serial<?, X> parent;
    private Serial<Y, ?> next;

    public Serial(X param, Class<? extends Task<X, Y>> task, Serial<?, X> serial) {
        this.param = param;
        this.task = task;
        this.parent = serial;
        if (serial != null) {
            serial.next = this;
        }
    }


    public static <X, Y> Serial<X, Y> create(X param, Class<? extends Task<X, Y>> task) {
        return new Serial<>(param, task, null);
    }

    public void run() {
        @SuppressWarnings("ALL")
        Serial serial = this;
        while (serial.parent != null) {
            serial = serial.parent;
        }
        //run -> next

    }
}
