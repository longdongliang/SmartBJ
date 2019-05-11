package com.example.smartbj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;


import com.example.smartbj.R;
import com.example.smartbj.utils.MyConstants;
import com.example.smartbj.utils.SpTools;

/**智慧北京的splash界面
 * Created by long on 2018/8/16.
 */

public class SplashActivity extends Activity {

    protected ImageView iv_mainview;

    protected  AnimationSet as;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();//初始化界面

        startAnimations();//开始播放动动画

        ininEvent();//初始化事件

    }

    protected void ininEvent() {

        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //监听动画播完
                if(SpTools.getBoolean(getApplicationContext(), MyConstants.ISSSETUP,false))
                {
                    //设置过,进主界面

                    Intent intent =new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    //没设置过,进入向导界面
                    Intent intent =new Intent(SplashActivity.this,GuideActivity.class);
                    startActivity(intent);
                    finish();

                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    protected void startAnimations() {
        //false代表动画集中每种动画都采用各自的插入器,
         as = new AnimationSet(false);

        //旋转动,锚点
        RotateAnimation ra =new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        //时间
        ra.setDuration(2000);
        ra.setFillAfter(true); //停留界面

        //添加到动画集
        as.addAnimation(ra);

        //渐变动画
        AlphaAnimation aa = new AlphaAnimation(0,1);
        //播放时间
        aa.setDuration(2000);
        aa.setFillAfter(true);//停留界面

        //添加
        as.addAnimation(aa);

        //
        ScaleAnimation sa = new ScaleAnimation(0,1,0,1,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

        sa.setDuration(2000);
        sa.setFillAfter(true);//停留界面

        //添加
        as.addAnimation(sa);

        //播放动画
        iv_mainview.startAnimation(as);

        //

        //监听播放完的事件

        //

    }

    ////初始化界面
    protected void initView() {

        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置主界面
        setContentView(R.layout.activity_splash);

        //
        iv_mainview = (ImageView) findViewById(R.id.iv_spplash_mainview);



    }
}
