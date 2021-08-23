package com.zero.support.binding;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.zero.support.glide.GlideModel;

public class BindUtil {
    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, int resId) {
        view.setImageResource(resId);

    }

    @BindingAdapter("android:text")
    public static void setText(TextView view, int resId) {
        view.setText(resId);

    }


    @BindingAdapter(value = {"imageModel", "placeHolder", "error"}, requireAll = false)
    public static void loadImage(ImageView imageView, Object model, Drawable holderDrawable, Drawable errorDrawable) {
        if (model instanceof Drawable) {
            imageView.setImageDrawable((Drawable) model);
        } else if (model instanceof Integer) {
            imageView.setImageResource((Integer) model);
        } else if (model instanceof GlideModel) {
            GlideModel imageModel = (GlideModel) model;
            Glide.with(imageView.getContext())
                    .load(imageModel.model)
                    .placeholder(imageModel.placeHolder)
                    .error(imageModel.error)
                    .diskCacheStrategy(imageModel.diskCacheStrategy)
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext())
                    .load(model)
                    .placeholder(holderDrawable)
                    .error(errorDrawable)
                    .into(imageView);
        }

    }


    @BindingAdapter(value = {"onRefreshListener", "refreshingAttrChanged"}, requireAll = false)
    public static void setOnRefreshListener(final SwipeRefreshLayout view,
                                            final SwipeRefreshLayout.OnRefreshListener listener,
                                            final InverseBindingListener refreshingAttrChanged) {
        Log.d("bind", "setRefreshingListener" + listener + refreshingAttrChanged + view.isRefreshing());
        SwipeRefreshLayout.OnRefreshListener newValue = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("bind", "onRefresh: run ");
                if (refreshingAttrChanged != null) {
                    refreshingAttrChanged.onChange();
                }
                if (listener != null) {
                    listener.onRefresh();
                }
            }
        };
        if (view.isRefreshing()) {
            if (refreshingAttrChanged != null) {
                refreshingAttrChanged.onChange();
            }
//            if (listener != null) {
//                listener.onRefresh();
//            }
        }
        view.setOnRefreshListener(newValue);

    }

    @BindingAdapter("refreshing")
    public static void setRefreshing(SwipeRefreshLayout view, boolean refreshing) {
        Log.d("bind", "setRefreshing" + refreshing);
        if (refreshing != view.isRefreshing()) {
            view.setRefreshing(refreshing);
        }
    }

    @InverseBindingAdapter(attribute = "refreshing", event = "refreshingAttrChanged")
    public static boolean isRefreshing(SwipeRefreshLayout view) {
        return view.isRefreshing();
    }
}
