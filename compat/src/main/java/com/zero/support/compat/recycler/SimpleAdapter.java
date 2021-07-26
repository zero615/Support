package com.zero.support.compat.recycler;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SimpleAdapter<T> extends BaseAdapter {
    private List<T> items;

    public SimpleAdapter(List<T> list) {
        super(new ClassTypeProvider());
        items = list;
    }

    public SimpleAdapter add(Class<?> cls, ItemViewBinder viewBinder) {
        ClassTypeProvider provider = (ClassTypeProvider) getTypeProvider();
        provider.addViewBinder(cls, viewBinder);
        return this;
    }

    public void attach(RecyclerView recyclerView) {
        recyclerView.setAdapter(this);
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }
}
