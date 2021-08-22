package com.zero.support.recycler.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by prayxiang on 2017/8/1.
 */

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpanCount;
    private int mSpacing;
    private boolean mIncludeEdge;

    private int fixedSize;
    private int footSize;

    public GridItemDecoration(Context context, int spanCount, float divider, boolean includeEdge) {
        this(spanCount, Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, divider, context.getResources().getDisplayMetrics())), includeEdge);
    }

    public GridItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.mSpanCount = spanCount;
        this.mSpacing = spacing;
        this.mIncludeEdge = includeEdge;
    }

    public void setFixedSize(int fixedSize) {
        this.fixedSize = fixedSize;
    }

    public void setFootSize(int footSize) {
        this.footSize = footSize;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        int count = parent.getAdapter().getItemCount();
        if (position < fixedSize) {
            return;
        }
        if (position >= count - footSize) {
            return;
        }

        position = position - fixedSize;

        int column = position % mSpanCount;
        if (this.mIncludeEdge) {
            outRect.left = this.mSpacing - column * this.mSpacing / this.mSpanCount;
            outRect.left = this.mSpacing - column * this.mSpacing / this.mSpanCount;
            outRect.right = (column + 1) * this.mSpacing / this.mSpanCount;
            if (position < this.mSpanCount) {
                outRect.top = this.mSpacing;
            }
            outRect.bottom = this.mSpacing;
        } else {
            outRect.left = column * this.mSpacing / this.mSpanCount;
            outRect.right = this.mSpacing - (column + 1) * this.mSpacing / this.mSpanCount;
            if (position >= this.mSpanCount) {
                outRect.top = this.mSpacing;
            }
        }
    }
}
