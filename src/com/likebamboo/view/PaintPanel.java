package com.likebamboo.view;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by xhrong on 2015/5/29.
 */
public class PaintPanel extends FrameLayout {
    public PaintPanel(Context context) {
        super(context);
    }


    public void addImage(){
        ImageView imageView=new ImageView(this.getContext());
    }
}
