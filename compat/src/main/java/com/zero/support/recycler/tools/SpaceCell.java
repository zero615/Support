package com.zero.support.recycler.tools;

import android.view.View;

import com.zero.support.recycler.Cell;
import com.zero.support.recycler.ItemViewHolder;

public class SpaceCell implements Cell {
    @Override
    public void onItemClick(View view, ItemViewHolder holder) {

    }

    @Override
    public boolean onLongItemClick(View view, ItemViewHolder holder) {
        return false;
    }
}
