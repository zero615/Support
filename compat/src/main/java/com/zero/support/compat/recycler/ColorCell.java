package com.zero.support.compat.recycler;

import android.graphics.Color;

import com.zero.support.compat.vo.BaseObject;
import com.zero.support.compat.recycler.annotation.RecyclerViewBind;


@RecyclerViewBind(SquareViewBinder.class)
public class ColorCell extends BaseObject {
    private int color;

    public ColorCell(int color) {
        this.color = color;
    }

    public ColorCell(String color) {
        this.color = Color.parseColor(color);
    }

    public int getColor() {
        return color;
    }
}
