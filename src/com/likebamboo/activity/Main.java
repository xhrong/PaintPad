
package com.likebamboo.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import com.likebamboo.interfaces.EditTextDialogListener;
import com.likebamboo.interfaces.OnClickOkListener;
import com.likebamboo.interfaces.PaintViewCallBack;
import com.likebamboo.paintpad.R;
import com.likebamboo.utils.BitMapUtils;
import com.likebamboo.utils.ImageButtonTools;
import com.likebamboo.utils.PaintConstants;
import com.likebamboo.utils.PaintConstants.ERASER_SIZE;
import com.likebamboo.utils.PaintConstants.ERASER_TYPE;
import com.likebamboo.utils.PaintConstants.PEN_SIZE;
import com.likebamboo.utils.PaintConstants.PEN_TYPE;
import com.likebamboo.utils.PaintState;
import com.likebamboo.view.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity实现了主场景的Activity主要负责PaintView与各组件的协调
 *
 * @author rf
 */
public class Main extends Activity implements OnClickListener {


    // PaintView
    private PaintView mPaintView = null;

    //left buttons
    private ImageButton mouseButton = null;
    private ImageButton penButton = null;
    private ImageButton eraserButton = null;
    private ImageButton cameraButton = null;
    private ImageButton addImageButton = null;
    private ImageButton undoButton = null;
    private ImageButton redoButton = null;
    private ImageButton saveButton = null;

    private PaintState state = PaintState.PEN;
    private RelativeLayout paintViewLayout = null;

    List<ImageViewExt> imageMaterials = new ArrayList<ImageViewExt>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        init();
        setState(PaintState.PEN);
    }

    private void init() {
        initLayout();
        initButtons();
        initPaintView();
        initCallBack();
    }

    /**
     * 初始化paintView的回调函数
     */
    private void initCallBack() {
        mPaintView.setCallBack(new PaintViewCallBack() {
            // 当画了之后对Button进行更新
            @Override
            public void onHasDraw() {
                enableUndoButton();
                disableRedoButton();
            }

            // 当点击之后让各个弹出的窗口都消失
            @Override
            public void onTouchDown() {

            }
        });
    }


    /**
     * 初始化画画所用的paintView
     */
    private void initPaintView() {
        mPaintView = new PaintView(this);
        paintViewLayout.addView(mPaintView);
    }

    /**
     * 初始化所用到的Layout
     */
    private void initLayout() {

        paintViewLayout = (RelativeLayout) findViewById(R.id.paintViewLayout);
    }


    /**
     * 初始化所有的Button
     */
    private void initButtons() {
        findButtonById();
        setBackGroundDrawable();
        List<ImageButton> list = initButtonList();
        for (ImageButton imageButton : list) {
            ImageButtonTools.setButtonFocusChanged(imageButton);
            imageButton.setOnClickListener(this);
        }
    }

    /**
     * 将需要处理的ImageButton加入到List中
     */
    private List<ImageButton> initButtonList() {
        List<ImageButton> list = new ArrayList<ImageButton>();
        list.add(mouseButton);
        list.add(penButton);
        list.add(eraserButton);
        list.add(cameraButton);
        list.add(addImageButton);
        list.add(undoButton);
        list.add(redoButton);
        list.add(saveButton);
        return list;
    }

    /**
     * 找到所有的通过所有的button
     */
    private void findButtonById() {
        mouseButton = (ImageButton) findViewById(R.id.ibtMouse);
        penButton = (ImageButton) findViewById(R.id.ibtPen);
        eraserButton = (ImageButton) findViewById(R.id.ibtEraser);
        cameraButton = (ImageButton) findViewById(R.id.ibtCamera);
        addImageButton = (ImageButton) findViewById(R.id.ibtAddImage);
        undoButton = (ImageButton) findViewById(R.id.ibtUndo);
        redoButton = (ImageButton) findViewById(R.id.ibtRedo);
        saveButton = (ImageButton) findViewById(R.id.ibtSave);
    }

    /**
     * 初始化所有Button的Drawable
     */
    private void setBackGroundDrawable() {
        mouseButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.mouse));
        penButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pen));
        eraserButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.eraser));
        cameraButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.camera));
        addImageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.addimage));
        redoButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.redo_disable));
        undoButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.undo_disable));
        saveButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.save));
    }

    /**
     * onClick函数
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtMouse:
                onClickMouseButton();
                break;
            case R.id.ibtPen:
                onClickPenButton();
                break;
            case R.id.ibtCamera:
                onClickCameraButton();
            case R.id.ibtAddImage:
                onClickAddImageButton();
                break;
            case R.id.ibtSave:
                onClickButtonSave();
                break;
            case R.id.ibtEraser:
                onClickButtonEraser();
                break;
            case R.id.ibtUndo:
                onClickButtonUndo();
                break;
            case R.id.ibtRedo:
                onClickButtonRedo();
                break;
            default:
                break;
        }
    }

    public void setState(PaintState state) {
        this.state = state;
        switch (this.state) {
            case MOUSE:
                setCurrentMenuItem(mouseButton);
                bringAllImageToFront();
                break;
            case PEN:
                setCurrentMenuItem(penButton);
                bringPaintViewToFront();
                break;
            case ERASER:
                setCurrentMenuItem(eraserButton);
                bringPaintViewToFront();
                break;
            default:
                break;

        }
        mPaintView.setState(state);
    }

    public PaintState getState() {
        return this.state;
    }

    private void onClickMouseButton() {
        setState(PaintState.MOUSE);
    }

    private void onClickPenButton() {
        setState(PaintState.PEN);
        showPenPopWindow((View) penButton.getParent());
    }

    /**
     * 橡皮
     */
    private void onClickButtonEraser() {
        setState(PaintState.ERASER);
        showEraserPopWindow((View) eraserButton.getParent());
    }


    private void onClickCameraButton() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, PaintConstants.GET_CAMERA_ACTIVITY);

    }

    /**
     * 载入图片
     */
    private void onClickAddImageButton() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PaintConstants.GET_IMAGE_ACTIVITY);
    }


    private void setCurrentMenuItem(View currentMenuItem) {
        ((View) mouseButton.getParent()).setBackgroundColor(Color.TRANSPARENT);
        ((View) penButton.getParent()).setBackgroundColor(Color.TRANSPARENT);
        ((View) eraserButton.getParent()).setBackgroundColor(Color.TRANSPARENT);
        ((View) currentMenuItem.getParent()).setBackgroundColor(Color.LTGRAY);
    }

    private void bringAllImageToFront() {

        for (ImageViewExt img : imageMaterials) {
            paintViewLayout.bringChildToFront(img);
            img.setSelectable(true);
        }
    }

    private void bringImageToFront(ImageViewExt img) {
        paintViewLayout.bringChildToFront(img);
        img.setSelectable(true);
        imageMaterials.remove(img);
        imageMaterials.add(img);
    }

    private void bringPaintViewToFront() {
        for (ImageViewExt img : imageMaterials) {
            img.setSelectable(false);
        }
        paintViewLayout.bringChildToFront(mPaintView);
    }


    /**
     * 当点击menu的时候将popupwindow伪装成menu显示
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // 点击返回
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            OkCancleDialog returnDialog = new OkCancleDialog(this, new OnClickOkListener() {
                @Override
                public void onClickOk() {
                    finish();
                }
            });
            returnDialog.show();
            returnDialog.setMessage("确定要退出么？");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 载入之后得到路径
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaintConstants.GET_IMAGE_ACTIVITY:
            case PaintConstants.GET_CAMERA_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        addImage(uri);
                        setState(PaintState.MOUSE);
                    }
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void addImage(Uri uri) {
        ContentResolver cr = this.getContentResolver();
        try {
            Bitmap bitmap;
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(cr.openInputStream(uri), null, op);
            int wRatio = (int) Math.ceil(op.outWidth / (float) mPaintView.getWidth());
            int hRatio = (int) Math.ceil(op.outHeight / (float) mPaintView.getHeight());
            // 如果超出指定大小，则缩小相应的比例
            if (wRatio > 1 && hRatio > 1) {
                if (wRatio > hRatio) {
                    op.inSampleSize = wRatio;
                } else {
                    op.inSampleSize = hRatio;
                }
            }
            op.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, op);
            addImage(bitmap);
        } catch (Exception e) {
            return;
        }
    }

    private void addImage(Bitmap bitmap) {
        final ImageViewExt imageView = new ImageViewExt(Main.this);
        imageView.setClickable(true);
        imageView.setParentWidth(paintViewLayout.getWidth());
        imageView.setParentHeight(paintViewLayout.getHeight());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageBitmap(bitmap);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(100, 100, 0, 0);
        layoutParams.height = mPaintView.getHeight() / 2;
        layoutParams.width = mPaintView.getWidth() / 2;
        ((RelativeLayout) findViewById(R.id.paintViewLayout)).addView(imageView, layoutParams);
        imageMaterials.add(imageView);


        imageView.setOnZIndexChangedListener(new ImageViewExt.OnZIndexChangedListener() {
            @Override
            public void onZIndexChanged() {
                bringImageToFront(imageView);
            }
        });
    }

    /**
     * redo
     */
    private void onClickButtonRedo() {
        mPaintView.redo();
        upDateUndoRedo();
    }

    /**
     * undo
     */
    private void onClickButtonUndo() {
        mPaintView.undo();
        upDateUndoRedo();
    }

    /**
     * 更新UndoRedo Button
     */
    private void upDateUndoRedo() {
        if (mPaintView.canUndo()) {
            enableUndoButton();
        } else {
            disableUndoButton();
        }
        if (mPaintView.canRedo()) {
            enableRedoButton();
        } else {
            disableRedoButton();
        }
    }

    private void enableRedoButton() {
        redoButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.redo_enable));
    }

    private void disableUndoButton() {
        undoButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.undo_disable));
    }


    /**
     * 发送广播，更新sd卡中的数据库
     */
    private void sendUpdateBroadCast() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addDataScheme("file");
    }

    /**
     * 保存
     */
    private void onClickButtonSave() {
        boolean sdCardIsMounted = android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (!sdCardIsMounted) {
            OkDialog okDialog = new OkDialog(this, new OnClickOkListener() {
                @Override
                public void onClickOk() {
                }
            });
            okDialog.show();
            okDialog.setMessage("请插入存储卡");
        } else {
            SaveDialog dialog = new SaveDialog(this, new EditTextDialogListener() {
                // 当点击确定的时候自动调用 getDialogText接口
                @Override
                public void getDialogText(String string) {
                    String sdDir = getDirPath();
                    String file = sdDir + string + ".png";
                    Bitmap bitmap =getSnapShoot();
                    BitMapUtils.saveToSdCard(file, bitmap);
                    sendUpdateBroadCast();
                }
            });
            dialog.show();
        }
    }

    /**
     * 得到当前view的截图
     */
    public Bitmap getSnapShoot() {
        // 获得当前的view的图片
        paintViewLayout.setDrawingCacheEnabled(true);
        paintViewLayout.buildDrawingCache(true);
        Bitmap bitmap =  paintViewLayout.getDrawingCache(true);
        Bitmap bmp = BitMapUtils.duplicateBitmap(bitmap);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        // 将缓存清理掉
        paintViewLayout. setDrawingCacheEnabled(false);
        return bmp;
    }

    /**
     * 得到存储路径
     */
    private String getDirPath() {
        File sdcarddir = android.os.Environment.getExternalStorageDirectory();
        String dirString = sdcarddir.getPath() + "/paintPad/";
        File filePath = new File(dirString);
        if (!filePath.exists()) {
            // 如果无法创建
            if (!filePath.mkdirs()) {
                OkDialog dialog = new OkDialog(this, new OnClickOkListener() {
                    @Override
                    public void onClickOk() {

                    }
                });
                dialog.show();
                dialog.setMessage("无法在sd卡中创建目录/paintPad, \n请检查SDCard");
            }
        }
        return dirString;
    }

    private void enableUndoButton() {
        undoButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.undo_enable));
    }

    private void disableRedoButton() {
        redoButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.redo_disable));
    }


    //==================POPWINDOW==============

    private void showPenPopWindow(View parent) {
        LayoutInflater mLayoutInflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popView = mLayoutInflater.inflate(R.layout.pen_pop_window, null);
        PopupWindow popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popView.findViewById(R.id.smallpen).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.middlepen).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.bigpen).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.color_black).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.color_blue).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.color_green).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.color_red).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.color_white).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.color_yellow).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.pentype_blur).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.pentype_emboss).setOnClickListener(new PenItemOnClickListener());
        popView.findViewById(R.id.pentype_plain).setOnClickListener(new PenItemOnClickListener());

        popupWindow.showAsDropDown(parent, parent.getWidth(), -parent.getHeight());
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
    }

    public class PenItemOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.smallpen:
                    mPaintView.setPenSize(PEN_SIZE.SIZE_1);
                    break;
                case R.id.middlepen:
                    mPaintView.setPenSize(PEN_SIZE.SIZE_3);
                    break;
                case R.id.bigpen:
                    mPaintView.setPenSize(PEN_SIZE.SIZE_5);
                    break;
                case R.id.color_black:
                    mPaintView.setPenColor(Color.BLACK);
                    break;
                case R.id.color_blue:
                    mPaintView.setPenColor(Color.BLUE);
                    break;
                case R.id.color_green:
                    mPaintView.setPenColor(Color.GREEN);
                    break;
                case R.id.color_red:
                    mPaintView.setPenColor(Color.RED);
                    break;
                case R.id.color_yellow:
                    mPaintView.setPenColor(Color.YELLOW);
                    break;
                case R.id.color_white:
                    mPaintView.setPenColor(Color.WHITE);
                    break;
                case R.id.pentype_blur:
                    mPaintView.setPenType(PEN_TYPE.BLUR);
                    break;
                case R.id.pentype_emboss:
                    mPaintView.setPenType(PEN_TYPE.EMBOSS);
                    break;
                case R.id.pentype_plain:
                    mPaintView.setPenType(PEN_TYPE.PLAIN_PEN);
                    break;
                default:
                    break;
            }
        }
    }


    private void showEraserPopWindow(View parent) {
        LayoutInflater mLayoutInflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popView = mLayoutInflater.inflate(R.layout.eraser_pop_window, null);
        PopupWindow popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popView.findViewById(R.id.smalleraser).setOnClickListener(new EraserItemOnClickListener());
        popView.findViewById(R.id.middleeraser).setOnClickListener(new EraserItemOnClickListener());
        popView.findViewById(R.id.bigeraser).setOnClickListener(new EraserItemOnClickListener());
        popView.findViewById(R.id.blockeraser).setOnClickListener(new EraserItemOnClickListener());
        popView.findViewById(R.id.alleraser).setOnClickListener(new EraserItemOnClickListener());

        //设置popwindow显示位置
        popupWindow.showAsDropDown(parent, parent.getWidth(), -parent.getHeight());
        //获取popwindow焦点
        popupWindow.setFocusable(false);
        //设置popwindow如果点击外面区域，便关闭。
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
    }

    public class EraserItemOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.smalleraser:
                    mPaintView.setEraserSize(ERASER_SIZE.SIZE_3);
                    mPaintView.setEraserType(ERASER_TYPE.LINE);
                    break;
                case R.id.middleeraser:
                    mPaintView.setEraserSize(ERASER_SIZE.SIZE_4);
                    mPaintView.setEraserType(ERASER_TYPE.LINE);
                    break;
                case R.id.bigeraser:
                    mPaintView.setEraserSize(ERASER_SIZE.SIZE_5);
                    mPaintView.setEraserType(ERASER_TYPE.LINE);
                    break;
                case R.id.blockeraser:
                    mPaintView.setEraserType(ERASER_TYPE.BLOCK);
                    break;
                case R.id.alleraser:
                    mPaintView.clearAll();
                    upDateUndoRedo();
                    break;
                default:
                    break;
            }
        }
    }
}
