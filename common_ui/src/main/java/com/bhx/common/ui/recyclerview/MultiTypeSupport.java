package com.bhx.common.ui.recyclerview;

/**
 * 多布局支持
 */
public interface MultiTypeSupport<T> {
    /**
     * @param item     item数据对象
     * @param position item位置
     * @return 布局的ID
     */
    int getMultiLayoutId(T item, int position);
}
