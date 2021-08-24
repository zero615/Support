package com.zero.easy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zero.easy.R;


/**
 * 实现基于父控件宽高的百分比来设定宽高. 宽高为WRAP_CONTENT时生效。
 */
public class AspectRatioLayout extends FrameLayout {

    private static final int NO_MAX_HEIGHT = -1;

    private float mAspectRatio;
    private float mHeightRatio = 1f;
    private float mWidthRatio = 1f;
    private int mMaxHeight = NO_MAX_HEIGHT;


    public AspectRatioLayout(Context context) {
        this(context, null, 0);
    }

    public AspectRatioLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs == null) {
            return;
        }
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.AspectRatioLayout, defStyle, 0);
        mAspectRatio = a.getFloat(R.styleable.AspectRatioLayout_aspectRatio, 0);
        mWidthRatio = a.getFloat(R.styleable.AspectRatioLayout_layout_widthRatio, 1);
        mHeightRatio = a.getFloat(R.styleable.AspectRatioLayout_layout_heightRatio, 1);
        mMaxHeight = a.getDimensionPixelSize(R.styleable.AspectRatioLayout_aspectMaxHeight, NO_MAX_HEIGHT);
        a.recycle();
    }

    public void setAspectRatio(float aspectRatio) {
        mAspectRatio = aspectRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (mAspectRatio != 0) {
            height = (int) (width / mAspectRatio);
            if (mMaxHeight > 0) {
                height = Math.min(height, mMaxHeight);
            }
            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else {
            if (mWidthRatio != 1f && widthMode == MeasureSpec.AT_MOST) {
                width = (int) (width * mWidthRatio);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            }
            if (mHeightRatio != 1f && heightMode == MeasureSpec.AT_MOST) {
                height = (int) (height * mHeightRatio);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }
            if (mAspectRatio != 0 && heightMode == MeasureSpec.AT_MOST) {
                height = (int) (width / mAspectRatio);
                if (mMaxHeight > 0) {
                    height = Math.min(height, mMaxHeight);
                }
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
