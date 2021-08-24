package com.zero.easy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.zero.support.app.SupportActivity;


public class TitleActivity extends SupportActivity {
    private TextView textView;
    private FrameLayout container;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_title);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        textView = findViewById(R.id.title);
        container = findViewById(R.id.content);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void setTitle(int title) {
        textView.setText(title);
    }

    public void setTitle(String title) {
        textView.setText(title);
    }

    public <T extends ViewDataBinding> T setBindingContentView(int layoutResID) {
        return DataBindingUtil.inflate(LayoutInflater.from(this), layoutResID, container, true);
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, container, true);
    }

    @Override
    public void setContentView(View view) {
        container.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        container.addView(view, params);
    }
}
