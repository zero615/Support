package com.zero.support.sample;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zero.easy.CellActivity;
import com.zero.easy.CellViewModel;
import com.zero.support.recycler.CellAdapter;
import com.zero.support.recycler.SimpleViewBound;



public class MainActivity extends CellActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAdapter().setEnableClick(true);
        getToolbar().setNavigationIcon(null);
        if (!getViewModel().isRequested()){
            getViewModel().notifyDataSetChanged(null);
        }
    }

    @Override
    protected void onBindRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
    }

    @Override
    protected void onBindCellAdapter(CellAdapter adapter) {
        super.onBindCellAdapter(adapter);
        adapter.add(Router.class,new SimpleViewBound(BR.data,R.layout.item_router));
    }

    @Override
    protected CellViewModel<?> onCreateCellViewModel() {
        return attachSupportViewModel(MainViewModel.class);
    }
}