package com.zero.easy.apps;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.zero.easy.databinding.ActivityPackagesBinding;
import com.zero.support.recycler.CellAdapter;
import com.zero.easy.CellActivity;


public class PackagesActivity extends CellActivity {

    ActivityPackagesBinding binding;
//
//    @Override
//    protected void onCreateContentView(LayoutInflater from, FrameLayout content) {
//        binding = DataBindingUtil.inflate(from, R.layout.activity_packages, content, false);
//        content.addView(binding.getRoot());
//        setUpRecyclerView(binding.recyclerView);
//        binding.slideBar.setOnTouchLetterChangeListener((isTouch, letter) -> getViewModel().indexOf(getAdapter().getItems().pickCells(), letter));
//    }

    @Override
    public PackagesViewModel getViewModel() {
        return (PackagesViewModel) super.getViewModel();
    }

    @Override
    protected void onBindRecyclerView(RecyclerView recyclerView) {
        super.onBindRecyclerView(recyclerView);
    }

    @Override
    protected PackagesViewModel onCreateCellViewModel() {
        return attachSupportViewModel(PackagesViewModel.class);
    }

    @Override
    protected void onBindCellAdapter(CellAdapter adapter) {
        super.onBindCellAdapter(adapter);
    }
}
