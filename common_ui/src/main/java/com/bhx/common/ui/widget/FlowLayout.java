package com.bhx.common.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局
 */
public class FlowLayout extends ViewGroup {
    //    //存储所有子View
//    private List<List<View>> mAllChildViews = new ArrayList<>();
//    //每一行的高度
//    private List<Integer> mLineHeight = new ArrayList<>();
//    //存储每一行得View
//    private List<View> mLineViews = new ArrayList<>();
//
//    public FlowLayout(Context context) {
//        super(context);
//    }
//
//    public FlowLayout(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        // TODO Auto-generated method stub
//
//        //父控件传进来的宽度和高度以及对应的测量模式
//        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
//        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
//        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
//
//        //如果当前ViewGroup的宽高为wrap_content的情况
//        int width = 0;//自己测量的 宽度
//        int height = 0;//自己测量的高度
//        //记录每一行的宽度和高度
//        int lineWidth = 0;
//        int lineHeight = 0;
//
//        //获取子view的个数
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = getChildAt(i);
//            //测量子View的宽和高
//            measureChild(child, widthMeasureSpec, heightMeasureSpec);
//            //得到LayoutParams
//            MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
//            //子View占据的宽度
//            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
//            //子View占据的高度
//            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
//            //换行时候
//            if (lineWidth + childWidth > sizeWidth) {
//                //对比得到最大的宽度
//                width = Math.max(width, lineWidth);
//                //重置lineWidth
//                lineWidth = childWidth;
//                //记录行高
//                height += lineHeight;
//                lineHeight = childHeight;
//            } else {//不换行情况
//                //叠加行宽
//                lineWidth += childWidth;
//                //得到最大行高
//                lineHeight = Math.max(lineHeight, childHeight);
//            }
//            //处理最后一个子View的情况
//            if (i == childCount - 1) {
//                width = Math.max(width, lineWidth);
//                height += lineHeight;
//            }
//        }
//        //wrap_content
//        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width,
//                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height);
//
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        // TODO Auto-generated method stub
//        mAllChildViews.clear();
//        mLineHeight.clear();
//        //获取当前ViewGroup的宽度
//        int width = getWidth();
//
//        int lineWidth = 0;
//        int lineHeight = 0;
//        //记录当前行的view
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = getChildAt(i);
//            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
//            int childWidth = child.getMeasuredWidth();
//            int childHeight = child.getMeasuredHeight();
//
//            //如果需要换行
//            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width) {
//                //记录LineHeight
//                mLineHeight.add(lineHeight);
//                //记录当前行的Views
//                mAllChildViews.add(mLineViews);
//                //重置行的宽高
//                lineWidth = 0;
//                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
//                //重置view的集合
//                mLineViews = new ArrayList<>();
//            }
//            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
//            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
//            mLineViews.add(child);
//        }
//        //处理最后一行
//        mLineHeight.add(lineHeight);
//        mAllChildViews.add(mLineViews);
//        //设置子View的位置
//        int left = 0;
//        int top = 0;
//        //获取行数
//        int lineCount = mAllChildViews.size();
//        for (int i = 0; i < lineCount; i++) {
//            //当前行的views和高度
//            List<View> lineViews = mAllChildViews.get(i);
//            lineHeight = mLineHeight.get(i);
//            for (int j = 0; j < lineViews.size(); j++) {
//                View child = lineViews.get(j);
//                //判断是否显示
//                if (child.getVisibility() == View.GONE) {
//                    continue;
//                }
//                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
//                int cLeft = left + lp.leftMargin;
//                int cTop = top + lp.topMargin;
//                int cRight = cLeft + child.getMeasuredWidth();
//                int cBottom = cTop + child.getMeasuredHeight();
//                //进行子View进行布局
//                child.layout(cLeft, cTop, cRight, cBottom);
//                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
//            }
//            left = getLeft();
//            top += lineHeight;
//        }
//
//    }
//
//    /**
//     * 与当前ViewGroup对应的LayoutParams
//     */
//    @Override
//    public LayoutParams generateLayoutParams(AttributeSet attrs) {
//        return new MarginLayoutParams(getContext(), attrs);
//    }
    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private List<List<View>> mLineViews = new ArrayList<>();
    private List<Integer> mLineHeight = new ArrayList<>();

    /**
     * 测量所有子View大小,确定ViewGroup的宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //由于onMeasure会执行多次,避免重复的计算控件个数和高度,这里需要进行清空操作
        mLineViews.clear();
        mLineHeight.clear();

        //获取测量的模式和尺寸大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec) + getPaddingTop() + getPaddingBottom();


        //记录ViewGroup真实的测量宽高
        int viewGroupWidth = 0 - getPaddingLeft() - getPaddingRight();
        int viewGroupHeight = getPaddingTop() + getPaddingBottom();

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            viewGroupWidth = widthSize;
            viewGroupHeight = heightSize;
        } else {
            //当前所占的宽高
            int currentLineWidth = 0;
            int currentLineHeight = 0;

            //用来存储每一行上的子View
            List<View> lineView = new ArrayList<>();
            int childViewsCount = getChildCount();
            for (int i = 0; i < childViewsCount; i++) {
                View childView = getChildAt(i);
                //对子View进行测量
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int childViewWidth = childView.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                int childViewHeight = childView.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;

                if (currentLineWidth + childViewWidth > widthSize) {
                    //当前行宽+子View+左右外边距>ViewGroup的宽度,换行
                    viewGroupWidth = Math.max(currentLineWidth, widthSize);
                    viewGroupHeight += currentLineHeight;
                    //添加行高
                    mLineHeight.add(currentLineHeight);
                    //添加行对象
                    mLineViews.add(lineView);

                    //new新的一行
                    lineView = new ArrayList<View>();
                    //添加行对象里的子View
                    lineView.add(childView);
                    currentLineWidth = childViewWidth;

                } else {
                    //当前行宽+子View+左右外边距<=ViewGroup的宽度,不换行
                    currentLineWidth += childViewWidth;
                    currentLineHeight = Math.max(currentLineHeight, childViewHeight);
                    //添加行对象里的子View
                    lineView.add(childView);
                }


                if (i == childViewsCount - 1) {
                    //最后一个子View的时候
                    //添加行对象
                    mLineViews.add(lineView);
                    viewGroupWidth = Math.max(childViewWidth, viewGroupWidth);
                    viewGroupHeight += childViewHeight;
                    //添加行高
                    mLineHeight.add(currentLineHeight);

                }
            }

        }
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : viewGroupWidth,
                heightMode == MeasureSpec.EXACTLY ? heightSize : viewGroupHeight);

    }

    /**
     * 设置ViewGroup里子View的具体位置
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int left = getPaddingLeft();
        int top = getPaddingTop();
        //一共有几行
        int lines = mLineViews.size();
        for (int i = 0; i < lines; i++) {
            //每行行高
            int lineHeight = mLineHeight.get(i);
            //行内有几个子View
            List<View> viewList = mLineViews.get(i);
            int views = viewList.size();

            for (int j = 0; j < views; j++) {
                View view = viewList.get(j);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
                int vl = left + marginLayoutParams.leftMargin;
                int vt = top + marginLayoutParams.topMargin;
                int vr = vl + view.getMeasuredWidth();
                int vb = vt + view.getMeasuredHeight();
                view.layout(vl, vt, vr, vb);
                left += view.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight;

        }


    }

    /**
     * 指定ViewGroup的LayoutParams
     *
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

}
