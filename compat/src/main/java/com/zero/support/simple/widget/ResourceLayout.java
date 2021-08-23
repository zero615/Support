package com.zero.support.simple.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.zero.support.compat.R;
import com.zero.support.compat.vo.Resource;

public class ResourceLayout extends FrameLayout {

    private int currentStatus;
    private SparseArray<View> mViews = new SparseArray<>();

    public ResourceLayout(@NonNull Context context) {
        this(context, null);
    }

    public ResourceLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view;
        View content;
        view = findViewById(R.id.viewContent);
        content = view;
        mViews.put(Resource.SUCCESS, view);
        view = findViewById(R.id.viewEmpty);
        if (view == null) {
            view = content;
        }
        mViews.put(Resource.EMPTY, view);
        view = findViewById(R.id.viewError);
        if (view == null) {
            view = content;
        }
        mViews.put(Resource.ERROR, view);
        view = findViewById(R.id.viewLoading);
        if (view == null) {
            view = content;
        }
        mViews.put(Resource.LOADING, view);
        setCurrentStatus(Resource.EMPTY);
    }

    public void setCurrentStatus(int status) {
        View view = mViews.get(status);
        if (view == null) {
            return;
        }
        currentStatus = status;
        for (int i = 0; i < mViews.size(); i++) {
            view = mViews.valueAt(i);
            if (view == null) {
                continue;
            }
            if (mViews.keyAt(i) != status) {
                view.setVisibility(GONE);
            }
        }
        view = mViews.get(currentStatus);
        if (view != null) {
            view.setVisibility(VISIBLE);
        }

    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setResource(Resource<?> resource) {
        if (resource == null) {
            return;
        }
        if (resource.isInitialize()) {
            setCurrentStatus(resource.status);
        } else if (currentStatus != Resource.SUCCESS) {
            setCurrentStatus(resource.status);
        }
    }

    @BindingAdapter("resource")
    public static void setResource(ResourceLayout layout, Resource<?> resource) {
        layout.setResource(resource);
    }
}
