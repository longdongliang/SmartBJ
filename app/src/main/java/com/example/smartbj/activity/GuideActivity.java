package com.example.smartbj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.smartbj.R;
import com.example.smartbj.utils.DensityUtil;
import com.example.smartbj.utils.MyConstants;
import com.example.smartbj.utils.SpTools;

import java.util.ArrayList;
import java.util.List;

/**设置向导界面
 * Created by long on 2018/8/16.
 */

public class GuideActivity extends Activity {
   protected  ViewPager vp_guids;
   protected  LinearLayout ll_points;
    protected View v_redpoint;
    protected Button bt_startExp;
    protected  List<ImageView> guids;
    protected MyAdapter adapter;
    protected  int disPoints;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView(); //初始化界面

        initData(); //初始化数据

        initEvent();//初始化组件事件



    }

    protected void initEvent() {

        //监听布局完成,触发的结果
        v_redpoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                v_redpoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                //计算点以点距离
               disPoints = ll_points.getChildAt(1).getLeft() - ll_points.getChildAt(0).getLeft();
            }
        });

        //
        bt_startExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存设置的状态
                SpTools.setBoolean(getApplicationContext(), MyConstants.ISSSETUP,true);

                //进入主界面
                Intent main =new Intent(GuideActivity.this, MainActivity.class);
                startActivity(main);
                //
                finish();

            }
        });

        //给ViewPage添加页码改变事件
        vp_guids.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                //计算红点的左边距
                float leftMargin = disPoints *(position + positionOffset);

                //设置红点的在左边距
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v_redpoint.getLayoutParams();
                layoutParams.leftMargin = Math.round(leftMargin);

                //重新设置布局
                v_redpoint.setLayoutParams(layoutParams);

            }

            @Override
            public void onPageSelected(int position) {
                //当前页面
               if (position == (guids.size() -1))
               {
                   bt_startExp.setVisibility(View.VISIBLE);

               }else{
                   bt_startExp.setVisibility(View.GONE);
               }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    protected void initData() {
        //图片的数据
        int[] pics = new int[]{R.drawable.guide_1,R.drawable.guide_2,R.drawable.guide_3};

        //定义Viewpager使用的容器
        guids = new ArrayList<ImageView>();

        //
        for(int i=0;i<pics.length;i++)
        {
            ImageView iv_temp = new ImageView(getApplicationContext());
            iv_temp.setBackgroundResource(pics[i]);

            guids.add(iv_temp);

            //
            View v_point =new View(getApplicationContext());
            v_point.setBackgroundResource(R.drawable.gray_point);

            int dip =10;
            //
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(getApplicationContext(),dip),DensityUtil.dip2px(getApplicationContext(),dip)); //px no dp
            if(i !=0 )params.leftMargin = 20; //px 过虑第一个点
            v_point.setLayoutParams(params);

            //

            //
            ll_points.addView(v_point);



        }
        //适配器
         adapter = new MyAdapter();

        //设置适配器
        vp_guids.setAdapter(adapter);



    }

    protected  class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return guids.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            //super.destroyItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //获取View
            View child = guids.get(position);
            //添加View
            container.addView(child);
            return child;
        }
    }

    protected void initView() {

        setContentView(R.layout.activity_guide);

        //ViewPage组件
        vp_guids = (ViewPager) findViewById(R.id.vp_guide_pages);
        //动态加点容器
         ll_points = (LinearLayout) findViewById(R.id.ll_guide_points);

        //红点
         v_redpoint = findViewById(R.id.v_guide_redpoint);
        //开始体验按钮
        bt_startExp = (Button) findViewById(R.id.bt_guidde_startexp);

        bt_startExp.setVisibility(View.GONE); //不显示按钮

    }
}
