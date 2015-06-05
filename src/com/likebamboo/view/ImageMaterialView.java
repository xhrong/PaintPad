package com.likebamboo.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by xhrong on 2015/5/29.
 */
public class ImageMaterialView extends ImageView {

    private float maxScale = 3f;
    private float minScale = .3f;

    private enum State {
        INIT, DRAG, ZOOM
    }

    private State state;

    private Paint paint = new Paint();
    private PointF last = new PointF();
    private float currentScale = 1f;
    private Matrix matrix;


    private int space = 1;

    public ImageMaterialView(Context context) {
        super(context);
        setUp(context);
    }

    public ImageMaterialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(context);
    }

    public ImageMaterialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUp(context);
    }

    /**
     * Set up the class. Method called by constructors.
     *
     * @param context
     */
    private void setUp(Context context) {
        super.setClickable(false);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        matrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //加个边框
        canvas.drawRect(new Rect(-space, -space, getWidth() + space, getHeight() + space), paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getRawX(), event.getRawY());
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.getLayoutParams();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                last.set(current);
                state = State.DRAG;
                break;

            case MotionEvent.ACTION_MOVE:
                if (state == State.DRAG) {
                    View v = this;
                    int dx = (int) event.getRawX() - (int) last.x;
                    int dy = (int) event.getRawY() - (int) last.y;
                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }
                    if (right > 1000) {
                        right = 1000;
                        left = right - v.getWidth();
                    }
                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }
                    if (bottom > 800) {
                        bottom = 800;
                        top = bottom - v.getHeight();
                    }
                    v.layout(left, top, right, bottom);
                    layoutParams.setMargins(left, top, 0, 0);
                    last.set(new PointF(event.getRawX(), event.getRawY()));
                }
                break;

            case MotionEvent.ACTION_UP:
                state = State.INIT;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                state = State.INIT;
                break;
        }

        setImageMatrix(matrix);
        invalidate();
        return true;
    }


    /**
     * Scale method for zooming
     *
     * @param focusX      X of center of scale
     * @param focusY      Y of center of scale
     * @param scaleFactor scale factor to zoom in/out
     */
    private void scale(float focusX, float focusY, float scaleFactor) {
        float lastScale = currentScale;
        float newScale = lastScale * scaleFactor;

        // Calculate next scale with resetting to max or min if required
        if (newScale > maxScale) {
            currentScale = maxScale;
            scaleFactor = maxScale / lastScale;
        } else if (newScale < minScale) {
            currentScale = minScale;
            scaleFactor = minScale / lastScale;
        } else {
            currentScale = newScale;
        }

        // Do scale

        matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);


    }
}
