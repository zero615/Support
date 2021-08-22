package com.zero.support.recycler.tools;



import com.zero.support.compat.vo.BaseObject;
import com.zero.support.recycler.Cell;

import java.util.List;

public class ListCell extends BaseObject {
    private String name;
    private CellList items = new CellList();

    public String getName() {
        return name;
    }

    public void addItem(Cell cell) {
        items.addContentCell(cell);
    }

    public CellList getItems() {
        if (items.hasChanged()) {
            items.submit(true);
        }
        return items;
    }

    public void addCells(List<Cell> cells) {
        items.replaceContentCells(cells);
    }

}
