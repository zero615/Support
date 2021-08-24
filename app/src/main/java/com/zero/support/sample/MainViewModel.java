package com.zero.support.sample;


import android.view.View;

import com.zero.support.core.AppGlobal;
import com.zero.support.core.app.ActivityInjector;
import com.zero.support.app.SupportActivity;
import com.zero.support.recycler.Cell;
import com.zero.support.recycler.ItemViewHolder;
import com.zero.support.simple.CellViewModel;
import com.zero.support.simple.vo.LetterCell;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends CellViewModel<String> {
    private List<Cell> cells = new ArrayList<>();
    @Override
    protected void onViewModelCreated(SupportActivity activity) {
        super.onViewModelCreated(activity);
    }

    @Override
    protected List<Cell> performExecute(String s) throws Throwable {
        cells.add(new LetterCell("head"));

        cells.add(new Router("dialog", DialogActivity.class){
            @Override
            public void onItemClick(View view, ItemViewHolder holder) {
//                super.onItemClick(view, holder);
                AppGlobal.sendMessage("tesst");
            }
        });
        if (isInitialize()){
            cells.add(new Router("dialog", DialogActivity.class));
            cells.add(new Router("dialog", DialogActivity.class));
            cells.add(new LetterCell("test"));
            cells.add(new Router("dialog", DialogActivity.class));
            cells.add(new Router("dialog", DialogActivity.class));
            cells.add(new LetterCell("test2"));
            Thread.sleep(5000);
        }else {
            cells.add(new Router("dialog", DialogActivity.class));
            cells.add(new LetterCell("test2"));
            Thread.sleep(500);
        }


        return cells;
    }
}
