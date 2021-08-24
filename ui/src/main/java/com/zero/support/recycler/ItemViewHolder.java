package com.zero.support.recycler;

import android.content.Context;
import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by prayxiang on 2017/10/17.
 */
@SuppressWarnings("all")
public class ItemViewHolder extends RecyclerView.ViewHolder {
    private Object object;
    private Object item;

    private ViewDataBinding binding;


    @SuppressWarnings("all")
    public ItemViewHolder(View itemView) {
        super(itemView);
    }

    public ItemViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public <T extends ViewDataBinding> T getBinding() {
        return (T) binding;
    }

    public <T extends BaseAdapter> T getAdapter() {
        RecyclerView recyclerView = (RecyclerView) itemView.getParent();
        if (recyclerView != null) {
            return (T) recyclerView.getAdapter();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getItem() {
        return (T) item;
    }

    public void setItem(Object object) {
        this.item = object;
    }

    public <T> T getItem(Class<?> cls) {
        if (cls.isInstance(item)) {
            return (T) item;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject() {
        return (T) object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public int dp2px(int dp) {
        return (int) (getContext().getResources().getDisplayMetrics().density * dp + 0.5);

    }
}
