package com.example.smartbj.basepage;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartbj.activity.MainActivity;
import com.example.smartbj.domain.NewsCenterData;
import com.example.smartbj.newscenterpage.BaseNewsCenterPage;
import com.example.smartbj.newscenterpage.InteractBaseNewsCeterPage;
import com.example.smartbj.newscenterpage.NewsBaseNewsCeterPage;
import com.example.smartbj.newscenterpage.PhotosBaseNewsCeterPage;
import com.example.smartbj.newscenterpage.TopicBaseNewsCeterPage;
import com.example.smartbj.utils.MyConstants;
import com.example.smartbj.utils.SpTools;
import com.example.smartbj.view.LeftMenuFragment;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by long on 2018/11/27.
 */

public class NewCenterBaseTagPager extends BaseTagPage {

    private NewsCenterData newsCenterData;
    private Gson gson;

    //新闻中心要显示的四个页面
    private List<BaseNewsCenterPage>  newsCenterPages = new ArrayList<BaseNewsCenterPage>();

    public NewCenterBaseTagPager(MainActivity context) {
        super(context);
    }

    @Override
    public void initData() {

        //１.获取本地数据
        String jsonCache = SpTools.getString(mainActivity,MyConstants.NEWSCENTERURL,"");
        if (!TextUtils.isEmpty(jsonCache))
        {
            //有本地数据，从本地取数据显示
            parseData(jsonCache);
        }

        //２.获取网络数据
        RequestParams params = new RequestParams(MyConstants.NEWSCENTERURL);
        //params.setSslSocketFactory(...);//ssl
        params.addQueryStringParameter("wd","xUtils");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //Toast.makeText(x.app(), result, Toast.LENGTH_SHORT).show();
                Log.i("aaa",result);

                //保存到本地一份
                SpTools.setString(mainActivity,MyConstants.NEWSCENTERURL,result);
                //解析数据
                parseData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), "网络数据请求失败T_T", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                Toast.makeText(x.app(), "网络数据请求完成", Toast.LENGTH_SHORT).show();
            }
        });

//        //设置page的标题
//        tv_title.setText("新闻中心");
//        //要展示的内容
//        TextView tv = new TextView(mainActivity);
//        tv.setText("新闻中心的内容");
//        tv.setTextSize(25);
//        tv.setGravity(Gravity.CENTER);
//
//        //替换掉的白纸
//        fl_content.addView(tv); //添加自的内容

        super.initData();
    }

    //解析Josn数据
    protected void parseData(String jsonData){
        //google的json解析器
       if (gson == null) gson = new Gson();

        newsCenterData = gson.fromJson(jsonData,NewsCenterData.class);
        Log.i("bbb",newsCenterData.data.get(0).children.get(0).title);

        mainActivity.getLeftMenuFragment().setLeftMenuData(newsCenterData.data);

        //设置左侧菜单的监听回调
        mainActivity.getLeftMenuFragment().setOnSwitchPageListener(new LeftMenuFragment.OnSwitchPageListener() {
            @Override
            public void switchPage(int selectionIndex) {
                NewCenterBaseTagPager.this.switchPage(selectionIndex);
            }
        });

        //读取的数据封装到界面容器中，通过左侧菜单点击，显示不同界面
        //根据服务的数据　创建四个页面，按顺序
        for (NewsCenterData.NewsData newsData:newsCenterData.data){
            BaseNewsCenterPage newsPage=null;
            //遍历四个新闻中心页面
            switch (newsData.type){
                case 1: //新闻页面
                    newsPage = new NewsBaseNewsCeterPage(mainActivity,newsCenterData.data.get(0).children);
                    break;
                case 10: //专题页面
                    newsPage = new TopicBaseNewsCeterPage(mainActivity);
                    break;
                case 2: //组图页面
                    newsPage = new PhotosBaseNewsCeterPage(mainActivity);
                    break;
                case 3: //互动页面
                    newsPage = new InteractBaseNewsCeterPage(mainActivity);
                    break;

                    default:
                        break;
            }
            //添加新闻中心页面到容器中
            newsCenterPages.add(newsPage);
        }
        //控制四个页面的显示，默认选择第一个新闻页面
        switchPage(0);

    }

    //根据位置，动态显示不同的新闻中心页面
    public void switchPage(int position){
        BaseNewsCenterPage baseNewsCenterPage = newsCenterPages.get(position);

        //设置page的标题
        tv_title.setText(newsCenterData.data.get(position).title);

        //移除掉原来画的内容
        fl_content.removeAllViews();

        //初始化数据
        baseNewsCenterPage.initData();

        //替换掉白纸
        fl_content.addView(baseNewsCenterPage.getRoot());


    }
}
