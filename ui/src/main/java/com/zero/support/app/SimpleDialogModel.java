package com.zero.support.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;

import com.zero.support.core.app.DialogModel;


public class SimpleDialogModel extends DialogModel {
    private final Builder builder;
    public static final int TYPE_HTML = 2;
    public static final int TYPE_HTML_URL = 3;
    public static final int TYPE_PLAIN_TEXT = 1;

    public static final int TYPE_RICH_TEXT = 0;

    @Override
    public boolean isEnableCached() {
        return false;
    }

    protected SimpleDialogModel(Builder builder) {
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

    public CharSequence getContent() {
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
        if (builder.textType != TYPE_RICH_TEXT) {
            return text;
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
    protected Dialog onCreateLayer(Activity activity) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        String value = getTitle();
        if (value != null) {
            alertBuilder.setTitle(value);
        }
        boolean handled = false;
        if (this.builder.contentViewId != 0) {
            alertBuilder.setView(this.builder.contentViewId);
            handled = true;
        } else if (this.builder.contentView != null) {
            alertBuilder.setView(this.builder.contentView);
            handled = true;
        } else if (this.builder.viewBinder != null) {
            View view = this.builder.viewBinder.onCreateView(activity, null);
            if (view != null) {
                this.builder.viewBinder.onBindView(view, this);
                handled = true;
                alertBuilder.setView(view);
            }
        }
        if (!handled) {
            if (this.builder.textType == TYPE_HTML) {
                WebView webView = this.builder.webView;
                if (webView == null) {
                    webView = new WebView(activity);
                    this.builder.webView = webView;
                }
                webView.loadData(String.valueOf(getContent()), "text/html", "UTF-8");
            } else if (this.builder.textType == TYPE_HTML_URL) {
                WebView webView = this.builder.webView;
                if (webView == null) {
                    webView = new WebView(activity);
                    this.builder.webView = webView;
                }
                webView.loadUrl(String.valueOf(getContent()));
            } else {
                alertBuilder.setMessage(getContent());
            }
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog alertDialog = (AlertDialog) dialog;
                dispatchClickEvent(alertDialog.getButton(which), which);
            }
        };
        alertBuilder.setNegativeButton(getNegative(), listener);
        alertBuilder.setPositiveButton(getPositive(), listener);
        alertBuilder.setCancelable(this.builder.cancelable);
        alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dispatchClickEvent(null, DialogInterface.BUTTON_NEUTRAL);
                dismiss();
            }
        });
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        AlertDialog dialog = alertBuilder.create();
        dialog.show();
        dialog.dismiss();
        dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchClickEvent(v, DialogInterface.BUTTON_POSITIVE);
                if (builder.clickDismiss) {
                    dismiss();
                }
            }
        });
        dialog.getButton(Dialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchClickEvent(v, DialogInterface.BUTTON_NEGATIVE);
                if (builder.clickDismiss) {
                    dismiss();
                }
            }
        });
        dialog.getButton(Dialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchClickEvent(v, DialogInterface.BUTTON_NEUTRAL);
                if (builder.clickDismiss) {
                    dismiss();
                }
            }
        });
        return dialog;
    }

    public Builder newBuilder() {
        return new Builder(builder);
    }


    public interface ClickInterceptor {
        String intercept(String url);
    }

    public interface ViewBinder {
        View onCreateView(Context context, ViewGroup parent);

        void onBindView(View view, SimpleDialogModel model);
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
        private int textType;
        private boolean cancelable = false;
        private WebView webView;
        private View contentView;
        private ViewBinder viewBinder;
        private boolean clickDismiss = true;

        public Builder(Builder builder) {
            this.negative = builder.negative;
            this.negativeId = builder.negativeId;
            this.positive = builder.positive;
            this.positiveId = builder.positiveId;
            this.content = builder.content;
            this.args = builder.args;
            this.contentId = builder.contentId;
            this.title = builder.title;
            this.titleId = builder.titleId;
            this.name = builder.name;
            this.interceptor = builder.interceptor;
            this.contentViewId = builder.contentViewId;
            this.textType = builder.textType;
            this.cancelable = builder.cancelable;
            this.webView = builder.webView;
            this.contentView = builder.contentView;
            this.viewBinder = builder.viewBinder;
            this.clickDismiss = builder.clickDismiss;
        }

        public Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder clickDismiss(boolean dismiss) {
            this.clickDismiss = dismiss;
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

        public Builder viewBinder(ViewBinder viewBinder) {
            this.viewBinder = viewBinder;
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

        public Builder webView(WebView webView) {
            this.webView = webView;
            return this;
        }

        public Builder contentView(View view) {
            this.contentView = view;
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

        public Builder textType(int type) {
            this.textType = type;
            return this;
        }


        public Builder cancelable(boolean cancel) {
            this.cancelable = cancel;
            return this;
        }
    }
}
