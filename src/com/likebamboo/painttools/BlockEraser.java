package com.likebamboo.painttools;

import android.graphics.*;
import com.likebamboo.interfaces.ToolInterface;

/**
 * Created by xhrong on 2015/6/2.
 */
public class BlockEraser implements ToolInterface {

    // 只有等移动距离超过这个值才会移动
    private static final float TOUCH_TOLERANCE = 4.0f;

    private float mCurrentX = 0.0f;
    private float mCurrentY = 0.0f;
    private Path mPath = new Path();
    private Paint mEraserPaint = new Paint();
    private Paint mRectPaint = new Paint();


    private boolean mHasDraw = false;
    private int eraserSize = 0;
    RectF clearRect;
    PathEffect effects;

    public BlockEraser(int eraserSize) {
        mEraserPaint.setStrokeWidth(eraserSize);
        this.eraserSize = eraserSize;
        setUp();
    }

    private void setUp() {
        // color并不中还要，混色的模式决定了eraser
        mEraserPaint.setColor(Color.BLACK);
        mEraserPaint.setDither(true);
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setStyle(Paint.Style.FILL);
        mEraserPaint.setStrokeWidth(4);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.SQUARE);
        mEraserPaint .setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));


        effects = new DashPathEffect(new float[]{5,5,5,5},1);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(1);
        mRectPaint.setColor(Color.RED);
        mRectPaint.setPathEffect(effects);

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(clearRect, mRectPaint);
        if (null != canvas) {
            canvas.drawPath(mPath, mEraserPaint);
        }
    }

    @Override
    public void touchDown(float x, float y) {
        mPath.reset();
        mCurrentX = x;
        mCurrentY = y;
        clearRect = new RectF(mCurrentX, mCurrentY, x, y);

    }

    @Override
    public void touchMove(float x, float y) {
        if (isMoved(x, y)) {
            drawRect(x, y);
            mHasDraw = true;
        }

    }

    @Override
    public void touchUp(float x, float y) {

        mPath.addRect(Math.min(mCurrentX,x)-1,Math.min(mCurrentY,y)-1, Math.max(mCurrentX,x)+1, Math.max(mCurrentY,y)+1, Path.Direction.CCW);
    }

    @Override
    public boolean hasDraw() {
        return mHasDraw;
    }

    // 判断是否移动
    private boolean isMoved(float x, float y) {
        float dx = Math.abs(x - mCurrentX);
        float dy = Math.abs(y - mCurrentX);
        boolean isMoved = ((dx >= TOUCH_TOLERANCE) || (dy >= TOUCH_TOLERANCE));
        return isMoved;
    }

    // 画出贝塞尔曲线
    private void drawRect(float x, float y) {

        clearRect.top = mCurrentY;
        clearRect.left = mCurrentX;
        clearRect.right = x;
        clearRect.bottom = y;
    }


    @Override
    public String toString() {
        return "eraser：" + " size is" + eraserSize;
    }
}
