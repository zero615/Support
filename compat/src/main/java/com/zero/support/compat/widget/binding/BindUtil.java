//package com.zero.support.compat.widget.binding;
//
//import android.util.Log;
//
//import androidx.databinding.BindingAdapter;
//import androidx.databinding.InverseBindingListener;
//import androidx.databinding.InverseBindingMethod;
//import androidx.databinding.InverseBindingMethods;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.zero.support.common.vo.ViewPage;
//import com.zero.support.common.widget.ViewPageLayout;
//
//@InverseBindingMethods({
//        @InverseBindingMethod(
//                type = SwipeRefreshLayout.class,
//                attribute = "refreshing",
//                event = "refreshingAttrChanged",
//                method = "isRefreshing")})
//public class BindUtil {
//
//
//    @BindingAdapter(value = {"onRefreshListener", "refreshingAttrChanged"}, requireAll = false)
//    public static void setOnRefreshListener(final SwipeRefreshLayout view,
//                                            final SwipeRefreshLayout.OnRefreshListener listener,
//                                            final InverseBindingListener refreshingAttrChanged) {
//        Log.d("bind", "setRefreshingListener" + listener + refreshingAttrChanged + view.isRefreshing());
//        SwipeRefreshLayout.OnRefreshListener newValue = new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.e("bind", "onRefresh: run ");
//                if (refreshingAttrChanged != null) {
//                    refreshingAttrChanged.onChange();
//                }
//                if (listener != null) {
//                    listener.onRefresh();
//                }
//            }
//        };
//        if (view.isRefreshing()) {
//            if (refreshingAttrChanged != null) {
//                refreshingAttrChanged.onChange();
//            }
//            if (listener != null) {
//                newValue.onRefresh();
//            }
//        }
//        view.setOnRefreshListener(newValue);
//        Log.d("bind", "setRefreshingListener" + newValue);
//
//    }
//
//    @BindingAdapter("refreshing")
//    public static void setRefreshing(SwipeRefreshLayout view, boolean refreshing) {
//        Log.d("bind", "setRefreshing" + refreshing);
//        if (refreshing != view.isRefreshing()) {
//            view.setRefreshing(refreshing);
//        }
//    }
//
//    @BindingAdapter("viewPage")
//    public static void setViewPage(ViewPageLayout view, ViewPage<?> viewPage) {
//        Log.d("bind", "setViewPage" + viewPage);
//        view.setViewPage(viewPage);
//    }
////    @InverseBindingAdapter(attribute = "refreshing", event = "refreshingAttrChanged")
////    public static boolean isRefreshing(SwipeRefreshLayout view) {
////        return view.isRefreshing();
////    }
//
//}
