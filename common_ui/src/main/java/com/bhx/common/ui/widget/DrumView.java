package com.bhx.common.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bhx.common.ui.R;

/**
 * 自定义鼓view
 */
public class DrumView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 大圆/2-小圆/2 = size
     */
    private int size = 50;
    /**
     * 外圈颜色
     */
    private int outColor = Color.RED;
    /**
     * 内圈颜色
     */
    private int centerColor = Color.WHITE;
    /**
     * 线颜色
     */
    private int lineColor = Color.YELLOW;
    private OnItemClickListener onItemClickListener;

    public DrumView(Context context) {
        super(context);
        init(null, 0);
    }

    public DrumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DrumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DrumView, defStyle, 0);

        size = a.getInt(R.styleable.DrumView_size, size);
        outColor = a.getColor(R.styleable.DrumView_outColor, outColor);
        centerColor = a.getColor(R.styleable.DrumView_centerColor, centerColor);
        lineColor = a.getColor(R.styleable.DrumView_lineColor, lineColor);

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

//    private void invalidateTextPaintAndMeasurements() {
//
//    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int mWidth = MeasureSpec.getSize(widthMeasureSpec);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int startX = contentWidth / 2;
        int startY = contentHeight / 2;

        paint.setAntiAlias(true);
        paint.setColor(outColor);
        canvas.drawCircle(startX, startX, startX, paint);
        paint.setColor(lineColor);
        canvas.drawLine(startX, startY, (float) (startX + Math.sqrt(startX * startX / 2.0)), (float) (startY + Math.sqrt(startX * startX / 2.0)), paint);
        canvas.drawLine(startX, startY, (float) (startX - Math.sqrt(startX * startX / 2.0)), (float) (startY + Math.sqrt(startX * startX / 2.0)), paint);
        canvas.drawLine(startX, startY, (float) (startX - Math.sqrt(startX * startX / 2.0)), (float) (startY - Math.sqrt(startX * startX / 2.0)), paint);
        canvas.drawLine(startX, startY, (float) (startX + Math.sqrt(startX * startX / 2.0)), (float) (startY - Math.sqrt(startX * startX / 2.0)), paint);
        paint.setColor(centerColor);
        canvas.drawCircle(startX, startY, startX - size, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
//                break;
            case MotionEvent.ACTION_UP:
                if (onItemClickListener != null)
                    onItemClickListener.setOnItemClickListener(this);
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 鼓点击事件
     *
     * @param onItemClickListener OnItemClickListener
     */
    public void setOnDrumClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 点击监听
     */
    public interface OnItemClickListener {
        void setOnItemClickListener(View view);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getOutColor() {
        return outColor;
    }

    public void setOutColor(int outColor) {
        this.outColor = outColor;
    }

    public int getCenterColor() {
        return centerColor;
    }

    public void setCenterColor(int centerColor) {
        this.centerColor = centerColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }
}
