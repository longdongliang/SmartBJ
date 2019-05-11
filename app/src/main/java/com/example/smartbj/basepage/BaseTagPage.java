package com.example.smartbj.basepage;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.smartbj.R;
import com.example.smartbj.activity.MainActivity;

/**
 * Created by long on 2018/11/26.
 */

public class BaseTagPage {

    protected MainActivity mainActivity;
    protected View root;
    protected ImageButton ib_menu; //
    protected TextView tv_title;
    protected FrameLayout fl_content;

    public BaseTagPage(MainActivity context) {
        this.mainActivity = context;
        initView();//初始化布局
        //initData(); //要显示时在初始化
        initEvent();

    }

    public void initEvent() {
        //给菜单按钮添加点击事件
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开关闭左侧菜单
                mainActivity.getSlidingMenu().toggle();

            }
        });
    }

    public void initData(){
        
    }

    public void switchPage(int position){

    }

    public void initView() {
        //界面的根布局
         root = View.inflate(mainActivity, R.layout.fragment_content_base_content,null);
        ib_menu = (ImageButton) root.findViewById(R.id.ib_base_content_menu);
        tv_title = (TextView) root.findViewById(R.id.tv_base_content_title);
        fl_content = (FrameLayout) root.findViewById(R.id.fl_base_content_tag);


    }

    public View getRoot(){
        return root;
    }
}
