package com.zero.support.simple;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zero.support.compat.R;
import com.zero.support.compat.databinding.ActivityCellBinding;
import com.zero.support.vo.Resource;
import com.zero.support.recycler.Cell;
import com.zero.support.recycler.CellAdapter;

import java.util.List;

public abstract class CellActivity extends TitleActivity {
    private CellAdapter adapter;
    private RecyclerView recyclerView;
    private CellViewModel<?> viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCellBinding binding = setBindingContentView(R.layout.activity_cell);
        recyclerView = binding.recyclerView;
        viewModel = onCreateCellViewModel();
        binding.setViewModel(viewModel);
        adapter = new CellAdapter(viewModel);
        onBindRecyclerView(binding.recyclerView);
        onBindCellAdapter(adapter);

        binding.recyclerView.setAdapter(adapter);
        viewModel.resource().asLive().observe(this, new Observer<Resource<List<Cell>>>() {
            @Override
            public void onChanged(Resource<List<Cell>> resource) {
                binding.setResource(resource);
                if (resource.isSuccess()) {
                    adapter.submitList(resource.data());
                }
            }
        });
    }

    public CellViewModel<?> getViewModel() {
        return viewModel;
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

    protected abstract CellViewModel<?> onCreateCellViewModel();
}
