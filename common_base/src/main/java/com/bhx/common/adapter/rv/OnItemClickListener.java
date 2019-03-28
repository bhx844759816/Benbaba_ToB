package com.bhx.common.adapter.rv;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView得点击事件
 */
public interface OnItemClickListener {

    void onItemClick(View view, ViewHolder holder, int position);

    boolean onItemLongClick(View view, ViewHolder holder, int position);

}
