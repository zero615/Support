package com.zero.support.compat.recycler;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.databinding.OnRebindCallback;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;


import com.zero.support.compat.recycler.manager.StickyHeaders;

import java.util.List;

public abstract class BaseAdapter extends RecyclerView.Adapter<ItemViewHolder> implements StickyHeaders {
    static final Object DB_PAYLOAD = new Object();
    private static final OnItemClickListener defaultItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, ItemViewHolder holder) {
            Object o = holder.getItem();
            if (o instanceof Cell) {
                ((Cell) o).onItemClick(view, holder);
            }

        }
    };
    private static final OnItemLongClickListener defaultItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(View view, ItemViewHolder holder) {
            Object o = holder.getItem();
            if (o instanceof Cell) {
                return ((Cell) o).onLongItemClick(view, holder);
            }
            return false;
        }
    };
    private static ItemViewBinder defaultViewBinder = new ItemViewBinder() {
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemViewHolder(new TextView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position, List payloads) {
            TextView textView = (TextView) holder.itemView;
            textView.setText(String.valueOf(holder.<Object>getItem()));
        }
    };
    protected ClassTypeProvider provider;
    private OnItemClickListener mOnItemClickListener = defaultItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener = defaultItemLongClickListener;
    private RecyclerView recyclerView;
    private OnRebindCallback<ViewDataBinding> mOnRebindCallback;
    private boolean enableClick = false;

    public BaseAdapter() {
        this(new ClassTypeProvider());
    }

    public BaseAdapter(ClassTypeProvider provider) {
        this.provider = provider;
    }

    public void setEnableClick(boolean enableClick) {
        this.enableClick = enableClick;
    }

    public OnRebindCallback<ViewDataBinding> getOnRebindCallback() {
        if (mOnRebindCallback == null) {
            mOnRebindCallback = new OnRebindCallback<ViewDataBinding>() {
                @Override
                public boolean onPreBind(ViewDataBinding binding) {
                    final RecyclerView recyclerView = getCurrentRecyclerView();
                    if (recyclerView == null || recyclerView.isComputingLayout()) {
                        return true;
                    }
                    int childAdapterPosition = recyclerView.getChildAdapterPosition(binding.getRoot());
                    if (childAdapterPosition == RecyclerView.NO_POSITION) {
                        return true;
                    }
                    notifyItemChanged(childAdapterPosition, DB_PAYLOAD);
                    return false;
                }
            };
        }
        return mOnRebindCallback;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public boolean isStickyHeader(int position) {
        Object o = getItem(position);
        if (o instanceof StickyHeaders) {
            return ((StickyHeaders) o).isStickyHeader(position);
        }
        return false;
    }

    @Override
    public void onViewRecycled(@NonNull ItemViewHolder holder) {
        super.onViewRecycled(holder);
        ItemViewBinder binder = provider.getViewBinder(holder.getItemViewType());
        if (binder == null) {
            binder = defaultViewBinder;
        }
        binder.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull ItemViewHolder holder) {
        return super.onFailedToRecycleView(holder);
    }

    @NonNull
    @Override
    @CallSuper
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewBinder binder = provider.getViewBinder(viewType);
        if (binder == null) {
            binder = defaultViewBinder;
            binder.adapter = this;
        }
        binder.adapter = this;
        ItemViewHolder holder = binder.onCreateViewHolder(parent, viewType);
        if (enableClick) {
            InternalClickListener listener = new InternalClickListener(holder);
            holder.itemView.setOnClickListener(listener);
            holder.itemView.setOnLongClickListener(listener);
        }
        return holder;
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public final void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        throw new IllegalArgumentException("just overridden to make final.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull List<Object> payloads) {
        ItemViewBinder binder = provider.getViewBinder(holder.getItemViewType());
        if (binder == null) {
            binder = defaultViewBinder;
        }
        holder.setItem(getItem(position));
        binder.onBindViewHolder(holder, position, payloads);
    }

    public RecyclerView getCurrentRecyclerView() {
        return recyclerView;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewAttachedToWindow(@NonNull ItemViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ItemViewBinder binder = provider.getViewBinder(holder.getItemViewType());
        if (binder == null) {
            binder = defaultViewBinder;
        }
        binder.adapter = this;
        binder.onViewAttachedToWindow(holder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewDetachedFromWindow(@NonNull ItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ItemViewBinder binder = provider.getViewBinder(holder.getItemViewType());
        if (binder == null) {
            binder = defaultViewBinder;
        }
        binder.onViewDetachedFromWindow(holder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getItemViewType(int position) {
        return provider.getItemViewType(position, getItem(position));
    }

    @Override
    public abstract int getItemCount();

    public abstract Object getItem(int position);

    public @NonNull
    ClassTypeProvider getTypeProvider() {
        return provider;
    }

    public void setTypeProvider(ClassTypeProvider provider) {
        this.provider = provider;
    }

    public void addTypeProvider(ClassTypeProvider provider) {
        this.provider.copyFrom(provider);
    }

    private class InternalClickListener implements View.OnClickListener, View.OnLongClickListener {
        ItemViewHolder mHolder;

        public InternalClickListener(ItemViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = mOnItemClickListener;
            if (listener != null) {
                listener.onItemClick(v, mHolder);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            final OnItemLongClickListener listener = mOnItemLongClickListener;
            if (listener != null) {
                return listener.onItemLongClick(v, mHolder);
            }
            return false;
        }
    }
}
