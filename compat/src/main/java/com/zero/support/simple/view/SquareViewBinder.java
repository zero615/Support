package com.zero.easy.view;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.zero.support.recycler.ColorCell;
import com.zero.support.recycler.ItemViewBinder;
import com.zero.support.recycler.ItemViewHolder;
import com.zero.support.compat.widget.AspectRatioLayout;
import com.zero.easy.vo.ImageCell;

import java.util.List;

public class SquareViewBinder extends ItemViewBinder {
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AspectRatioLayout layout = new AspectRatioLayout(parent.getContext());
        layout.setAspectRatio(1);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ItemViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        Object o = holder.getItem();
        if (o instanceof ColorCell) {
            holder.itemView.setBackgroundColor(((ColorCell) o).getColor());
        } else if (o instanceof ImageCell) {
            holder.itemView.setBackgroundResource(((ImageCell) o).getImageResource());
        }
    }
}
