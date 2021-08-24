package com.zero.support.recycler;


public class SimpleViewBound extends ItemViewBound {
    private int data = -1;
    private int viewHolder = -1;
    private int layoutId = -1;

    public SimpleViewBound(int layoutId) {
        super(layoutId);
    }

    public SimpleViewBound(int data, int layoutId) {
        super(layoutId);
        this.data = data;
    }

    public SimpleViewBound(int data, int viewHolder, int layoutId) {
        super(layoutId);
        this.data = data;
        this.viewHolder = viewHolder;
        this.layoutId = layoutId;
    }


    @Override
    public void bindItem(ItemViewHolder holder) {
        if (data != -1) {
            holder.getBinding().setVariable(data, holder.getItem());
        }
        if (viewHolder != -1) {
            holder.getBinding().setVariable(viewHolder, holder);
        }

    }
}
