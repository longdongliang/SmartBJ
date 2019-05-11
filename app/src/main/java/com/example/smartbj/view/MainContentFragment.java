package com.example.smartbj.view;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.widget.ViewUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.smartbj.BuildConfig;
import com.example.smartbj.R;
import com.example.smartbj.basepage.BaseTagPage;
import com.example.smartbj.basepage.GovAffairsBaseTagPager;
import com.example.smartbj.basepage.HomeBaseTagPager;
import com.example.smartbj.basepage.NewCenterBaseTagPager;
import com.example.smartbj.basepage.SettingCenterBaseTagPager;
import com.example.smartbj.basepage.SmartServiceBaseTagPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**y主界面的Fragment
 * Created by long on 2018/8/23.
 */

public class MainContentFragment extends BaseFragment {

    @ViewInject(R.id.vp_main_content_pages)
    protected ViewPager viewPager;

    @ViewInject(R.id.rg_content_radios)
    protected RadioGroup rg_radios;

    private List<BaseTagPage> pages = new ArrayList<BaseTagPage>();

    private int selectindex; //设置当前选择页面编号

    @Override
    public void initEvent() {
        //添加自已的事件
        rg_radios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //五个单选按钮
                switch(checkedId){
                    case R.id.rb_main_content_home: //主界面
                        selectindex = 0;

                        break;
                    case R.id.rb_main_content_newscenter: //新闻中心界
                        selectindex = 1;

                        break;
                    case R.id.rb_main_content_smartservice: //智慧服务界面
                        selectindex = 2;

                        break;
                    case R.id.rb_main_content_govaffairs: //政务中心界面
                        selectindex = 3;

                        break;
                    case R.id.rb_main_content_settingcenter: //设备界面
                        selectindex = 4;

                        break;

                        default:
                            break;
                }

                switchPage();

            }
            
        });
        super.initEvent();
    }

    //左侧菜单点击，让主界面切换不同的页面
    public void leftMenuClickSwitchPage(int subSelectionIndex){
       BaseTagPage baseTagPage =  pages.get(selectindex);
        baseTagPage.switchPage(subSelectionIndex);
    }

    //设置选中页面
    private void switchPage() {
       // BaseTagPage currentPage = pages.get(selectindex);
        viewPager.setCurrentItem(selectindex);

        //如果是第一个或者最后一个 不让械侧菜单滑动出来
        if(selectindex == 0 || selectindex == pages.size() - 1){
            //不让左侧菜单滑动出来
            mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }else{
            //可以滑动左侧菜单
            mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }
    }

    @Override
    public View initView() {
        View root = View.inflate(mainActivity, R.layout.fragment_content_view,null);

        //xutils动态注入view

        x.view().inject(this,root);
        return root;
    }

    @Override
    public void initData() {
        //首页
        pages.add(new HomeBaseTagPager(mainActivity));
        //新闻中心
        pages.add(new NewCenterBaseTagPager(mainActivity));
        //智慧服务
        pages.add(new SmartServiceBaseTagPager(mainActivity));
        //政务中心
        pages.add(new GovAffairsBaseTagPager(mainActivity));
        //设置
        pages.add(new SettingCenterBaseTagPager(mainActivity));

        MyAdapter adapter = new MyAdapter();
        viewPager.setAdapter(adapter);

        //默认首页
        switchPage();

        //第一个按钮被选中
        rg_radios.check(R.id.rb_main_content_home);

        //super.initData();
    }

    private class MyAdapter extends PagerAdapter
    {

        @Override
        public int getCount() {
            Log.i("==","========pages.size========="+pages.size()+"=============");
            return pages.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseTagPage baseTagPage = pages.get(position);

            View root = baseTagPage.getRoot();
            Log.d("===============tag",String.valueOf(position));
            container.addView(root);

            //加载数据
            baseTagPage.initData();
            return root;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           container.removeView((View) object);
        }
    }
}
