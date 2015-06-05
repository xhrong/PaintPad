package com.likebamboo.shapes;

import android.graphics.*;
import com.likebamboo.activity.MyApplication;
import com.likebamboo.interfaces.Shapable;
import com.likebamboo.paintpad.R;

/**
 * Created by xhrong on 2015/5/29.
 */
public class Image extends ShapeAbstract {

    public Image(Shapable paintTool) {
        super(paintTool);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (canvas==null || paint == null) {
            return;
        }
        super.draw(canvas, paint);

        Bitmap bitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.icon) ;
        canvas.drawBitmap(bitmap, new Rect((int)x1,(int)y1,(int)x2,(int)y2),new Rect(), paint);
    }

    @Override
    public String toString() {
        return " image";
    }
}
