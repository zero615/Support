package com.zero.support.recycler;

import android.view.View;

public interface Cell {
    void onItemClick(View view, ItemViewHolder holder);

    boolean onLongItemClick(View view, ItemViewHolder holder);
}
