package com.zero.support.compat.recycler.tools;

import android.view.View;

import com.zero.support.compat.recycler.Cell;
import com.zero.support.compat.recycler.ItemViewHolder;

public class SpaceCell implements Cell {
    @Override
    public void onItemClick(View view, ItemViewHolder holder) {

    }

    @Override
    public boolean onLongItemClick(View view, ItemViewHolder holder) {
        return false;
    }
}
