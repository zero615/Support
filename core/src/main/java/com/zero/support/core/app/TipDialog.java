package com.zero.support.core.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zero.support.core.R;


public class TipDialog extends Dialog {


    private TextView textView;
    private final SparseArray<View> views = new SparseArray<>();
    /**
     * 显示 Loading 图标
     */

    private View current;


    public TipDialog(@NonNull Context context) {
        this(context, 0);
    }

    public TipDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_common_tip);

        textView = findViewById(R.id.tip_message);
        views.put(Tip.TYPE_FAIL, findViewById(R.id.tip_error));
        views.put(Tip.TYPE_LOADING, findViewById(R.id.tip_loading));
        views.put(Tip.TYPE_INFO, findViewById(R.id.tip_info));
        setCancelable(false);

    }

    public void dispatchTipEvent(Tip tip) {
        View view = views.get(tip.type);
        if (view != current) {
            if (current != null) {
                current.setVisibility(View.GONE);
            }
            current = view;
        }
        if (tip.message != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(tip.message);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

}
