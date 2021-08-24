package com.zero.support.recycler.tools;


import com.zero.support.recycler.Cell;
import com.zero.support.vo.BaseObject;

import java.util.List;

public class GroupCell extends BaseObject {
    private final CellList items = new CellList();

    public GroupCell() {
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

    public void replaceCells(List<Cell> cells) {
        items.replaceContentCells(cells);
    }

}
