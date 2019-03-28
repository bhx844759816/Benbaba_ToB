package com.bhx.common.ui.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 多布局列表的支持
 *
 * @param <T>
 */
public abstract class MultiTypeAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private MultiTypeSupport<T> multiTypeSupport;
    private List<T> mDatas;

    public MultiTypeAdapter(Context context, List<T> datas, MultiTypeSupport<T> multiTypeSupport) {
        this.mContext = context;
        this.mDatas = datas;
        this.multiTypeSupport = multiTypeSupport;
    }

    @Override
    public int getItemViewType(int position) {
        if (multiTypeSupport != null) {
            return multiTypeSupport.getMultiLayoutId(mDatas.get(position), position);
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (multiTypeSupport == null) {
            throw new RuntimeException("multiTypeSupport can not null");
        }
        View view = mInflater.inflate(viewType, viewGroup);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        convert(holder,mDatas.get(position));
    }

    public abstract void convert(ViewHolder holder, T item);
}
