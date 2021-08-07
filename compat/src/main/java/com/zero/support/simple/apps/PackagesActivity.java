package com.zero.support.simple.apps;

import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.zero.support.compat.R;
import com.zero.support.compat.databinding.ActivityPackagesBinding;
import com.zero.support.compat.recycler.CellAdapter;
import com.zero.support.simple.CellActivity;


public class PackagesActivity extends CellActivity {

    ActivityPackagesBinding binding;

    @Override
    protected void onCreateContentView(LayoutInflater from, FrameLayout content) {
        binding = DataBindingUtil.inflate(from, R.layout.activity_packages, content, false);
        content.addView(binding.getRoot());
        setUpRecyclerView(binding.recyclerView);
        binding.slideBar.setOnTouchLetterChangeListener((isTouch, letter) -> getViewModel().indexOf(getAdapter().getItems().pickCells(), letter));
    }

    @Override
    public PackagesViewModel getViewModel() {
        return (PackagesViewModel) super.getViewModel();
    }

    @Override
    protected void onBindRecyclerView(RecyclerView recyclerView) {
        super.onBindRecyclerView(recyclerView);
    }

    @Override
    protected PackagesViewModel onCreateViewModel() {
        return attachSupportViewModel(PackagesViewModel.class);
    }

    @Override
    protected void onBindCellAdapter(CellAdapter adapter) {
        super.onBindCellAdapter(adapter);
    }
}
