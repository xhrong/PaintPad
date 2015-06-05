package com.likebamboo.activity;

import android.app.Application;
import android.content.Context;

/**
 * Created by xhrong on 2015/5/29.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        //获取Context
        context = getApplicationContext();
    }

    //返回
    public static Context getContext(){
        return context;
    }

}
