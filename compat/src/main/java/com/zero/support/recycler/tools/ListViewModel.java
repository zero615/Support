package com.zero.support.recycler.tools;



import com.zero.support.app.ResourceViewModel;
import com.zero.support.recycler.Cell;
import com.zero.support.recycler.CellAdapter;

import java.util.List;

public abstract class ListViewModel<T> extends ResourceViewModel<T,List<Cell>> {
    private CellAdapter cellAdapter;

    public ListViewModel() {

    }
//
//    public void setupRecyclerView(CellAdapter adapter) {
//        adapter.setItems(getViewPage().getItems());
//    }
//
//    @Override
//    public Resource<List<Cell>> onCreateResource() {
//        return super.onCreateResource();
//    }

//    @Override
//    protected void onSubmit(Resource<List<Cell>> resource) {
//        super.onSubmit(resource);
//        getViewPage().postResource(resource);
//    }

//    public abstract ListViewPage getViewPage();
}
