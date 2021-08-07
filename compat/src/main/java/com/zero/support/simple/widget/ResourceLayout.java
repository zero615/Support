package com.zero.support.simple.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.zero.support.compat.R;
import com.zero.support.compat.vo.Resource;

public class ResourceLayout extends FrameLayout {
    private FrameLayout content;
    private AppCompatImageView error;
    private AppCompatImageView empty;
    private ProgressBar progress;


    public ResourceLayout(@NonNull Context context) {
        this(context, null);
    }

    public ResourceLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ResourceLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewGroup view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.resource_layout, this, true);

        content = view.findViewById(R.id.resourceContent);
        error = view.findViewById(R.id.resourceError);
        empty = view.findViewById(R.id.resourceEmpty);
        progress = view.findViewById(R.id.resourceProgress);
        Log.e("xgf", "ResourceLayout: " + content + error + empty + progress+view.getChildCount());

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


    }

    public void setResource(Resource<?> resource) {
        if (resource == null) {
            return;
        }
        if (resource.isSuccess()) {
            if (resource.isEmpty()) {
                empty.setVisibility(VISIBLE);
                content.setVisibility(GONE);
            } else {
                empty.setVisibility(GONE);
                content.setVisibility(VISIBLE);
            }
        }
        if (resource.isError()) {
            error.setVisibility(VISIBLE);
        } else {
            error.setVisibility(GONE);
        }

        if (resource.isLoading()) {
            progress.setVisibility(VISIBLE);
        } else {
            progress.setVisibility(GONE);
        }

    }

    @BindingAdapter("resource")
    public static void setResource(ResourceLayout layout, Resource<?> resource) {
        layout.setResource(resource);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (content != null) {
            content.addView(child, index, params);
        }else {
            super.addView(child,index,params);
        }
    }
}
