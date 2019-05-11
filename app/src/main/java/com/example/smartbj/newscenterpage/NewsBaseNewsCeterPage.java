package com.example.smartbj.newscenterpage;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ViewUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartbj.R;
import com.example.smartbj.activity.MainActivity;
import com.example.smartbj.domain.NewsCenterData;
import com.example.smartbj.newscenterpage.BaseNewsCenterPage;
import com.example.smartbj.newstpipage.TPINewsNewsCenterPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class NewsBaseNewsCeterPage extends BaseNewsCenterPage {

    @ViewInject(R.id.newcenter_vp)
    private ViewPager vp_newscenter;

    @ViewInject(R.id.newcenter_tpi)
    private TabPageIndicator tpi_newscenter;

    @Event(value = R.id.newcenter_ib_nextpage,type = View.OnClickListener.class)
    private  void next(View v){
        //切换到下个页面
        vp_newscenter.setCurrentItem(vp_newscenter.getCurrentItem() + 1);
    }

    private List<NewsCenterData.NewsData.ViewTagData> viewTagDatas = new ArrayList<NewsCenterData.NewsData.ViewTagData>(); //面签的数据

    public NewsBaseNewsCeterPage(MainActivity mainActivity, List<NewsCenterData.NewsData.ViewTagData> children) {
        super(mainActivity);

        this.viewTagDatas = children;
    }

    @Override
    public void initEvent() {
        //添加自己的事件

        //给Viewpager添加页面切的监听事件，当页面位于第一个可以滑动出左侧菜单，否则不滑动
        tpi_newscenter.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //当页面位于第一个可以滑动出左侧菜单
                if (i == 0){
                    //第一个，可以滑出左则菜单
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                }else{
                    //第二个以后不可以滑出左则菜单
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        super.initEvent();
    }

    @Override
    public View initView() {

        //要展示的内容
        View newsCenterRoot = View.inflate(mainActivity
        , R.layout.newscenterpage_content,null);

        //xutils工具注入组件
        x.view().inject(this
        ,newsCenterRoot);

        return newsCenterRoot;

    }

    @Override
    public void initData() {
        //设置数据
        MyAdapter adapter = new MyAdapter();
        //设置ViewPager的造适配器
        vp_newscenter.setAdapter(adapter);
        //把ViewPager和Tabpagerindicator关联
        tpi_newscenter.setViewPager(vp_newscenter);

        super.initData();
    }

    /**
     * //页签对应
     */

    private  class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return viewTagDatas.size(); //获取数据个数
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return viewTagDatas.get(position).title;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //要展示的内容
            TPINewsNewsCenterPager tpiPager = new TPINewsNewsCenterPager(mainActivity,viewTagDatas.get(position));
            View rootView = tpiPager.getRootView();
            container.addView(rootView);

            return  rootView;
//            TextView tv = new TextView(mainActivity);
//            tv.setText(viewTagDatas.get(position).title);
//            tv.setTextSize(25);
//            tv.setGravity(Gravity.CENTER);
//
//            container.addView(tv);
//
//            return tv;
        }
    }


}
