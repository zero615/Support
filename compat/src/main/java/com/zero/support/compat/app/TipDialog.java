package com.zero.support.compat.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zero.support.compat.R;

public class TipDialog extends Dialog {
    private Runnable mDismissDaemon = new Runnable() {
        @Override
        public void run() {
            if (isShowing()) {
                dismiss();
            }
        }
    };

    private TextView textView;
    private SparseArray<View> views = new SparseArray<>();
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
        setContentView(R.layout.dialog_common_tip);
        textView = findViewById(R.id.tip_message);
        views.put(Tip.TYPE_FAIL, findViewById(R.id.tip_error));
        views.put(Tip.TYPE_LOADING, findViewById(R.id.tip_loading));
        views.put(Tip.TYPE_INFO, findViewById(R.id.tip_info));
        setCancelable(false);
    }

    public void dispatchTipEvent(Tip tip) {
        if (tip.type == Tip.TYPE_DISMISS) {
            dismiss();
            return;
        }
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
        if (tip.type == Tip.TYPE_SUCCESS) {
            showWithDaemon(700);
        } else {
            showWithDaemon(1500);
        }
    }


    public void showWithDaemon(long delayMillis) {
        if (!isShowing()) {
            show();
        }
        dismissWithDaemon(delayMillis);
    }

    public void dismissWithDaemon(long delayMillis) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.getDecorView().removeCallbacks(mDismissDaemon);
        window.getDecorView().postDelayed(mDismissDaemon, delayMillis);

    }

    @Override
    public void show() {
        super.show();
    }

}
