package com.zero.support.simple;


import com.zero.support.app.ResourceViewModel;
import com.zero.support.recycler.Cell;
import com.zero.support.vo.Resource;

import java.util.List;

public abstract class CellViewModel<T> extends ResourceViewModel<T, List<Cell>> {

    @Override
    protected void onResourceChanged(Resource<List<Cell>> resource) {
        super.onResourceChanged(resource);
    }


}
