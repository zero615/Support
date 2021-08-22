package com.zero.support.recycler;

import android.util.Log;
import android.util.SparseArray;


import com.zero.support.recycler.annotation.RecyclerViewBind;

import java.util.ArrayList;
import java.util.List;

public class ClassTypeProvider {
    private List<Class<?>> types = new ArrayList<>();
    private SparseArray<ItemViewBinder> mCaches = new SparseArray<>();

    public int getItemViewType(int position, Object item) {
        int type = types.indexOf(item.getClass());
        if (type == -1) {
            RecyclerViewBind viewBind = item.getClass().getAnnotation(RecyclerViewBind.class);
            if (viewBind != null) {
                try {
                    if (viewBind.value() != ItemViewBinder.class) {
                        addViewBinder(item.getClass(), viewBind.value().newInstance());
                    } else if (viewBind.layout() != -1) {
                        addViewBinder(item.getClass(), new SimpleViewBound(viewBind.br(), viewBind.layout()));
                    }
                    return getItemViewType(position, item);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            Log.d("type", "not fount class = " + item.getClass());
        }
        return type;
    }

    ItemViewBinder getViewBinder(int type) {
        return mCaches.get(type);
    }

    public void copyFrom(ClassTypeProvider provider) {
        for (int i = 0; i < provider.mCaches.size(); i++) {
            addViewBinder(provider.types.get(mCaches.keyAt(i)), provider.mCaches.valueAt(i));
        }
    }

    public void addViewBinder(Class<?> cls, ItemViewBinder viewBinder) {
        int index = types.indexOf(cls);
        if (index == -1) {
            types.add(cls);
            index = types.size() - 1;
        }
        mCaches.put(index, viewBinder);
    }


}
