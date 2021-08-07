package com.zero.support.simple;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zero.support.compat.R;
import com.zero.support.compat.databinding.ActivityCellBinding;
import com.zero.support.compat.recycler.Cell;
import com.zero.support.compat.recycler.CellAdapter;
import com.zero.support.compat.vo.Resource;
import com.zero.support.simple.widget.ResourceLayout;

import java.util.List;

public class CellActivity extends TitleActivity {
    private CellAdapter adapter;
    private RecyclerView recyclerView;
    private CellViewModel<?> viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCellBinding binding = setBindingContentView(R.layout.activity_cell);
        viewModel = onCreateViewModel();
        adapter = new CellAdapter(viewModel);
        onCreateContentView(LayoutInflater.from(this), (ResourceLayout) binding.getRoot());


        viewModel.resource().asLive().observe(this, new Observer<Resource<List<Cell>>>() {
            @Override
            public void onChanged(Resource<List<Cell>> listResource) {
                binding.setResource(listResource);
                if (listResource.isSuccess()) {
                    adapter.submitList(listResource.data);
                }
            }
        });

    }

    public CellViewModel<?> getViewModel() {
        return viewModel;
    }

    protected void onCreateContentView(LayoutInflater from, FrameLayout content) {
        RecyclerView recyclerView = new RecyclerView(this);
        content.addView(recyclerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setUpRecyclerView(recyclerView);
    }

    protected void setUpRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        onBindRecyclerView(recyclerView);
        onBindCellAdapter(adapter);
        recyclerView.setAdapter(adapter);
    }

    protected void onBindRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void onBindCellAdapter(CellAdapter adapter) {
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public CellAdapter getAdapter() {
        return adapter;
    }

    protected CellViewModel<?> onCreateViewModel() {
        return attachSupportViewModel(CellViewModel.class);
    }
}
