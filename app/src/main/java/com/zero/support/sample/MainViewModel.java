package com.zero.support.sample;


import com.zero.support.app.SupportActivity;
import com.zero.support.recycler.Cell;
import com.zero.support.simple.CellViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends CellViewModel<String> {
    @Override
    protected void onViewModelCreated(SupportActivity activity) {
        super.onViewModelCreated(activity);
    }

    @Override
    protected List<Cell> performExecute(String s) throws Throwable {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Router("dialog", DialogActivity.class));
        cells.add(new Router("dialog", DialogActivity.class));
        cells.add(new Router("dialog", DialogActivity.class));
        cells.add(new Router("dialog", DialogActivity.class));
        cells.add(new Router("dialog", DialogActivity.class));

        return cells;
    }
}
