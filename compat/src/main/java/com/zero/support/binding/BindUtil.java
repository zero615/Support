package com.zero.support.binding;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.bumptech.glide.Glide;
import com.zero.support.compat.BR;
import com.zero.support.compat.vo.Resource;
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


}
