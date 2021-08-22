package com.zero.support.recycler;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.zero.support.app.SupportViewModel;
import com.zero.support.recycler.tools.CellList;

import java.util.Collections;
import java.util.List;

public class CellAdapter extends BaseAdapter implements CellList.ListCallback {
    private CellList items;
    private SupportViewModel viewModel;

    public CellAdapter(SupportViewModel viewModel) {
        this(new ClassTypeProvider());
        this.viewModel = viewModel;
    }

    public CellAdapter(CellList items) {
        this.items = items;
        this.items.addListCallback(this);
    }

    public CellAdapter() {
        this(new ClassTypeProvider());
    }

    public CellAdapter(ClassTypeProvider provider) {
        super(provider);
        items = new CellList();
        items.addListCallback(this);
    }

    @SuppressWarnings("ALL")
    public <T extends SupportViewModel> T getViewModel() {
        return (T) viewModel;
    }

    public CellList getItems() {
        return items;
    }

    public void setItems(CellList items) {
        if (this.items != null) {
            items.removeListCallback(this);
        }
        this.items = items;
        this.items.addListCallback(this);
    }

    public CellAdapter add(Class<?> cls, ItemViewBinder viewBinder) {
        ClassTypeProvider provider = (ClassTypeProvider) getTypeProvider();
        provider.addViewBinder(cls, viewBinder);
        return this;
    }

    public void attach(RecyclerView recyclerView) {
        recyclerView.setAdapter(this);
    }

    public CellAdapter addFixFirstCells(Cell cell) {
        items.addFixFirstCell(cell);
        return this;
    }

    public CellAdapter addFixLastCells(Cell cell) {
        items.addFixLastCell(cell);
        return this;
    }

    public void submitList(List<? extends Cell> content) {
        if (content == null) {
            content = Collections.emptyList();
        }
        items.replaceContentCells(content);
        items.submit(false);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Cell getItem(int position) {
        return items.getItem(position);
    }

    @Override
    public void onDataSetChanged() {
        notifyDataSetChanged();
    }

    @Override
    public void onDataSetChanged(DiffUtil.DiffResult result) {
        result.dispatchUpdatesTo(this);
    }
}
