package com.zero.support.vo;

/**
 * Created by xianggaofeng on 2017/12/14.
 */

public class CheckBean<T> extends BaseObject {
    private T object;
    private String title;


    public CheckBean(T object, String title) {
        this.object = object;
        this.title = String.valueOf(title);
    }

    public CheckBean(T object) {
        this.object = object;
        this.title = String.valueOf(object);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T getItem() {
        return object;
    }
}
