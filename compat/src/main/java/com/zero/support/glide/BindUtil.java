package com.zero.support.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

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
        } else {
            Glide.with(imageView.getContext())
                    .load(model)
                    .placeholder(holderDrawable)
                    .error(errorDrawable)
                    .into(imageView);
        }

    }


}
