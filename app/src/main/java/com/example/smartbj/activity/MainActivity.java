package com.example.smartbj.activity;

import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.example.smartbj.BuildConfig;
import com.example.smartbj.R;
import com.example.smartbj.view.LeftMenuFragment;
import com.example.smartbj.view.MainContentFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import org.xutils.x;



public class MainActivity extends SlidingFragmentActivity {

    protected static final String LEFT_MUNE_TAG = "LEFT_MUNE_TAG";
    protected static final String MAIN_MUNE_TAG = "MAIN_MUNE_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView(); //
        initData();//

        x.view().inject(this);

    }

    //返回左侧菜单的fragment
    public LeftMenuFragment getLeftMenuFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        LeftMenuFragment leftFragment = (LeftMenuFragment) fragmentManager.findFragmentByTag(LEFT_MUNE_TAG);

        return leftFragment;
    }

    //返回主界面的fragment
    public MainContentFragment getMainMenuFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainContentFragment mainFragment = (MainContentFragment) fragmentManager.findFragmentByTag(MAIN_MUNE_TAG);

        return mainFragment;
    }

    protected void initData() {
       FragmentManager fragmentManager = getSupportFragmentManager();
        //1.获取事物
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //2.完成替换

        //完成左则菜单界面的替换
        transaction.replace(R.id.fl_left_menu,new LeftMenuFragment(),LEFT_MUNE_TAG);
        //完成主界面的替换
        transaction.replace(R.id.fl_main_menu,new MainContentFragment(),MAIN_MUNE_TAG);
        //3.提交事物
        transaction.commit();
    }

    protected void initView() {
       //主界面 setContentView(R.layout.activity_main);
        setContentView(R.layout.fragment_content_tag);

        //设置左侧菜单界面
        setBehindContentView(R.layout.fragment_left);

        //设置滑动模式
        SlidingMenu sm = getSlidingMenu();
        sm.setMode(SlidingMenu.LEFT);//设置左侧可以滑动

        //设置滑动位置
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        //设置主界面左侧滑动后剩余空间位置
        sm.setBehindOffset(800);

    }
}
