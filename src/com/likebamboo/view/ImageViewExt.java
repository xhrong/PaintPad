package com.likebamboo.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageViewExt extends ImageView implements View.OnTouchListener {
    protected int parentWidth;
    protected int parentHeight;
    protected int lastX;
    protected int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    private int dragDirection;
    private static final int TOP = 0x15;
    private static final int LEFT = 0x16;
    private static final int BOTTOM = 0x17;
    private static final int RIGHT = 0x18;
    private static final int LEFT_TOP = 0x11;
    private static final int RIGHT_TOP = 0x12;
    private static final int LEFT_BOTTOM = 0x13;
    private static final int RIGHT_BOTTOM = 0x14;
    private static final int CENTER = 0x19;
    protected Paint rectPaint = new Paint();
    protected Paint cornorPaint = new Paint();

    private boolean isSelectable = true;
    private OnZIndexChangedListener onZIndexChangedListener;

    public void setOnZIndexChangedListener(OnZIndexChangedListener listener){
        onZIndexChangedListener=listener;
    }



    public void setParentWidth(int width) {
        this.parentWidth = width;
    }

    public void setParentHeight(int height) {
        this.parentHeight = height;
    }

    /**
     * 初始化获取屏幕宽高
     */
    protected void initScreenW_H() {
        // this.setPadding(10, 10, 10, 10);
        rectPaint.setColor(Color.BLUE);
        rectPaint.setStrokeWidth(4.0f);
        rectPaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{1, 2, 4, 8}, 1);
        rectPaint.setPathEffect(effects);

        cornorPaint.setColor(Color.BLUE);
        cornorPaint.setStrokeWidth(8.0f);
        cornorPaint.setStyle(Paint.Style.STROKE);
        parentHeight = getResources().getDisplayMetrics().heightPixels;
        parentWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public ImageViewExt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public ImageViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public ImageViewExt(Context context) {
        super(context);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public void setSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    public boolean getSelectable() {
        return this.isSelectable;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelectable) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), rectPaint);
            canvas.drawLines(new float[]{0, 30, 0, 0, 0, 0, 30, 0}, cornorPaint);
            canvas.drawLines(new float[]{0, getHeight() - 30, 0, getHeight(), 0, getHeight(), 30, getHeight()}, cornorPaint);
            canvas.drawLines(new float[]{getWidth() - 30, 0, getWidth(), 0, getWidth(), 0, getWidth(), 30}, cornorPaint);
            canvas.drawLines(new float[]{getWidth() - 30, getHeight(), getWidth(), getHeight(), getWidth(), getHeight(), getWidth(), getHeight() - 30}, cornorPaint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if(onZIndexChangedListener!=null){
                onZIndexChangedListener.onZIndexChanged();
            }
            oriLeft = v.getLeft();
            oriRight = v.getRight();
            oriTop = v.getTop();
            oriBottom = v.getBottom();
            lastY = (int) event.getRawY();
            lastX = (int) event.getRawX();
            dragDirection = getDirection(v, (int) event.getX(),
                    (int) event.getY());
        }
        // 处理拖动事件
        delDrag(v, event, action);
        invalidate();
        return false;
    }

    /**
     * 处理拖动事件
     *
     * @param v
     * @param event
     * @param action
     */
    protected void delDrag(View v, MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                switch (dragDirection) {
                    case LEFT: // 左边缘
                        left(v, dx);
                        break;
                    case RIGHT: // 右边缘
                        right(v, dx);
                        break;
                    case BOTTOM: // 下边缘
                        bottom(v, dy);
                        break;
                    case TOP: // 上边缘
                        top(v, dy);
                        break;
                    case CENTER: // 点击中心-->>移动
                        center(v, dx, dy);
                        break;
                    case LEFT_BOTTOM: // 左下
                        left(v, dx);
                        bottom(v, dy);
                        break;
                    case LEFT_TOP: // 左上
                        left(v, dx);
                        top(v, dy);
                        break;
                    case RIGHT_BOTTOM: // 右下
                        right(v, dx);
                        bottom(v, dy);
                        break;
                    case RIGHT_TOP: // 右上
                        right(v, dx);
                        top(v, dy);
                        break;
                }
                if (dragDirection != CENTER) {
                    v.layout(oriLeft, oriTop, oriRight, oriBottom);

                    ((RelativeLayout.LayoutParams) v.getLayoutParams()).setMargins(oriLeft, oriTop, 0, 0);
                    ((RelativeLayout.LayoutParams) v.getLayoutParams()).width = oriRight - oriLeft;
                    ((RelativeLayout.LayoutParams) v.getLayoutParams()).height = oriBottom - oriTop;
                }
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                dragDirection = 0;
                break;
        }
    }

    /**
     * 触摸点为中心->>移动
     *
     * @param v
     * @param dx
     * @param dy
     */
    private void center(View v, int dx, int dy) {
        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() + dx;
        int bottom = v.getBottom() + dy;
        if (left < 0) {
            left = 0;
            right = left + v.getWidth();
        }
        if (right > parentWidth) {
            right = parentWidth;
            left = right - v.getWidth();
        }
        if (top < 0) {
            top = 0;
            bottom = top + v.getHeight();
        }
        if (bottom > parentHeight) {
            bottom = parentHeight;
            top = bottom - v.getHeight();
        }
        v.layout(left, top, right, bottom);
        ((RelativeLayout.LayoutParams) v.getLayoutParams()).setMargins(left, top, 0, 0);
    }

    /**
     * 触摸点为上边缘
     *
     * @param v
     * @param dy
     */
    private void top(View v, int dy) {
        oriTop += dy;
        if (oriTop < 0) {
            oriTop = 0;
        }
        if (oriBottom - oriTop < 200) {
            oriTop = oriBottom - 200;
        }
    }

    /**
     * 触摸点为下边缘
     *
     * @param v
     * @param dy
     */
    private void bottom(View v, int dy) {
        oriBottom += dy;
        if (oriBottom > parentHeight) {
            oriBottom = parentHeight;
        }
        if (oriBottom - oriTop < 200) {
            oriBottom = 200 + oriTop;
        }
    }

    /**
     * 触摸点为右边缘
     *
     * @param v
     * @param dx
     */
    private void right(View v, int dx) {
        oriRight += dx;
        if (oriRight > parentWidth) {
            oriRight = parentWidth;
        }
        if (oriRight - oriLeft < 200) {
            oriRight = oriLeft + 200;
        }
    }

    /**
     * 触摸点为左边缘
     *
     * @param v
     * @param dx
     */
    private void left(View v, int dx) {
        oriLeft += dx;
        if (oriLeft < 0) {
            oriLeft = 0;
        }
        if (oriRight - oriLeft < 200) {
            oriLeft = oriRight - 200;
        }
    }

    /**
     * 获取触摸点flag
     *
     * @param v
     * @param x
     * @param y
     * @return
     */
    protected int getDirection(View v, int x, int y) {
        int left = v.getLeft();
        int right = v.getRight();
        int bottom = v.getBottom();
        int top = v.getTop();
        if (x < 40 && y < 40) {
            return LEFT_TOP;
        }
        if (y < 40 && right - left - x < 40) {
            return RIGHT_TOP;
        }
        if (x < 40 && bottom - top - y < 40) {
            return LEFT_BOTTOM;
        }
        if (right - left - x < 40 && bottom - top - y < 40) {
            return RIGHT_BOTTOM;
        }
        if (x < 40) {
            return LEFT;
        }
        if (y < 40) {
            return TOP;
        }
        if (right - left - x < 40) {
            return RIGHT;
        }
        if (bottom - top - y < 40) {
            return BOTTOM;
        }
        return CENTER;
    }

    public interface OnZIndexChangedListener{

        public void onZIndexChanged();
    }
}
