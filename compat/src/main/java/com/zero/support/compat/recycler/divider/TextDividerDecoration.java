package com.zero.support.compat.recycler.divider;

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


    public TextDividerDecoration(String text, int color, float size) {
        paint.setTextSize(size);
        paint.setColor(color);
        paint.getTextBounds(text, 0, text.length(), bounds);
    }


    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        c.drawText(text, 0, 0, paint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(bounds);
    }
}
