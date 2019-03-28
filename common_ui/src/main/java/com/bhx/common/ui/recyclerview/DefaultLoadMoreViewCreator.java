package com.bhx.common.ui.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 上拉加载更多样式
 */
public class DefaultLoadMoreViewCreator extends LoadViewCreator {

    @Override
    public View getLoadView(Context context, ViewGroup parent) {
        return null;
    }

    @Override
    public void onPull(int currentDragHeight, int loadViewHeight, int currentLoadStatus) {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onStopLoad() {

    }
}
