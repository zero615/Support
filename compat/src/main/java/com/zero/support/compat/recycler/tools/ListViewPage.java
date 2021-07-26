//package com.zero.support.compat.recycler.tools;
//
//import com.zero.support.compat.vo.Resource;
//import com.zero.support.compat.recycler.Cell;
//
//import java.util.List;
//
//public class ListViewPage extends ViewPage<List<Cell>> {
//    private CellList mItems = new CellList();
//
//    public CellList getItems() {
//        return mItems;
//    }
//
//    @Override
//    protected void onSubmit(Resource<List<Cell>> resource) {
//        if (resource.status == Resource.SUCCESS) {
//            onSubmitList(resource.data);
//            mItems.replaceContentCells(resource.data);
//            mItems.submit(false);
//        } else if (resource.status == Resource.LOADING) {
//            if (resource.data != null && resource.data.size() > 0) {
//                onSubmitList(resource.data);
//                mItems.replaceContentCells(resource.data);
//                mItems.submit(false);
//            }
//        }
//    }
//
//    private void onSubmitList(List<Cell> data) {
//
//    }
//}
