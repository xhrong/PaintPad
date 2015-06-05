
package com.likebamboo.utils;

import android.graphics.Color;
import android.os.Environment;

public class PaintConstants {

    private PaintConstants() {

    }

    public static final class ERASER_SIZE {
        public static final int SIZE_1 = 5;

        public static final int SIZE_2 = 10;

        public static final int SIZE_3 = 15;

        public static final int SIZE_4 = 30;

        public static final int SIZE_5 = 50;
    }

    public static final class ERASER_TYPE {

        public static final int LINE = 0;
        public static final int BLOCK = 1;
    }

    public static final class PEN_SIZE {
        public static final int SIZE_1 = 5;

        public static final int SIZE_2 = 10;

        public static final int SIZE_3 = 15;

        public static final int SIZE_4 = 20;

        public static final int SIZE_5 = 30;
    }

    public static final class SHAPE_TYPE {
        /**
         * 曲线
         */
        public static final int CURV = 1;

        /**
         * 直线
         */
        public static final int LINE = 2;

        /**
         * 矩形
         */
        public static final int RECT = 3;

        /**
         * 园
         */
        public static final int CIRCLE = 4;

        /**
         * 椭圆
         */
        public static final int OVAL = 5;

        /**
         * 正方形
         */
        public static final int SQUARE = 6;

        /**
         * 五角星
         */
        public static final int STAR = 7;
    }

    public static final class PATH {
        public static final String SAVE_PATH = Environment.getExternalStorageDirectory().getPath()
                + "/paintPad";
    }

    public static final class PEN_TYPE {
        /**
         * 铅笔
         */
        public static final int PLAIN_PEN = 1;

        /**
         * 橡皮擦
         */
     //   public static final int ERASER = 2;

        /**
         * 模糊
         */
        public static final int BLUR = 3;

        /**
         * 浮雕
         */
        public static final int EMBOSS = 4;
    }

    public static final class DEFAULT {
        public static final int PEN_COLOR = Color.BLACK;

        public static final int BACKGROUND_COLOR = Color.WHITE;
    }

    public static final int UNDO_STACK_SIZE = 20;

    public static final int COLOR_VIEW_SIZE = 80;

    public static final int GET_IMAGE_ACTIVITY = 1;

    public static final int GET_CAMERA_ACTIVITY=2;


}