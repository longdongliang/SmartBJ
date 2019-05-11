package com.example.smartbj.newstpipage;


//新闻中心页签对应的页面

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartbj.R;
import com.example.smartbj.activity.MainActivity;
import com.example.smartbj.domain.NewsCenterData;
import com.example.smartbj.domain.TPINewsData;
import com.example.smartbj.utils.DensityUtil;
import com.example.smartbj.utils.MyConstants;
import com.example.smartbj.utils.SpTools;
import com.example.smartbj.view.RefreshListView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * The type Tpi news news center pager.
 */
public class TPINewsNewsCenterPager {
    //所有组件
    @ViewInject(R.id.vp_tpi_news_lunbo_pic)
    private ViewPager vp_lunbo; //轮播图的显示组件

    @ViewInject(R.id.tv_tpi_news_pic_desc)
    private TextView tv_pic_desc;//图片的描述信息

    @ViewInject(R.id.ll_tpi_news_pic_points)
    private LinearLayout ll_points; //轮播图的每张图片对应的点组合

    @ViewInject(R.id.lv_tpi_news_listnews)
    private RefreshListView lv_listnews;//显示列表新闻的组件

    //数据相关
    private MainActivity mainActivity;
    private View root;
    private NewsCenterData.NewsData.ViewTagData viewTagData;//页签对应的数据
    private Gson gson;
    //轮播图的数据
    private  List<TPINewsData.TPINewsData_Data.TPINewsData_Data_LunBoData> lunboDatas = new ArrayList<TPINewsData.TPINewsData_Data.TPINewsData_Data_LunBoData>();
   //轮播图的适配器
    private  LunBoAdapter lunboAdapter;

    private  ImageOptions options;

    private int picSelectIndex;

    private  Handler handler;

    private LunBoTask lunBoTask;

    private ListNewsAdapter listNewsAdapter;

    private boolean isFresh = false;

    private String loadingMoreDatasUrl;//加载更多数据的Url

    private String loadingDataUrl;

    //新闻列表数据
    private List<TPINewsData.TPINewsData_Data.TPINewsData_Data_ListNewsData> listNews = new ArrayList<TPINewsData.TPINewsData_Data.TPINewsData_Data_ListNewsData>();

    public  TPINewsNewsCenterPager(MainActivity mainActivity, NewsCenterData.NewsData.ViewTagData viewTagData){
        this.mainActivity = mainActivity;
        this.viewTagData = viewTagData;
        gson = new Gson();

        lunBoTask = new LunBoTask();

        // 设置加载图片的参数
         options = new ImageOptions.Builder()
        // 是否忽略GIF格式的图片
        .setIgnoreGif(false)
        // 图片缩放模式
       .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
       // 下载中显示的图片
         .setLoadingDrawableId(R.drawable.home_scroll_default)
        // 下载失败显示的图片
         .setFailureDrawableId(R.drawable.xsearch_loading)
        // 得到ImageOptions对象
         .build();

        initView();//初始化界面　
        initData();//初始数据
        initEvent();//初始化事件

    }

    private  void initView(){
        //页签对应页面的根布局
        root = View.inflate(mainActivity, R.layout.tpi_news_centent,null);

        //xutils工具注入组件
        x.view().inject(this,root);

        //
         View lunBoPic = View.inflate(mainActivity,R.layout.tpi_news_lunbopic,null);
        x.view().inject(this,lunBoPic);

         lv_listnews.setIsRefreshHead(true);//启动下拉刷新
         //把轮播图加listView中
       // lv_listnews.addHeaderView(lunBoPic);
        lv_listnews.addHeaderView(lunBoPic);
    }

    private void initData(){
        //轮播图的适配器
        lunboAdapter = new LunBoAdapter();
        //给轮播图
        vp_lunbo.setAdapter(lunboAdapter);

        //新闻列表的适配器
        listNewsAdapter = new ListNewsAdapter();
        //设置
        lv_listnews.setAdapter(listNewsAdapter);


        //轮播图的数据
        //新闻列表的数据
        //从本地取数据
        String jsonCache = SpTools.getString(mainActivity,loadingDataUrl,"");
        if (!TextUtils.isEmpty(jsonCache))
        {
            //有数据，解析处理
            TPINewsData newsData = parseJson(jsonCache);
            //处理数据
            processData(newsData);
        }
        loadingDataUrl = MyConstants.SERVERURL + viewTagData.url;
        getDataFromNet(loadingDataUrl,false); //从网络获取数据

    }

    /**
     * @param newsData
     */
    private void processData(TPINewsData newsData){
        //完成数据的处理

        //1.设置轮播图的数据
        setLunBoData(newsData);

        //2.轮播图对应的点处理
        initPoints(); //初始化轮播图的点

        //3.设置图片描述和点的效果
        setPicDescAndPointSelect(picSelectIndex);

        //4.开始轮播图的处理
        lunBoTask.startLunbo();

        //5.新列表的数据
        setListViewNews(newsData);

    }

    /**
     * 设置新闻列表的数据
     * @param newsData
     */
    private void setListViewNews(TPINewsData newsData) {
        listNews = newsData.data.news;

        //更新界面
        listNewsAdapter.notifyDataSetChanged();

    }

    private void lunboProcess() {
        if (handler == null){
            handler = new Handler();
        }
        //清空原来所有任务
        handler.removeCallbacksAndMessages(null);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //任务
                //控制轮播图的显示
                vp_lunbo.setCurrentItem((vp_lunbo.getCurrentItem() + 1) % vp_lunbo.getAdapter().getCount());

                //再发一次
                handler.postDelayed(this,3000);
            }
        },1000);



    }

    private class LunBoTask extends  Handler implements Runnable{

        public void stopLunbo(){
            //移除当前所有的任务
            removeCallbacksAndMessages(null);
        }
        public void startLunbo(){
            postDelayed(this,3000);
        }

        @Override
        public void run() {
            //控制轮播图的显示
            vp_lunbo.setCurrentItem((vp_lunbo.getCurrentItem() + 1) % vp_lunbo.getAdapter().getCount());
            postDelayed(this,3000);
        }
    }

    private void setPicDescAndPointSelect(int picSelectIndex) {
        if (picSelectIndex < 0 && picSelectIndex >lunboDatas.size() -1)
        {
            return; //防数组越界
        }
        //设置描述信息
        tv_pic_desc.setText(lunboDatas.get(picSelectIndex).title);

        //设置点是否选中
        for (int i = 0; i < lunboDatas.size();i++)
        {
            ll_points.getChildAt(i).setEnabled(i == picSelectIndex);
        }


    }

    private void initPoints() {
        //清空以前的存在的点
        ll_points.removeAllViews();

        //轮播图有几张 加几人点
        for (int i = 0;i < lunboDatas.size();i++)
        {
            View v_point = new View(mainActivity);
            //设置点的背景选择器
            v_point.setBackgroundResource(R.drawable.point_seletor);
            v_point.setEnabled(false);//默认的灰色点
            //点大小
            ViewGroup.LayoutParams params =  new LinearLayout.LayoutParams(DensityUtil.dip2px(mainActivity,5),DensityUtil.dip2px(mainActivity,5));
           //设置点和点间距
            ((LinearLayout.LayoutParams) params).leftMargin = DensityUtil.dip2px(mainActivity,10);
            //设置参数
            v_point.setLayoutParams(params);

            ll_points.addView(v_point);
        }
    }


    private void setLunBoData(TPINewsData newsData) {
        //获取轮播图数据
       lunboDatas =  newsData.data.topnews;

       //更新界面
        lunboAdapter.notifyDataSetChanged();


    }


    /**
     *
     */
    private class ListNewsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return listNews.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null){
                convertView = View.inflate(mainActivity,R.layout.tpi_news_listview_item,null);
                holder = new ViewHolder();
                holder.iv_icon = convertView.findViewById(R.id.iv_tpi_news_listview_item_icon);
                holder.iv_newspic = convertView.findViewById(R.id.iv_tpi_news_listview_item_pic);
                holder.tv_title = convertView.findViewById(R.id.tv_tpi_news_listview_item_title);
                holder.tv_time = convertView.findViewById(R.id.tv_tpi_news_listview_item_time);
                convertView.setTag(holder);

            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            //设置数据
            TPINewsData.TPINewsData_Data.TPINewsData_Data_ListNewsData tpiNewsData_data_listNewsData = listNews.get(position);

            //设置标题
            holder.tv_title.setText(tpiNewsData_data_listNewsData.title);

            //设置时间
            holder.tv_time.setText(tpiNewsData_data_listNewsData.pubdate);

            //设置图片
            //加载图片
            x.image().bind(holder.iv_newspic, tpiNewsData_data_listNewsData.listimage, options, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    LogUtil.e("listimage图下载成功");
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    LogUtil.e("listimage图下载出错，" + ex.getMessage());
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    LogUtil.e("listimage图下载取消");
                }

                @Override
                public void onFinished() {
                    LogUtil.e("listimage图下载完成");
                }
            });



            return convertView;
        }
    }

    private class ViewHolder{
        ImageView iv_newspic;
        TextView tv_title;
        TextView tv_time;
        ImageView iv_icon;

    }

    /**
     * 轮播图的适配器
     */
    private class LunBoAdapter extends PagerAdapter{

       private float downX;
       private float downY;
       private long downTime;

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView iv_lunbo_pic = new ImageView(mainActivity);
            iv_lunbo_pic.setScaleType(ImageView.ScaleType.FIT_XY);
            //给图片添加数据
            TPINewsData.TPINewsData_Data.TPINewsData_Data_LunBoData tpiNewsData_Data_LunBoData = lunboDatas.get(position);
            //图片的Url
            String topimageUrl = tpiNewsData_Data_LunBoData.topimage;
            //把Url的图片给iv_lunbo_pic
            //加载图片
            x.image().bind(iv_lunbo_pic, topimageUrl, options, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    LogUtil.e("轮播图下载成功");
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    LogUtil.e("轮播图下载出错，" + ex.getMessage());
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    LogUtil.e("轮播图下载取消");
                }

                @Override
                public void onFinished() {
                    LogUtil.e("轮播图下载完成");
                }
            });

            //给图片添加触摸事件
            iv_lunbo_pic.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN: //按下停止轮播

                            downX = event.getX();

                            downY = event.getY();

                            downTime = System.currentTimeMillis();

                            lunBoTask.stopLunbo();
                            break;
                        case MotionEvent.ACTION_UP: //松开
                            float upX = event.getX();
                            float upY = event.getY();

                            if(upX == downX && upY == downY ){
                                long upTime = System.currentTimeMillis();
                                if((upTime -downTime) < 500){
                                    lunboPicClick("被单击了...");

                            }
                            }
                            lunBoTask.startLunbo(); //开始轮播
                            break;
                        case MotionEvent.ACTION_CANCEL: //事件取消
                            lunBoTask.startLunbo(); //开始轮播
                            break;

                            default:
                                break;
                    }
                    return true;
                }

                private void lunboPicClick(Object data) {
                    //处理图片的点击事件
                    Log.i("处理图片的点击事件",data +"");

                }
            });
            container.addView(iv_lunbo_pic);

            return  iv_lunbo_pic;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return lunboDatas.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }
    }


    /**
     * @param jsonData
     * @return
     */
    private  TPINewsData parseJson(String jsonData){

        //解析json数据
       TPINewsData tipNewsData = gson.fromJson(jsonData, TPINewsData.class);
        Log.i("解析json数据:tip",tipNewsData.data.news.get(0).title);

        if (!TextUtils.isEmpty(tipNewsData.data.more)){
            loadingMoreDatasUrl = MyConstants.SERVERURL + tipNewsData.data.more;
        }else{
            loadingMoreDatasUrl = "";
        }
        return tipNewsData;

    }






    private void getDataFromNet(final String url,final boolean isLoadingMore) {
        //httpUtils
        //获取网络数据
        RequestParams params = new RequestParams(url);
        params.addQueryStringParameter("username","abc");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //请求数据成功
                String jsonData = result;

                //保存数据到本地
                SpTools.setString(mainActivity,url,jsonData);

                //解析数据
                TPINewsData newsData = parseJson(jsonData);

               // 判断是否加载更多数据
                if (isLoadingMore){

                    //原有的数据　+　新数据
                    listNews.addAll(newsData.data.news);
                    //更新界面
                    listNewsAdapter.notifyDataSetChanged();
                    Toast.makeText(mainActivity,"底部数据加载成功",Toast.LENGTH_LONG).show();
                }else {
                    //第一次刷新数据或刷新数据
                    //处理数据
                    processData(newsData);

                    if (isFresh)
                    {
                        SystemClock.sleep(2000);

                        Toast.makeText(mainActivity,"刷新数据成功",Toast.LENGTH_LONG).show();
                    }

                }
                lv_listnews.refreshStateFinish();


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //请求数据失败
                if (isFresh) {
                    lv_listnews.refreshStateFinish();
                    Toast.makeText(mainActivity, "刷新数据失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private  void initEvent(){

        lv_listnews.setOnRefreshDataListener(new RefreshListView.OnRefreshDataListener() {
            @Override
            public void refresdData() {
                isFresh = true;
                //刷新数据
                getDataFromNet(MyConstants.SERVERURL + viewTagData.url,false);
                //改变listview的状态

            }

            @Override
            public void loadingMore() {
                //判断是否有更多数据
                if (TextUtils.isEmpty(loadingMoreDatasUrl)){
                    Toast.makeText(mainActivity, "没有更多数据", Toast.LENGTH_LONG).show();

                    //关闭刷新数据的状态
                    lv_listnews.refreshStateFinish();
                }else{
                    //有数据
                    getDataFromNet(loadingMoreDatasUrl,true);
                }
            }
        });

        //给轮播图添加页面切换事件
        vp_lunbo.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                picSelectIndex = i;
                setPicDescAndPointSelect(picSelectIndex);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public View getRootView(){
        return root;
    }
}
