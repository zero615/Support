package com.zero.support.simple.vo;

import android.view.View;

import com.zero.support.app.SupportViewModel;
import com.zero.support.compat.R;

import com.zero.support.recycler.CellAdapter;
import com.zero.support.recycler.ItemViewHolder;
import com.zero.support.recycler.SimpleViewBound;
import com.zero.support.recycler.annotation.RecyclerViewBind;
import com.zero.support.compat.vo.BaseObject;

@RecyclerViewBind(ImportCell.ImportViewBinder.class)
public class ImportCell extends BaseObject {
    public interface OnRequestImportListener {
        void onRequestImport(View view, ItemViewHolder holder);
    }

    public static String type;

    @Override
    public void onItemClick(View view, ItemViewHolder holder) {
        super.onItemClick(view, holder);
        CellAdapter adapter = holder.getAdapter();
        SupportViewModel viewModel = adapter.getViewModel();
        if (viewModel instanceof OnRequestImportListener) {
            ((OnRequestImportListener) viewModel).onRequestImport(view, holder);
        }
    }

    public static class ImportViewBinder extends SimpleViewBound {

        public ImportViewBinder() {
            super(R.layout.view_bound_import);
        }

    }
}
