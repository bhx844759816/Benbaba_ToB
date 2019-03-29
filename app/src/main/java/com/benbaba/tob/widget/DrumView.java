package com.benbaba.tob.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.benbaba.tob.R;
import com.bhx.common.utils.DensityUtil;

/**
 * 展示玩具鼓状态得View
 */
public class DrumView extends View {
    private Paint mPaint;

    private Paint mTextPaint;

    private Paint mCirclePaint;

    private Bitmap mCorrectBitmap;
    private Bitmap mErrorBitmap;

    private String bgColor = "#C58CA1";
    private String textColor = "#35272B";

    private int mViewSize;
    private int mTextTopMargin;

    private String text = "dadpat";

    private boolean isCurSetting ;//是否正在配置
    private int isSetResult = -1;

    public void setCurSetting(boolean curSetting) {
        isCurSetting = curSetting;
    }

    public void setIsSetResult(int isSetResult) {
        this.isSetResult = isSetResult;
    }

    public DrumView(Context context) {
        super(context);
        init(context);
    }

    public DrumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor(bgColor));
        paint.setStyle(Paint.Style.FILL);
        mPaint = paint;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor(textColor));
        paint.setTextSize(DensityUtil.dip2px(context, 14));
        paint.setTextAlign(Paint.Align.CENTER);
        mTextPaint = paint;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(8);
        mCirclePaint = paint;
        mTextTopMargin = DensityUtil.dip2px(context, 30);
        mCorrectBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_correct);
        mErrorBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_error);
        mViewSize = DensityUtil.dip2px(context, 100);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mViewSize + 5, mViewSize + 8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mViewSize >> 1, mViewSize >> 1, (mViewSize >> 1) - 8, mCirclePaint);
        //绘制背景
        canvas.drawCircle(mViewSize >> 1, mViewSize >> 1, (mViewSize >> 1) - 8, mPaint);
        mTextPaint.setTextSize(45);
        //绘制文本
        canvas.drawText(text, mViewSize >> 1, mTextTopMargin, mTextPaint);
        if (isCurSetting) {
            mTextPaint.setTextSize(30);
            if (isSetResult == -1) {
                canvas.drawText("正在配置...", mViewSize >> 1, mViewSize * 2.0f / 3, mTextPaint);
            } else if (isSetResult == 0) {//配置成功
                String text = "配置成功";
                canvas.drawText(text, mViewSize >> 1, mViewSize * 2.0f / 3, mTextPaint);
                canvas.drawBitmap(mCorrectBitmap, (mViewSize >> 1) + mTextPaint.measureText(text) / 2 + 5,
                        mViewSize * 2 / 3 - mCorrectBitmap.getHeight() / 2 - 8, mTextPaint);
            } else if (isSetResult == 1) {//配置失败
                String text = "配置失败";
                canvas.drawText(text, mViewSize >> 1, mViewSize * 2.0f / 3, mTextPaint);
                canvas.drawBitmap(mErrorBitmap, (mViewSize >> 1) + mTextPaint.measureText(text) / 2 + 5,
                        mViewSize * 2 / 3 - mErrorBitmap.getHeight() / 2 - 8, mTextPaint);
            }
        }

    }
}
