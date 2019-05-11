package com.example.smartbj.basepage;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.smartbj.activity.MainActivity;

/**
 * Created by long on 2018/11/27.
 */

public class HomeBaseTagPager extends BaseTagPage {


    public HomeBaseTagPager(MainActivity context) {
        super(context);
    }

    @Override
    public void initData() {
        //屏蔽左上菜单按钮
        ib_menu.setVisibility(View.GONE);

        //设置page的标题
        tv_title.setText("首页");
        //要展示的内容
        TextView tv = new TextView(mainActivity);
        tv.setText("首页的内容");
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER);

        //替换掉的白纸
        fl_content.addView(tv); //添加自的内容

        super.initData();
    }
}
