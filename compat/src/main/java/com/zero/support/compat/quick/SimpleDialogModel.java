package com.zero.support.compat.quick;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.zero.support.compat.app.DialogModel;
import com.zero.support.compat.app.SupportActivity;


public class SimpleDialogModel extends DialogModel {
    public static final String GROUP_DIALOG = "dialogs";

    private final Builder builder;


    @Override
    public boolean isEnableCached() {
        return false;
    }

    protected SimpleDialogModel(Builder builder) {
        super(builder.name);
        this.builder = builder;
    }


    @Override
    public void onClick(View view, int which) {
        super.onClick(view, which);

    }

    public String getNegative() {
        if (builder.negativeId != 0) {
            return requireViewModel().requireActivity().getString(builder.negativeId);
        }
        return builder.negative;
    }

    public String getPositive() {
        if (builder.positiveId != 0) {
            return requireViewModel().requireActivity().getString(builder.positiveId);
        }
        return builder.positive;
    }

    public String getTitle() {
        if (builder.titleId != 0) {
            return requireViewModel().requireActivity().getString(builder.titleId);
        }
        return builder.title;
    }

    public Spanned getContent() {
        String content = builder.content;
        if (builder.contentId != 0) {
            content = requireViewModel().requireActivity().getString(builder.contentId);
        }
        String text;
        if (builder.args != null) {
            text = String.format(content, builder.args);
        } else {
            text = content;
        }
        try {
            SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(text);
            URLSpan[] urlSpans = spanned.getSpans(0, spanned.length(), URLSpan.class);
            for (final URLSpan span : urlSpans) {
                int start = spanned.getSpanStart(span);
                int end = spanned.getSpanEnd(span);
                int flag = spanned.getSpanFlags(span);
                ClickableSpan clickableSpan = new URLSpan(span.getURL()) {
                    @Override
                    public void onClick(View widget) {
                        String url = span.getURL();
                        if (builder.interceptor != null) {
                            url = builder.interceptor.intercept(url);
                        }
                        SimpleDialogModel.this.onClickSpan(widget, url);
                    }
                };
                spanned.removeSpan(span);
                spanned.setSpan(clickableSpan, start, end, flag);

            }
            return spanned;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SpannableString(text);
    }

    public void onClickSpan(View widget, String url) {

    }

    @Override
    protected Dialog onCreateDialog(SupportActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String value = getTitle();
        if (value != null) {
            builder.setTitle(value);
        }

        if (this.builder.contentViewId != 0) {
            builder.setView(this.builder.contentViewId);
        } else {
            Spanned spanned = getContent();
            if (spanned != null) {
                builder.setMessage(spanned);
            }
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("xgf", "onClick: "+which );
                AlertDialog alertDialog = (AlertDialog) dialog;
                dispatchClickEvent(alertDialog.getButton(which), which);
            }
        };
        builder.setNegativeButton(getNegative(), listener);
        builder.setPositiveButton(getPositive(), listener);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.e("xgf", "onClick: cancel " );
                dispatchClickEvent(null, DialogInterface.BUTTON_NEUTRAL);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.e("xgf", "onClick: dismiss " );
                if (which() == DialogInterface.BUTTON_NEUTRAL) {
                    dismiss();
                }
            }
        });
        return builder.create();
    }

    public static class Builder {
        private String negative;
        private int negativeId;
        private String positive;
        private int positiveId;
        private String content;
        private Object[] args;
        private int contentId;
        private String title;
        private int titleId;
        private String name;
        private ClickInterceptor interceptor;
        private int contentViewId;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder clickInterceptor(ClickInterceptor interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        public Builder negative(String negative) {
            this.negative = negative;
            return this;
        }

        public Builder negative(int negative) {
            this.negativeId = negative;
            return this;
        }

        public Builder positive(String positive) {
            this.positive = positive;
            return this;
        }

        public Builder positive(int positive) {
            this.positiveId = positive;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder content(int content) {
            this.contentId = content;
            return this;
        }

        public Builder content(String content, Object[] args) {
            this.content = content;
            this.args = args;
            return this;
        }

        public Builder content(int content, Object[] args) {
            this.contentId = content;
            this.args = args;
            return this;
        }

        public Builder contentView(int content) {
            this.contentViewId = content;
            return this;
        }


        public Builder title(String title) {
            this.title = title;
            return this;
        }


        public Builder title(int title) {
            this.titleId = title;
            return this;
        }

        public SimpleDialogModel build() {
            return new SimpleDialogModel(this);
        }

        public <T extends SimpleDialogModel> T build(Class<T> cls) {
            try {
                return cls.getConstructor(Builder.class).newInstance(this);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
