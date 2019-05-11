package com.example.smartbj.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class InterceptScrollViewPager extends ViewPager {
   private float downX;
   private float downY;

    public InterceptScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public InterceptScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //true 申请父控件不拦截我的 touch事件 false 默认父类先拦截
       // getParent().requestDisallowInterceptTouchEvent(true);

        //事件完全由自己处理
        //
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN: //按下
                getParent().requestDisallowInterceptTouchEvent(true);
                //记录下点的位置
                 downX = ev.getX();
                 downY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE: //移动
                //获取移动的位置坐标
                float moveX = ev.getX();
                float moveY = ev.getY();

                float dx = moveX - downX;
                float dy = moveY - downY;
                //横向移动
                if(Math.abs(dx) > Math.abs(dy)){
                    //如果在第一个页面，并且是从左往右滑动，让父控件拦截
                    if (getCurrentItem() == 0 && dx > 0)
                    {
                        //由父控件处理事件
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else if(getCurrentItem() == getAdapter().getCount() - 1 && dx < 0){//如果在最后一个页面，并且是从右往左滑，父控件拦截
                        //由父控件处理事件
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else{
                        //否则都不让父类拦截
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                }else{
                    //让父控件拦截
                    getParent().requestDisallowInterceptTouchEvent(false);
                }


                break;

                default:
                    break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
