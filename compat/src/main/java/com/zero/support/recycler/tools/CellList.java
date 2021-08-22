package com.zero.support.recycler.tools;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.recyclerview.widget.DiffUtil;


import com.zero.support.recycler.Cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CellList {
    List<Cell> mCells = Collections.emptyList();
    List<Cell> mContentCells = new ArrayList<>();
    List<Cell> mFixFirstCells = new ArrayList<>();
    List<Cell> mFixLastCells = new ArrayList<>();
    private int startVersion = 0;
    private int dataVersion = 0;
    private Cell mEmptyCell;
    private int maxCount;
    private Cell maxLastCell;
    private int minCount;
    private Cell minLastCell;

    private List<ListCallback> mCallbacks = new CopyOnWriteArrayList<>();

    public CellList() {

    }

    public boolean hasChanged() {
        return dataVersion != startVersion;
    }

    public int indexOf(Object object) {
        return mCells.indexOf(object);
    }

    public void addListCallback(ListCallback callback) {
        mCallbacks.add(callback);
        callback.onDataSetChanged();
    }

    public void removeListCallback(ListCallback callback) {
        mCallbacks.remove(callback);
    }


    private void dispatchDataSetChanged() {
        for (ListCallback callback : mCallbacks) {
            callback.onDataSetChanged();
        }
    }

    private void dispatchDateRangeChanged(DiffUtil.DiffResult result) {
        for (ListCallback callback : mCallbacks) {
            callback.onDataSetChanged(result);
        }
    }

    public CellList addFixFirstCell(Cell cell) {
        dataVersion++;
        int index = mFixFirstCells.indexOf(cell);
        if (index == -1) {
            mFixFirstCells.add(0, cell);
        }
        return this;
    }

    public CellList appendFixFirstCell(Cell cell) {
        dataVersion++;
        int index = mFixFirstCells.indexOf(cell);
        if (index == -1) {
            mFixFirstCells.add(cell);
        }
        return this;
    }

    public CellList removeFixFirstCell(Cell cell) {
        if (cell == null) {
            return this;
        }
        dataVersion++;
        mFixFirstCells.remove(cell);
        return this;
    }


    public CellList setEmptyCell(Cell cell) {
        mEmptyCell = cell;
        return this;
    }


    public CellList addFixLastCell(Cell cell) {
        dataVersion++;
        int index = mFixFirstCells.indexOf(cell);
        if (index == -1) {
            mFixLastCells.add(cell);
        }
        return this;
    }


    public CellList removeFixLastCell(Cell cell) {
        dataVersion++;
        mFixLastCells.remove(cell);
        return this;
    }

    public CellList addLastMaxCell(int max, Cell cell) {
        dataVersion++;
        maxLastCell = cell;
        maxCount = max;
        return this;
    }

    public CellList addLastMinCell(int min, Cell cell) {
        dataVersion++;
        minLastCell = cell;
        minCount = min;
        return this;
    }

    private void submit(List<Cell> list, DiffUtil.DiffResult result) {
        if (dataVersion != startVersion) {
            mCells = list;
            startVersion = dataVersion;
            dispatchDateRangeChanged(result);
        }

    }

    public List<Cell> pickCells() {
        return mCells;
    }

    public void replaceContentCells(List<? extends Cell> contentCells) {
        mContentCells.clear();
        mContentCells.addAll(contentCells);
        dataVersion++;
    }

    public void submit(boolean sync) {
        if (dataVersion != startVersion) {
            if (sync) {
                mCells = values();
                startVersion = dataVersion;
                dispatchDataSetChanged();
            } else {
                new DiffTask(new ArrayList<>(mCells), values(), startVersion).execute();
            }
        }
    }

    public void flushOnly() {
        if (dataVersion != startVersion) {
            mCells = values();
            startVersion = dataVersion;
        }
    }

    public List<Cell> contents() {
        return new ArrayList<>(mContentCells);
    }

    public int fixFirstSize() {
        return mFixFirstCells.size();
    }

    public int fixLastSize() {
        return mFixLastCells.size();
    }

    public List<Cell> values() {
        if (dataVersion == startVersion) {
            return new ArrayList<>(mCells);
        }
        List<Cell> list = new ArrayList<>(mFixFirstCells.size() + mContentCells.size() + mContentCells.size());
        list.addAll(mFixFirstCells);
        list.addAll(mContentCells);
        list.addAll(mFixLastCells);
        if (list.size() >= maxCount && maxLastCell != null) {
            list.add(maxLastCell);
        }
        if (list.size() <= minCount && minLastCell != null) {
            list.add(minLastCell);
        }
        if (list.size() == 0 && mEmptyCell != null) {
            list.add(mEmptyCell);
        }
        return list;
    }

    public int size() {
        return mCells.size();
    }

    public Cell getItem(int position) {
        int size = mCells.size();
        if (size == 0 && mEmptyCell != null) {
            return mEmptyCell;
        }
        return mCells.get(position);
    }

    public void addContentCell(Cell cell) {
        mContentCells.add(cell);
        dataVersion++;
    }

    public void addContentCells(List<Cell> contentCells) {
        mContentCells.addAll(contentCells);
        dataVersion++;
    }

    public interface ListCallback {
        void onDataSetChanged();

        void onDataSetChanged(DiffUtil.DiffResult result);
    }

    @SuppressLint("StaticFieldLeak")
    private class DiffTask extends AsyncTask<Void, Void, DiffUtil.DiffResult> {
        List<Cell> oldCells;
        int version;
        List<Cell> newCells;

        public DiffTask(List<Cell> oldCells, List<Cell> newCells, int version) {
            this.oldCells = oldCells;
            this.newCells = newCells;
            this.version = version;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected DiffUtil.DiffResult doInBackground(Void... voids) {
            return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return oldCells.size();
                }

                @Override
                public int getNewListSize() {
                    return newCells.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Cell oldCell = oldCells.get(oldItemPosition);
                    Cell newCell = newCells.get(newItemPosition);
                    return oldCell.getClass() == newCell.getClass();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Cell oldCell = oldCells.get(oldItemPosition);
                    Cell newCell = newCells.get(newItemPosition);
                    return oldCell.equals(newCell);
                }
            }, true);
        }

        @Override
        protected void onPostExecute(DiffUtil.DiffResult diffResult) {
            if (version == startVersion) {
                submit(newCells, diffResult);
            }

        }
    }
}
