package com.zero.easy.view;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zero.easy.widget.AspectRatioLayout;
import com.zero.support.recycler.ItemViewBinder;
import com.zero.support.recycler.ItemViewHolder;


import java.util.List;

public class SquareViewBinder extends ItemViewBinder {
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AspectRatioLayout layout = new AspectRatioLayout(parent.getContext());
        layout.setAspectRatio(1);
        layout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ItemViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position, List<Object> payloads) {
        ColorCell colorCell = holder.getItem();
        holder.itemView.setBackgroundColor(colorCell.getColor());
    }
}
