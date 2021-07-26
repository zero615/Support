package com.zero.support.compat.window;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class WindowParentLayout extends FrameLayout {
    private final BaseWindow window;
    private final GestureDetector detector;
    private int widthPixels;
    private int heightPixels;

    public WindowParentLayout(@NonNull Context context, final BaseWindow window) {
        super(context);
        this.window = window;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;

        detector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            private int originDistanceX;
            private int originDistanceY;

            @Override
            public boolean onDown(MotionEvent e) {
                originDistanceX = (int) (e.getRawX() - window.params.x);
                originDistanceY = (int) (e.getRawY() - window.params.y);
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                window.onClickEvent(e);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                int x = (int) (e2.getRawX() - originDistanceX);
                int y = (int) (e2.getRawY() - originDistanceY);
                if (isOutSideX(x)) {
                    originDistanceX = (int) (e2.getRawX() - window.params.x);
                } else {
                    window.params.x = x;
                }
                if (isOutSideY(y)) {
                    originDistanceY = (int) (e2.getRawY() - window.params.y);
                } else {
                    window.params.y = y;
                }
                window.params.x = x;
                window.params.y = y;
                window.update();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return true;
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        window.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        window.onAttachedToWindow();
    }

    private boolean isOutSideY(int y) {
        if (y + getHeight() > heightPixels) {
            return true;
        }
        return y < 0;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    private boolean isOutSideX(int x) {
        if (x + getWidth() > widthPixels) {
            return true;
        }
        return x < 0;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        fixLocation();
    }

    public void fixLocation() {
        int x = window.params.x;
        int y = window.params.y;
        if (x + getWidth() > widthPixels) {
            x = widthPixels - getWidth();
        }
        if (y + getHeight() > heightPixels) {
            y = heightPixels - getHeight();
        }
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        window.params.x = x;
        window.params.y = y;
        window.update();
    }
}
