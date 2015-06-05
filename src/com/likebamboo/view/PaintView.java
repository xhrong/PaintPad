
package com.likebamboo.view;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.likebamboo.interfaces.*;
import com.likebamboo.painttools.*;
import com.likebamboo.shapes.*;
import com.likebamboo.utils.BitMapUtils;
import com.likebamboo.utils.PaintConstants.*;
import com.likebamboo.utils.PaintState;

import java.util.ArrayList;

import static com.likebamboo.utils.PaintConstants.*;

public class PaintView extends View implements UndoCommand {

    boolean canvasIsCreated = false;

    private Canvas mCanvas = null;

    private ToolInterface mCurrentPainter = null;

    /* Bitmap 的配置 */
    private Bitmap mBitmap = null;
    private int mBitmapWidth = 0;
    private int mBitmapHeight = 0;

    //  private int mBackGroundColor = DEFAULT.BACKGROUND_COLOR;

    /* paint 的配置 */
    private Paint mPaint = null;
    private paintPadUndoStack mUndoStack = null;

    private int mPenColor = DEFAULT.PEN_COLOR;
    private int mPenSize = PEN_SIZE.SIZE_1;
    private int mEraserSize = ERASER_SIZE.SIZE_1;
    private int mEraserType = ERASER_TYPE.LINE;
    private int mPenType = PEN_TYPE.PLAIN_PEN;
    private int mShapeType = SHAPE_TYPE.CURV;
    private Paint.Style mStyle = Paint.Style.STROKE;

    private PaintState state = PaintState.PEN;

    private PaintViewCallBack mCallBack = null;
    private ShapesInterface mCurrentShape = null;

    /**
     * 画笔样式为实心
     */


    /* 其他 的配置 */
    private boolean isTouchUp = false;

    private int mStackedSize = UNDO_STACK_SIZE;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mCanvas = new Canvas();
        mPaint = new Paint(Paint.DITHER_FLAG);
        mUndoStack = new paintPadUndoStack(this, mStackedSize);
        setupPen();
    }


    /**
     * 回调主函数的onHasDraw函数
     */
    public void setCallBack(PaintViewCallBack callBack) {
        mCallBack = callBack;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果是MOUSE，则不用执行TOUCH
        if (state == PaintState.MOUSE) {
            return true;
        }
        float x = event.getX();
        float y = event.getY();
        isTouchUp = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCanvas.setBitmap(mBitmap);
                setupPen();
                mCurrentPainter.touchDown(x, y);
                mUndoStack.clearRedo();
                if (mCallBack != null) {
                    mCallBack.onTouchDown();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentPainter.touchMove(x, y);
                if (state == PaintState.ERASER && mEraserType!=ERASER_TYPE.BLOCK) {
                    mCurrentPainter.draw(mCanvas);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentPainter.hasDraw()) {
                    mUndoStack.push(mCurrentPainter);
                    if (mCallBack != null) {
                        // 控制undo\redo的现实
                        mCallBack.onHasDraw();
                    }
                }


                mCurrentPainter.touchUp(x, y);
                // 只有在up的时候才在bitmap上画图，最终显示在view上
                mCurrentPainter.draw(mCanvas);

                invalidate();
                isTouchUp = true;
                break;
        }
        return true;
    }

    /**
     * 设置具体形状，需要注意的是构造函数中的Painter必须是新鲜出炉的
     */
    private void setShape() {
        if (mCurrentPainter instanceof Shapable) {
            switch (mShapeType) {
                case SHAPE_TYPE.CURV:
                    mCurrentShape = new Curv((Shapable) mCurrentPainter);
                    break;
                case SHAPE_TYPE.LINE:
                    mCurrentShape = new Line((Shapable) mCurrentPainter);
                    break;
                case SHAPE_TYPE.SQUARE:
                    mCurrentShape = new Square((Shapable) mCurrentPainter);
                    break;
                case SHAPE_TYPE.RECT:
                    mCurrentShape = new Rectangle((Shapable) mCurrentPainter);
                    break;
                case SHAPE_TYPE.CIRCLE:
                    mCurrentShape = new Circle((Shapable) mCurrentPainter);
                    break;
                case SHAPE_TYPE.OVAL:
                    mCurrentShape = new Oval((Shapable) mCurrentPainter);
                    break;
                case SHAPE_TYPE.STAR:
                    mCurrentShape = new Star((Shapable) mCurrentPainter);
                    break;
                default:
                    break;
            }
            ((Shapable) mCurrentPainter).setShap(mCurrentShape);
        }
    }


    @Override
    public void onDraw(Canvas cv) {
//        cv.drawBitmap(mBitmap, 0, 0, mPaint);
//        if(state==PaintState.PEN){
//
//            mCurrentPainter.draw(cv);
//        }else{
//            mCurrentPainter.draw(cv);
//        }

        // 在外部绘制的方法只有一种，就是先在bitmap上绘制，然后加载到cv
        cv.drawBitmap(mBitmap, 0, 0, mPaint);
        // TouchUp使用BitMap的canvas进行绘制，也就不用再View上绘制了
        if (!isTouchUp) {
            // 平时都只在view的cv上临时绘制
            // earaser不能再cv上绘制，需要直接绘制在bitmap上
            if (state == PaintState.PEN || (state==PaintState.ERASER && mEraserType==ERASER_TYPE.BLOCK)) {
               mCurrentPainter.draw(cv);
            }
        }
    }

    /**
     * 创建一个新的画笔
     */
    private void setupPen() {
        ToolInterface tool = null;
        if (state == PaintState.PEN) {
            switch (mPenType) {
                case PEN_TYPE.PLAIN_PEN:
                    tool = new PlainPen(mPenSize, mPenColor, mStyle);
                    break;
                case PEN_TYPE.BLUR:
                    tool = new BlurPen(mPenSize, mPenColor, mStyle);
                    break;
                case PEN_TYPE.EMBOSS:
                    tool = new EmbossPen(mPenSize, mPenColor, mStyle);
                    break;
                default:
                    break;
            }
            setShape();
        } else if (state == PaintState.ERASER) {
            switch (mEraserType) {
                case ERASER_TYPE.LINE:
                    tool = new Eraser(mEraserSize);
                    break;
                case ERASER_TYPE.BLOCK:
                    tool = new BlockEraser(mEraserSize);
                    break;
            }
        }
        mCurrentPainter = tool;

    }

    public void setState(PaintState state) {
        this.state = state;
    }

    public PaintState getState() {
        return this.state;
    }

    /**
     * 当此事件发生时，创建Bitmap并setCanvas
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!canvasIsCreated) {
            mBitmapWidth = w;
            mBitmapHeight = h;
            createCanvasBitmap(w, h);
            canvasIsCreated = true;
        }
    }

    /**
     * 得到当前view的截图
     */
    public Bitmap getSnapShoot() {
        // 获得当前的view的图片
        setDrawingCacheEnabled(true);
        buildDrawingCache(true);
        Bitmap bitmap = getDrawingCache(true);
        Bitmap bmp = BitMapUtils.duplicateBitmap(bitmap);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        // 将缓存清理掉
        setDrawingCacheEnabled(false);
        return bmp;
    }

    /**
     * 创建bitMap同时获得其canvas
     */
    private void createCanvasBitmap(int w, int h) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
    }


    /**
     * 清空屏幕
     *
     */
    public void clearAll() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        createCanvasBitmap(mBitmapWidth, mBitmapHeight);
        mUndoStack.clearAll();
        invalidate();
    }

    /**
     * 改变当前的Shap
     */
    public void setCurrentShapType(int type) {
        switch (type) {
            case SHAPE_TYPE.CURV:
            case SHAPE_TYPE.LINE:
            case SHAPE_TYPE.RECT:
            case SHAPE_TYPE.CIRCLE:
            case SHAPE_TYPE.OVAL:
            case SHAPE_TYPE.SQUARE:
            case SHAPE_TYPE.STAR:
                mShapeType = type;
                break;
            default:
                mShapeType = SHAPE_TYPE.CURV;
                break;
        }
    }

    /**
     * 得到当前画笔的类型
     */
    public int getPenType() {
        return mPenType;
    }

    /**
     * 改变当前画笔的大小
     */
    public void setPenSize(int size) {
        mPenSize = size;
    }

    public void setPenType(int penType) {
        mPenType = penType;
    }

    /**
     * 改变当前Eraser的大小
     */
    public void setEraserSize(int size) {
        mEraserSize = size;
    }

    public void setEraserType(int eraserType) {
        mEraserType = eraserType;
    }

    /**
     * 得到当前画笔的大小
     */
    public int getPenSize() {
        return mPenSize;
    }

    /**
     * 重置状态
     */
    public void resetState() {
        setPenType(PEN_TYPE.PLAIN_PEN);
        setPenColor(DEFAULT.PEN_COLOR);
        mUndoStack.clearAll();
    }


    /**
     * 改变画笔的颜色，在创建新笔的时候就能使用了
     */
    public void setPenColor(int color) {
        mPenColor = color;
    }

    /**
     * 得到penColor
     */
    public int getPenColor() {
        return mPenColor;
    }

    public void setPenStyle(Style style) {
        mStyle = style;
    }

    @Override
    public void undo() {
        if (null != mUndoStack) {
            mUndoStack.undo();
        }
    }

    @Override
    public void redo() {
        if (null != mUndoStack) {
            mUndoStack.redo();
        }
    }

    @Override
    public boolean canUndo() {
        return mUndoStack.canUndo();
    }

    @Override
    public boolean canRedo() {
        return mUndoStack.canRedo();
    }

    @Override
    public String toString() {
        return "mPaint" + mCurrentPainter + mUndoStack;
    }

    /*
     * ===================================内部类开始=================================
     * 内部类，负责undo、redo
     */
    public class paintPadUndoStack {
        private int m_stackSize = 0;

        private PaintView mPaintView = null;

        public ArrayList<ToolInterface> mUndoStack = new ArrayList<ToolInterface>();

        private ArrayList<ToolInterface> mRedoStack = new ArrayList<ToolInterface>();

        private ArrayList<ToolInterface> mOldActionStack = new ArrayList<ToolInterface>();

        public paintPadUndoStack(PaintView paintView, int stackSize) {
            mPaintView = paintView;
            m_stackSize = stackSize;
        }

        /**
         * 将painter存入栈中
         */
        public void push(ToolInterface penTool) {
            if (null != penTool) {
                // 如果undo已经存满
                if (mUndoStack.size() == m_stackSize && m_stackSize > 0) {
                    // 得到最远的画笔
                    ToolInterface removedTool = mUndoStack.get(0);
                    // 所有的笔迹增加
                    mOldActionStack.add(removedTool);
                    mUndoStack.remove(0);
                }

                mUndoStack.add(penTool);
            }
        }

        /**
         * 清空所有
         */
        public void clearAll() {
            mRedoStack.clear();
            mUndoStack.clear();
            mOldActionStack.clear();
        }

        /**
         * undo
         */
        public void undo() {
            if (canUndo() && null != mPaintView) {
                ToolInterface removedTool = mUndoStack.get(mUndoStack.size() - 1);
                mRedoStack.add(removedTool);
                mUndoStack.remove(mUndoStack.size() - 1);
                // 重新创建一份背景
                mPaintView.createCanvasBitmap(mPaintView.mBitmapWidth, mPaintView.mBitmapHeight);
                Canvas canvas = mPaintView.mCanvas;

                // First draw the removed tools from undo stack.
                for (ToolInterface paintTool : mOldActionStack) {
                    paintTool.draw(canvas);
                }

                for (ToolInterface paintTool : mUndoStack) {
                    paintTool.draw(canvas);
                }

                mPaintView.invalidate();
            }
        }

        /**
         * redo
         */
        public void redo() {
            if (canRedo() && null != mPaintView) {
                ToolInterface removedTool = mRedoStack.get(mRedoStack.size() - 1);
                mUndoStack.add(removedTool);
                mRedoStack.remove(mRedoStack.size() - 1);
                mPaintView.createCanvasBitmap(mPaintView.mBitmapWidth, mPaintView.mBitmapHeight);
                Canvas canvas = mPaintView.mCanvas;
                // 所有以前的笔迹都存放在removedStack中
                // First draw the removed tools from undo stack.
                for (ToolInterface sketchPadTool : mOldActionStack) {
                    sketchPadTool.draw(canvas);
                }
                // 不管怎样都是从撤销里面绘制，重做只是暂时的存储
                for (ToolInterface sketchPadTool : mUndoStack) {
                    sketchPadTool.draw(canvas);
                }

                mPaintView.invalidate();
            }
        }

        public boolean canUndo() {
            return (mUndoStack.size() > 0);
        }

        public boolean canRedo() {
            return (mRedoStack.size() > 0);
        }

        public void clearRedo() {
            mRedoStack.clear();
        }

        @Override
        public String toString() {
            return "canUndo" + canUndo();
        }
    }
    /* ==================================内部类结束 ================================= */

}
