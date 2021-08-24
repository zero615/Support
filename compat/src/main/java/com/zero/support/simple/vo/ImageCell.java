package com.zero.support.simple.vo;

import com.zero.support.vo.BaseObject;

public class ImageCell extends BaseObject {
    private int imageResource;

    public ImageCell(int image) {
        this.imageResource = image;
    }

    public int getImageResource() {
        return imageResource;
    }
}
