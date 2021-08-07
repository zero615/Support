package com.zero.support.glide;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class GlideModel {
    public int placeHolder;
    public int error;
    public DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.ALL;
    public Object model;
}
