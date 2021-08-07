package com.zero.support.simple.vo;

import com.zero.support.compat.vo.BaseObject;

public class ColorCell extends BaseObject {
    int color;

    public ColorCell(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
