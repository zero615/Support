//package com.zero.support.compat.widget;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.databinding.Observable;
//
//import com.zero.common.R;
//import com.zero.support.common.vo.ViewPage;
//
//public class ViewPageLayout extends AspectRatioLayout {
//    private ViewPage<?> viewPage;
//    private View contentView;
//    private View loadingView;
//    private View errorView;
//    private View emptyView;
//    private boolean viewContent;
//    private boolean enableViewContent;
//    private final Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
//        @Override
//        public void onPropertyChanged(Observable sender, int propertyId) {
//            setViewPageInternal((ViewPage<?>) sender);
//        }
//    };
//
//    public ViewPageLayout(@NonNull Context context) {
//        this(context, null);
//    }
//
//
//    public ViewPageLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public ViewPageLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context, attrs);
//    }
//
//    public void setEnableViewContent(boolean enableViewContent) {
//        this.enableViewContent = enableViewContent;
//    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//
//    }
//
//    public void init(Context context, AttributeSet attrs) {
//        if (isInEditMode()) {
//            return;
//        }
//        resolveXmlAttributes(context, attrs);
//        contentView = findViewById(R.id.view_page_content);
//        errorView = findViewById(R.id.view_page_error);
//        emptyView = findViewById(R.id.view_page_empty);
//        loadingView = findViewById(R.id.view_page_loading);
//    }
//
//    private void resolveXmlAttributes(Context c, AttributeSet attrs) {
//        if (attrs == null) {
//            return;
//        }
//
//    }
//
//    public void setViewPage(ViewPage<?> viewPage) {
//        if (this.viewPage != viewPage) {
//            if (this.viewPage != null) {
//                this.viewPage.removeOnPropertyChangedCallback(callback);
//            }
//        } else {
//            return;
//        }
//        if (viewPage == null) {
//            return;
//        }
//
//        this.viewPage = viewPage;
//
//        setViewPageInternal(viewPage);
//        viewPage.addOnPropertyChangedCallback(callback);
//    }
//
//    private void setViewPageInternal(ViewPage<?> viewPage) {
//        closeStatus();
//        if (viewPage.isRefreshing()) {
//            if (viewContent) {
//                if (contentView != null) {
//                    contentView.setVisibility(VISIBLE);
//                }
//            }
//            if (loadingView != null) {
//                loadingView.setVisibility(VISIBLE);
//            }
//        } else if (viewPage.isEmpty()) {
//            if (emptyView != null) {
//                emptyView.setVisibility(VISIBLE);
//            }
//            viewContent = false;
//        } else if (viewPage.isError()) {
//            if (errorView != null) {
//                errorView.setVisibility(VISIBLE);
//            }
//            viewContent = false;
//        } else {
//            if (contentView != null) {
//                contentView.setVisibility(VISIBLE);
//                if (enableViewContent) {
//                    viewContent = true;
//                }
//            }
//        }
//
//    }
//
//    private void closeStatus() {
//        if (loadingView != null) {
//            loadingView.setVisibility(GONE);
//        }
//        if (contentView != null) {
//            contentView.setVisibility(GONE);
//        }
//        if (emptyView != null) {
//            emptyView.setVisibility(GONE);
//        }
//        if (errorView != null) {
//            errorView.setVisibility(GONE);
//        }
//    }
//
//
//}
