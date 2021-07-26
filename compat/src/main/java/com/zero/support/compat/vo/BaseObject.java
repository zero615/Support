package com.zero.support.compat.vo;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableBoolean;

import com.zero.support.compat.recycler.Cell;
import com.zero.support.compat.recycler.ItemViewHolder;


/**
 * Created by xianggaofeng on 2018/1/30.
 */
public class BaseObject extends BaseObservable implements View.OnClickListener, View.OnLongClickListener, Cell {

    private final transient ObservableBoolean selected = new ObservableBoolean();
    private final transient ObservableBoolean checked = new ObservableBoolean();
    private final transient ObservableBoolean focused = new ObservableBoolean();
    private final transient ObservableBoolean enabled = new ObservableBoolean(true);
    private final transient ObservableBoolean changed = new ObservableBoolean();
    private final transient ObservableBoolean refreshing = new ObservableBoolean();

    public boolean isSelected() {
        return selected.get();
    }

    public ObservableBoolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public ObservableBoolean getChecked() {
        return checked;
    }

    public boolean isChecked() {
        return checked.get();
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    public ObservableBoolean getFocused() {
        return focused;
    }

    public boolean isFocused() {
        return focused.get();
    }

    public void setFocused(boolean focused) {
        this.focused.set(focused);
    }

    public ObservableBoolean getEnabled() {
        return enabled;
    }

    public boolean isEnabled() {
        return enabled.get();
    }


    public void setEnabled(boolean enabled) {
        this.enabled.set(true);
    }

    public ObservableBoolean getChanged() {
        return changed;
    }


    public boolean isChanged() {
        return changed.get();
    }


    public void setChanged(boolean changed) {
        this.changed.set(changed);
    }

    public ObservableBoolean getRefreshing() {
        return refreshing;
    }


    public boolean isRefreshing() {
        return refreshing.get();
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing.set(refreshing);
    }


    @Override
    public void onClick(View v) {
    }


    @Override
    public void onItemClick(View view, ItemViewHolder holder) {
        onClick(view);
    }

    @Override
    public boolean onLongItemClick(View view, ItemViewHolder holder) {
        return onLongClick(view);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
