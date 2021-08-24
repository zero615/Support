package com.zero.support.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;

import java.lang.reflect.Method;

public class FaKeLooper implements Runnable {
    private boolean exit;
    private Method next;
    private Method recycleUnchecked;

    public FaKeLooper() {
        try {
            next = MessageQueue.class.getDeclaredMethod("next");
            next.setAccessible(true);
            recycleUnchecked = Message.class.getDeclaredMethod("recycleUnchecked");
            recycleUnchecked.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void install() {
        new Handler(Looper.getMainLooper()).post(new FaKeLooper());
    }


    @Override
    public void run() {
        final Looper me = Looper.myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        Looper looper;
        final MessageQueue queue;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            queue = me.getQueue();
        } else {
            queue = Looper.myQueue();
        }
        Message msg;

        for (; ; ) {
            if (exit) {
                break;
            }
            msg = call(next, queue); // might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }

            Handler handler = msg.getTarget();
            if (handler == null) {
                continue;
            }
            handler.dispatchMessage(msg);
            call(recycleUnchecked, msg);
        }
    }

    public <T> T call(Method method, Object o, Object... param) {
        try {
            return (T) method.invoke(o, param);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public void exit() {
        exit = true;
    }
}