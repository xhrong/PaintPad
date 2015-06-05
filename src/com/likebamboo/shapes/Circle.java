package com.likebamboo.shapes;

import android.graphics.*;
import com.likebamboo.activity.MyApplication;
import com.likebamboo.interfaces.Shapable;
import com.likebamboo.paintpad.R;

public class Circle extends ShapeAbstract {
    Bitmap bitmap;

    public Circle(Shapable paintTool) {
        super(paintTool);
        bitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.about);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
//		if (canvas==null || paint == null) {
//			return;
//		}
//		super.draw(canvas, paint);
//		float cx = (x1 + x2)/2;
//		float cy = (y1+y2)/2;
//		float radius = (float) Math.sqrt(Math.pow(x1 - x2, 2)
//				+ Math.pow(y1 - y2, 2))/2;
//		canvas.drawCircle(cx, cy, radius, paint);

        if (canvas == null || paint == null) {
            return;
        }
        super.draw(canvas, paint);


        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect((int) x1, (int) y1, (int) x2, (int) y2), null);
    }

    @Override
    public String toString() {
        return " circle";
    }
}
