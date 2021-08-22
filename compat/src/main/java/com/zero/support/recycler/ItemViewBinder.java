package com.zero.support.recycler;


import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Created by prayxiang on 2017/10/17.
 */
@SuppressWarnings("all")
public class ItemViewBinder {

    protected BaseAdapter adapter;

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    public void onBindViewHolder(ItemViewHolder holder, int position, List<Object> payloads) {

    }

    public void onViewDetachedFromWindow(ItemViewHolder viewHolder) {

    }

    public void onViewAttachedToWindow(ItemViewHolder viewHolder) {
    }

    public void onViewRecycled(ItemViewHolder holder) {

    }
}
