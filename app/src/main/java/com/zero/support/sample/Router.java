package com.zero.support.sample;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.zero.support.vo.BaseObject;
import com.zero.support.recycler.ItemViewHolder;

public class Router extends BaseObject {
    private String name;
    private Class<? extends Activity> aClass;

    public Router(String name, Class<? extends Activity> aClass) {
        this.name = name;
        this.aClass = aClass;
    }

    @Override
    public void onItemClick(View view, ItemViewHolder holder) {
        super.onItemClick(view, holder);
        view.getContext().startActivity(new Intent(view.getContext(), aClass));
    }

    public String getName() {
        return name;
    }
}
