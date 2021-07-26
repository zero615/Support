package com.zero.support.compat.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.util.List;

import static com.zero.support.compat.recycler.BaseAdapter.DB_PAYLOAD;


/**
 * Created by prayxiang on 2017/10/17.
 */

public class ItemViewBound extends ItemViewBinder {
    private int mLayoutId;

    public ItemViewBound(@LayoutRes int layoutId) {
        mLayoutId = layoutId;
    }


    public static <T extends ViewDataBinding> ItemViewHolder create(ViewGroup parent, @LayoutRes int layoutId) {
        T binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewHolder vh = create(parent, mLayoutId);
        onDataBoundCreated(vh);
//        vh.getBinding().addOnRebindCallback(adapter.getOnRebindCallback());
        return vh;
    }

    @SuppressWarnings({"all"})
    public void onDataBoundCreated(ItemViewHolder viewHolder) {
    }

    public void bindItem(ItemViewHolder holder) {
    }

    @SuppressWarnings("unchecked")
    @Override
    final public void onBindViewHolder(ItemViewHolder holder, int position, List<Object> payloads) {
//        if (payloads.isEmpty() || hasNonDataBindingInvalidate(payloads)) {
//            bindItem(holder);
//        }
//        holder.getBinding().executePendingBindings();
        bindItem(holder);
        holder.getBinding().executePendingBindings();
    }

    private boolean hasNonDataBindingInvalidate(List<Object> payloads) {
        for (Object payload : payloads) {
            if (payload != DB_PAYLOAD) {
                return true;
            }
        }
        return false;
    }


}
