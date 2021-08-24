package com.zero.support.vo;

/**
 * Created by xianggaofeng on 2018/1/30.
 */

public class ImageInfo extends BaseObject {
    public int width = -2;
    public int height = -2;

    public long size;
    public String thumbUrl;
    public String imageUrl;


    public ImageInfo(int width, int height, long size, String thumbUrl, String imageUrl) {
        this.width = width;
        this.height = height;
        this.size = size;
        this.thumbUrl = thumbUrl;
        this.imageUrl = imageUrl;
    }

    public ImageInfo(String imageUrl) {
        this.imageUrl = imageUrl;
        thumbUrl = imageUrl;
    }
}
