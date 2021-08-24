package com.zero.support.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.zero.support.R;


public class SlideBar extends View {

    public static final String[] LETTERS = {"A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z", "#"};

    public String[] letters = LETTERS;

    private final Paint paint = new Paint();
    private Paint focusPaint;


    private int index = -1;

    private int textColor = Color.BLACK;

    private int focusTextColor = Color.MAGENTA;

    private int focusBackgroundColor = Color.LTGRAY;

    private boolean isTouch;

    private int textSize;

    private Rect rectBound;

    private OnTouchLetterChangeListener onTouchLetterChangeListener;

    private int cellWidth;
    private int cellHeight;
    int cellStartX;
    int cellStartY;

    public SlideBar(Context context) {
        super(context);
        init(context, null);
    }

    public SlideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectBound = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideBar);
        textSize = a.getDimensionPixelSize(R.styleable.SlideBar_android_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        textColor = a.getColor(R.styleable.SlideBar_android_textColor, Color.BLACK);
        focusTextColor = a.getColor(R.styleable.SlideBar_slideBarFocusTextColor, Color.MAGENTA);
        focusBackgroundColor = a.getColor(R.styleable.SlideBar_slideBarFocusBackgroundColor, Color.LTGRAY);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextAlign(Align.CENTER);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        focusPaint = new Paint(paint);
        focusPaint.setColor(focusBackgroundColor);
        a.recycle();
        paint.getTextBounds("#", 0, 1, rectBound);

        cellHeight = rectBound.bottom - rectBound.top;
        cellWidth = rectBound.right - rectBound.left;
    }


    public void setFocusBackgroundColor(int color) {
        this.focusBackgroundColor = color;

    }

    public void setTextSize(int size) {
        this.textSize = size;
    }

    public String[] getLetters() {
        return letters;
    }


    public void setLetters(String[] letters) {
        this.letters = letters;
    }

    public void setOnTouchLetterChangeListener(OnTouchLetterChangeListener onTouchLetterChangeListenner) {
        this.onTouchLetterChangeListener = onTouchLetterChangeListenner;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        cellStartX = getWidth() / 2;
        cellStartY = (getHeight() / letters.length - cellHeight) / 2;
    }

    public int getIndex() {
        return index;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int cellRange = getHeight() / letters.length;
        for (int i = 0; i < letters.length; i++) {
            canvas.translate(0, i * cellRange);
            if (i == index && isTouch) {
                paint.setColor(focusTextColor);
                canvas.drawCircle(getWidth() / 2f, cellRange / 2f, Math.max(cellWidth, cellHeight), focusPaint);
            } else {
                paint.setColor(textColor);
            }
            canvas.drawText(letters[i], cellStartX, cellStartY + cellHeight, paint);
            canvas.translate(0, -i * cellRange);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int index = (int) (event.getY() / getHeight() * letters.length);
        int oldIndex = this.index;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isTouch = true;
                if (index != oldIndex && index >= 0 && index < letters.length) {
                    this.index = index;
                    if (onTouchLetterChangeListener != null) {
                        onTouchLetterChangeListener.onTouchLetterChange(true, letters[index]);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if (index >= 0 && index < letters.length) {
                    if (onTouchLetterChangeListener != null) {
                        onTouchLetterChangeListener.onTouchLetterChange(false, letters[index]);
                    }
                }
                this.index = -1;
                invalidate();
                break;

            default:
                break;
        }
        return true;
    }

    public interface OnTouchLetterChangeListener {

        void onTouchLetterChange(boolean isTouch, String letter);
    }

}
