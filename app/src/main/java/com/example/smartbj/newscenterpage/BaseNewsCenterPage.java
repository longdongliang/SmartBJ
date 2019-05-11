package com.example.smartbj.newscenterpage;

import android.view.View;

import com.example.smartbj.activity.MainActivity;

public abstract class BaseNewsCenterPage {

    protected  MainActivity mainActivity;
    protected View root;//根布局

    public BaseNewsCenterPage(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        root = initView();
        initEvent();
    }

    //子类覆盖此方法完成事件的处理
    public void initEvent(){

    }

    //子类覆盖此方法来显示自定义的View
    public abstract View initView();

    public View getRoot(){
        return  root;
    }

    public void initData(){

    }
}
