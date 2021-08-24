//package com.zero.support.vo;
//
//import androidx.databinding.ObservableBoolean;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MediatorLiveData;
//import androidx.lifecycle.Observer;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//public class ViewPage<T> extends BaseObject implements SwipeRefreshLayout.OnRefreshListener {
//
//    private final List<SwipeRefreshLayout.OnRefreshListener> mListeners = new CopyOnWriteArrayList<>();
//    private final ObservableBoolean empty = new ObservableBoolean();
//    private final ObservableBoolean error = new ObservableBoolean();
//    protected MediatorLiveData<Resource<T>> mResource = new MediatorLiveData<>();
//
//    public ViewPage() {
//        mResource.observeForever(new Observer<Resource<T>>() {
//            @Override
//            public void onChanged(Resource<T> resource) {
//
//                if (resource.isLoading()) {
//                    setRefreshing(true);
//                } else {
//                    if (resource.isSuccess()) {
//                        if (resource.data == null) {
//                            setEmpty(true);
//                        } else if (resource.data instanceof Collection) {
//                            setEmpty(((Collection<?>) resource.data).isEmpty());
//                        } else {
//                            setEmpty(false);
//                        }
//                    }
//                    setRefreshing(false);
//                }
//                onSubmit(resource);
//            }
//        });
//    }
//
//    public LiveData<Resource<T>> getResource() {
//        return mResource;
//    }
//
//    public void postResource(Resource<T> resource) {
//        mResource.postValue(resource);
//    }
//
//    protected void onSubmit(Resource<T> resource) {
//
//    }
//
//
//    public ObservableBoolean getEmpty() {
//        return empty;
//    }
//
//    public boolean isEmpty() {
//        return empty.get();
//    }
//
//    public void setEmpty(boolean empty) {
//        this.empty.set(empty);
//    }
//
//    public boolean isError() {
//        return error.get();
//    }
//
//    public ObservableBoolean getError() {
//        return error;
//    }
//
//    public void setError(boolean empty) {
//        this.error.set(empty);
//    }
//
//    public void addOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
//        mListeners.add(listener);
//    }
//
//    public void removeOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
//        mListeners.remove(listener);
//    }
//
//    @Override
//    public void onRefresh() {
//        for (SwipeRefreshLayout.OnRefreshListener listener : mListeners) {
//            listener.onRefresh();
//        }
//    }
//}
