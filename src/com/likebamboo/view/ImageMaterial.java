package com.likebamboo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.likebamboo.paintpad.R;

/**
 * Created by xhrong on 2015/6/1.
 */
public class ImageMaterial extends RelativeLayout implements View.OnTouchListener {

    ImageView image, topLeftImage, topRightImage, bottomLeftImage, bottomRightImage;

    RelativeLayout image_material_root;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        PointF current = new PointF(event.getRawX(), event.getRawY());
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                last.set(current);
                if(view.getId()!=R.id.image)
                    state=State.ZOOM;
                else
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
                }else if(state==State.ZOOM){
                    View v = this;
                    int dx = (int) event.getRawX() - (int) last.x;
                    int dy = (int) event.getRawY() - (int) last.y;

                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;

                    int right = v.getRight();
                    int bottom = v.getBottom();
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
                  //  v.layout(left, top, right, bottom);

                    layoutParams.height=bottom-top;
                    Log.e("DX:",dx+"");
                    layoutParams.width=right-left;

                 //   layoutParams.setMargins(left, top, 0, 0);
                    v.setLayoutParams(new RelativeLayout.LayoutParams(400,400));
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

        //setImageMatrix(matrix);
        invalidate();
        return true;
    }

    private enum State {
        INIT, DRAG, ZOOM
    }

    private State state;

    private Paint paint = new Paint();
    private PointF last = new PointF();
    private float currentScale = 1f;
    private Matrix matrix;

    public ImageMaterial(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public ImageMaterial(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ImageMaterial(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        // TODO Auto-generated method stub
        View.inflate(context, R.layout.imagemeterial, this);
        image_material_root=(RelativeLayout)findViewById(R.id.image_material_root);
        image = (ImageView) findViewById(R.id.image);
        topLeftImage = (ImageView) findViewById(R.id.top_left);
        topRightImage = (ImageView) findViewById(R.id.top_right);
        bottomLeftImage = (ImageView) findViewById(R.id.bottom_left);
        bottomRightImage = (ImageView) findViewById(R.id.bottom_right);
        image.setOnTouchListener(this);
        topRightImage.setOnTouchListener(this);
        topLeftImage.setOnTouchListener(this);
        bottomLeftImage.setOnTouchListener(this);
        bottomRightImage.setOnTouchListener(this);
    }

    public void setSrc(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }



//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        PointF current = new PointF(event.getRawX(), event.getRawY());
//        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.getLayoutParams();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                last.set(current);
//                state = State.DRAG;
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                if (state == State.DRAG) {
//                    View v = this;
//                    int dx = (int) event.getRawX() - (int) last.x;
//                    int dy = (int) event.getRawY() - (int) last.y;
//                    int left = v.getLeft() + dx;
//                    int top = v.getTop() + dy;
//                    int right = v.getRight() + dx;
//                    int bottom = v.getBottom() + dy;
//                    if (left < 0) {
//                        left = 0;
//                        right = left + v.getWidth();
//                    }
//                    if (right > 1000) {
//                        right = 1000;
//                        left = right - v.getWidth();
//                    }
//                    if (top < 0) {
//                        top = 0;
//                        bottom = top + v.getHeight();
//                    }
//                    if (bottom > 800) {
//                        bottom = 800;
//                        top = bottom - v.getHeight();
//                    }
//                    v.layout(left, top, right, bottom);
//                    layoutParams.setMargins(left, top, 0, 0);
//                    last.set(new PointF(event.getRawX(), event.getRawY()));
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//                state = State.INIT;
//                break;
//
//            case MotionEvent.ACTION_POINTER_UP:
//                state = State.INIT;
//                break;
//        }
//
//        //setImageMatrix(matrix);
//        invalidate();
//        return true;
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.top_left:
//            case R.id.top_right:
//            case R.id.bottom_left:
//            case R.id.bottom_right:
//                Toast.makeText(this.getContext(),"CLICK",Toast.LENGTH_SHORT).show();
//        }
//    }
}
