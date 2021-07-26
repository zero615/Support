package com.zero.support.compat.recycler.tools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zero.support.compat.app.SupportFragment;
import com.zero.support.compat.vo.BaseObject;
import com.zero.support.compat.vo.Resource;
import com.zero.support.compat.recycler.Cell;
import com.zero.support.compat.recycler.CellAdapter;
import com.zero.support.compat.recycler.ItemViewHolder;
import com.zero.support.compat.recycler.OnItemClickListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@SuppressWarnings("ALL")
public abstract class ListFragment<T extends ListViewModel> extends SupportFragment implements OnItemClickListener {
    protected RecyclerView recyclerView;
    protected T listViewModel;
    private CellAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = generateDefaultRecyclerView();
        setupWithRecyclerView(recyclerView);
        return recyclerView;
    }

    public CellAdapter getCellAdapter() {
        if (mAdapter == null) {
            mAdapter = onCreateCellAdapter();
        }
        return mAdapter;
    }

    protected CellAdapter onCreateCellAdapter() {
        return new CellAdapter(listViewModel);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public RecyclerView setupWithRecyclerView(final RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        listViewModel = onCreateListViewModel();
        onInitializeListViewModel(listViewModel);
        listViewModel.resource().asLive().observe(this, new Observer<Resource<List<Cell>>>() {
            @Override
            public void onChanged(Resource<List<Cell>> resource) {
                if (resource.isSuccess()) {
                    getCellAdapter().submitList(resource.data);
                }
            }
        });
        getCellAdapter().setOnItemClickListener(this);
        recyclerView.setAdapter(getCellAdapter());
        return recyclerView;
    }

    public RecyclerView generateDefaultRecyclerView() {
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        return recyclerView;
    }

    public void onInitializeListViewModel(T viewModel) {
    }

    @Override
    public void onItemClick(View view, ItemViewHolder holder) {
        BaseObject object = holder.getItem();
        object.onItemClick(holder.itemView, holder);
    }

    public T onCreateListViewModel() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("missing type parameter.");
        }
        ParameterizedType parameter = (ParameterizedType) superclass;
        return attachSupportViewModel((Class<T>) parameter.getActualTypeArguments()[0]);
    }
}
