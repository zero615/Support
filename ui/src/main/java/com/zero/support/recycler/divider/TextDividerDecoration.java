package com.zero.support.recycler.divider;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TextDividerDecoration extends RecyclerView.ItemDecoration {

    private Paint paint = new Paint();

    private Rect bounds = new Rect();
    private String text;
    private Paint.FontMetrics metrics;


    public TextDividerDecoration(String text, int color, float size) {
        this.text = text;
        paint.setTextSize(size);
        paint.setColor(color);
        paint.setAntiAlias(true);
        metrics = paint.getFontMetrics();
        paint.getTextBounds(text, 0, text.length(), bounds);
    }


    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        canvas.save();
        float height = (parent.getHeight() - bounds.bottom + bounds.top) / 2f;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            canvas.drawText(text, child.getRight(), parent.getBottom() - height, paint);
            canvas.drawRect(bounds, paint);
        }
        canvas.restore();
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAt(parent.getChildCount()) != view) {
            outRect.set(bounds);
        }
    }
}
