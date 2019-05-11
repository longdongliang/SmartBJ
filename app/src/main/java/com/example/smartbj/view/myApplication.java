package com.example.smartbj.view;

import android.app.Application;

import org.xutils.x;

/**
 * Created by long on 2018/11/26.
 */

public class myApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
