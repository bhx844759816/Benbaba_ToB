package com.bhx.common.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

import com.bhx.common.ui.R;

public class GridRadioGroup extends RadioGroup {
    private static final int VERTICAL_SPACING_DIP = 15;

    private static final int HORIZONTAL_SPACING_DIP = 10;

    private float verticalSpacing = 20;

    private float horizontalSpacing = 12;

    private int numColumns = 3;

    public GridRadioGroup(Context context) {
        this(context, null);
    }

    public GridRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CommonUi_GridRadioGroup);
        numColumns = attributes.getInt(R.styleable.CommonUi_GridRadioGroup_numColumns, numColumns);
        int tempHorSpacing = (int) (HORIZONTAL_SPACING_DIP * context.getResources().getDisplayMetrics().density);
        horizontalSpacing = attributes.getDimension(R.styleable.CommonUi_GridRadioGroup_horizontalSpacing, tempHorSpacing);
        int tempVerSpacing = (int) (VERTICAL_SPACING_DIP * context.getResources().getDisplayMetrics().density);
        verticalSpacing = attributes.getDimension(R.styleable.CommonUi_GridRadioGroup_verticalSpacing, (int) tempVerSpacing);
        attributes.recycle();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int contentWidth = r - l - getPaddingRight() - getPaddingLeft();
        int itemWidth = (int) (contentWidth - horizontalSpacing * (numColumns - 1)) / numColumns;
        int x = getPaddingLeft();// 横坐标开始
        int y = 0;//纵坐标开始
        int rows = 0;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int height = view.getMeasuredHeight();
            x += itemWidth;
            if (i % numColumns == 0) {
                x = getPaddingLeft() + itemWidth;
                rows++;
            }
            y = rows * height + (rows - 1) * (int) verticalSpacing + getPaddingTop();
            view.layout(x - itemWidth, y - height, x, y);
            x += horizontalSpacing;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int contentWidth = specWidth - getPaddingRight() - getPaddingLeft();
        int itemWidth = (int) (contentWidth - horizontalSpacing * (numColumns - 1)) / numColumns;
        int y = 0;//纵坐标开始
        int rows = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), MeasureSpec.UNSPECIFIED);
            int height = child.getMeasuredHeight();
            if (i % numColumns == 0) {
                rows++;
            }
            y = rows * height + (rows - 1) * (int) verticalSpacing + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(specWidth, y);
    }
}
