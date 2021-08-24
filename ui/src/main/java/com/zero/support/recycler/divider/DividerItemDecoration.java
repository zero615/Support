package com.zero.support.recycler.divider;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by prayxiang on 2017/8/1.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private int mOrientation;
    private int divider;

    public DividerItemDecoration(Context context, int divider) {
        this(VERTICAL, Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, divider, context.getResources().getDisplayMetrics())));
    }

    public DividerItemDecoration(int divider) {
        this(VERTICAL, divider);
    }

    public DividerItemDecoration(int orientation, int divider) {
        this.divider = divider;
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, divider);
        } else {
            outRect.set(0, 0, divider, 0);
        }
    }
}
