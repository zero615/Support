package com.zero.easy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zero.easy.databinding.ActivityCellBinding;
import com.zero.easy.widget.ResourceLayout;

import com.zero.support.app.SupportFragment;


import com.zero.support.recycler.Cell;
import com.zero.support.recycler.CellAdapter;
import com.zero.support.vo.Resource;

import java.util.List;

public class CellFragment extends SupportFragment {
    private CellAdapter adapter;
    private RecyclerView recyclerView;
    private CellViewModel<?> viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ActivityCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_cell, container, false);
        viewModel = onCreateViewModel();
        adapter = new CellAdapter(viewModel);
        onCreateContentView(LayoutInflater.from(requireActivity()), (ResourceLayout) binding.getRoot());


        viewModel.resource().asLive().observe(getViewLifecycleOwner(), new Observer<Resource<List<Cell>>>() {
            @Override
            public void onChanged(Resource<List<Cell>> listResource) {
                binding.setResource(listResource);
                if (listResource.isSuccess()) {
                    adapter.submitList(listResource.data());
                }
            }
        });
        return binding.getRoot();
    }

    public CellViewModel<?> getViewModel() {
        return viewModel;
    }

    protected void onCreateContentView(LayoutInflater from, FrameLayout content) {
        RecyclerView recyclerView = new RecyclerView(requireActivity());
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
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
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
