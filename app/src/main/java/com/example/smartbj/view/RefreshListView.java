package com.example.smartbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.smartbj.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义刷新头和加载数据尾的ListView
 */
public class RefreshListView extends ListView {

    private View foot;//
    private LinearLayout head;

    private float downY = -1;

    private final int PULL_DOWN = 1;//下拉刷新状态

    private  final int RELEASE_STATE = 2;//松开刷新状态

    private  final int REFRESHING  = 3; //正在刷新状态

    private int currentState = PULL_DOWN; //当前的状态

    private  LinearLayout ll_refresh_head_root;

    private int ll_refresh_head_root_Height;

    private int ll_refresh_foot_Height;

    private  View lunbotu;

    private int listViewOnScreanY; //listview在屏幕中的Ｙ轴坐标位置

    private OnRefreshDataListener listener; //

    private ImageView iv_arrow;
    private ProgressBar pb_loading;
    private TextView tv_time;
    private TextView tv_state;
    private RotateAnimation down_ra;
    private RotateAnimation up_ra;

    private boolean isFresh = false; //记录是否刷新数据

    private boolean isEnablePullRefresh; //默认为否
    private boolean isLoadingMore; //是事加载更多数据

    public RefreshListView(Context context) {
       // super(context);
        this(context,null);

    }

    private void initView() {
        initFoot();

        initHead();
    }


    /**
     * 覆盖此方法宛成自忆的事件处理
     * @param
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //需要我们的功能屏蔽掉父类的touch事件
        //下拉拖动，当listview显示一条数据
        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN: //按下　

                downY = ev.getY();//按下时Ｙ轴坐标
                break;
            case MotionEvent.ACTION_MOVE: //移动

                if (!isEnablePullRefresh){
                    //没有启用下拉刷新
                    Log.i("---没有启用下拉刷新----", "break");
                    break;
                }

                if (currentState == REFRESHING){
                    //正在刷新
                    Log.i("---正在刷新----", "break");
                    break;
                }
                if (downY == -1){ //按下时没有获取坐标
                    downY = ev.getY();
                }
                float moveY = ev.getY();//移动后的Ｙ轴坐标

                if (!isLunboFullShow()){
                    //轮播图没有完全显示，不执行本以下代自定义代码，
                    Log.i("---轮播图没有完全显示----", "break");
                    break;
                }

                //移动的位置间距
                float dy = moveY - downY;
                Log.i("---dy----", String.valueOf(dy));
                //下拉拖动（当listview显示第一条数据）处理自已事件，不让listview原生的拖动事件生效
                if (dy > 0 && getFirstVisiblePosition() == 0){
                    //当前padding top 的参数值
                  float scrollYDis =  -ll_refresh_head_root_Height + dy;

                  if (scrollYDis < 0 && currentState != PULL_DOWN){
                      //刷新头没有完全显示
                      //下拉刷新的状态
                      currentState = PULL_DOWN;//标志位记录 目的只执行一次

                      refreshState();


                  }else if (scrollYDis >= 0 && currentState != RELEASE_STATE){
                      currentState = RELEASE_STATE; //记录松开刷新状态，
                      refreshState();
                  }

                  ll_refresh_head_root.setPadding(0, (int) scrollYDis,0,0);
                  return  true;
                }
                break;

            case MotionEvent.ACTION_UP: //松开
                downY = -1;
                //判断状态
                //如果是ＰＵＬＬ_DOWN状态，松开恢复原状
                if (currentState == PULL_DOWN)
                {
                    ll_refresh_head_root.setPadding(0,-ll_refresh_head_root_Height,0,0);
                }else if (currentState == RELEASE_STATE){
                    //刷新数据
                    ll_refresh_head_root.setPadding(0,0,0,0);

                    currentState = REFRESHING; //改变状为正在刷新数据的状态
                    refreshState();//刷新界面

                    //刷新数据
                    if (listener != null){
                        listener.refresdData();
                    }

                }
                break;

                default:
                    break;
        }


       return super.onTouchEvent(ev);
    }

    public void setOnRefreshDataListener(OnRefreshDataListener listener){
        this.listener = listener;
    }

    /**
     * 接口
     */
    public interface OnRefreshDataListener{
        void refresdData();
        void loadingMore();

    }

    /**
     * 动画
     */
    private void initAnimation(){
        up_ra = new RotateAnimation(0,-180, Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        up_ra.setDuration(500);
        up_ra.setFillAfter(true);//停留在动画结束状态

        down_ra = new RotateAnimation(-180,-360, Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        down_ra.setDuration(500);
        down_ra.setFillAfter(true);//停留在动画结束状态

    }

    /**
     *
     */
    private void refreshState() {

        switch(currentState){
            case  PULL_DOWN: //下拉刷新
                Log.i("refreshState","下拉刷新");
                tv_state.setText("下拉刷新");
                iv_arrow.startAnimation(down_ra);
                break;
            case RELEASE_STATE: //松开刷新
                Log.i("refreshState","松开刷新");
                tv_state.setText("松开刷新");
                iv_arrow.startAnimation(up_ra);
                break;
            case REFRESHING: //正在刷新状态
                iv_arrow.clearAnimation();//清除所有动画
                iv_arrow.setVisibility(View.INVISIBLE); //隐藏箭头图片
                pb_loading.setVisibility(View.VISIBLE);//显示进度条
                tv_state.setText("正在刷新数据...");

              break;

            default:

                break;
        }
    }

    /**
     * 刷新数据成功，处理结果
     */
    public void refreshStateFinish(){

        if (isLoadingMore){
            //加载更多
            isLoadingMore = false;
            //隐藏加载更多数据的组件
            foot.setPadding(0,-ll_refresh_foot_Height,0,0);
        }else{
            //下拉刷新
            //改变状态
            tv_state.setText("下拉刷新");
            iv_arrow.setVisibility(View.VISIBLE);//显示图片箭头
            pb_loading.setVisibility(View.INVISIBLE);//隐藏进度条
            //设置刷亲时间为当前时间
            tv_time.setText(getCurrentFormatDate());

            //隐藏刷新的头布局
            ll_refresh_head_root.setPadding(0,-ll_refresh_head_root_Height,0,0);

            currentState = PULL_DOWN; //初始化为下拉刷新的状态
        }

    }

    private String getCurrentFormatDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

        return format.format(new Date());
    }

    /**
     * 轮播图是否显示
     */
    private boolean isLunboFullShow() {

        //判断轮播图是否完全显示

            int[] location = new int[2];
            //如果轮播图没有完全显示，相应的是Listview的事件
            //
            if (listViewOnScreanY ==0){
                this.getLocationOnScreen(location);
                //获取listview在屏幕中的Ｙ轴坐标
                listViewOnScreanY = location[1];
            }

            Log.i("listview屏幕中的坐标", location[1] + "");

            //轮播图在屏幕中的坐标
            lunbotu.getLocationOnScreen(location);
            Log.i("轮播图在屏幕中的坐标", location[1] + "");
            //判断
            if (location[1] < listViewOnScreanY){
                //轮播图没有完全显示
                //继续响应listview事件
                return false;
            }

            return true;

    }


    public RefreshListView(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context,attrs,0);

    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
        initAnimation();
        initEvent();
    }

    private void initEvent() {
        //添加当前Listviewr的滑动动事件
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //状态停止，如果listview显示最后一条　加载更多数据　的显示
                //是否最后一条数据显示
                Log.i("加载更多数据对比",getLastVisiblePosition()+"=="+ getAdapter().getCount()+"" +isLoadingMore);
                if (getLastVisiblePosition() == getAdapter().getCount()-1 &&  !isLoadingMore){
                    //最后一条数据，显示加载更多的组件
                    foot.setPadding(0,0,0,0);
                    setSelection(getAdapter().getCount());

                    //加载更多数据

                    isLoadingMore = true;

                    if (listener != null){
                        listener.loadingMore(); //实现该接口的组件去完成数据加载
                        Log.i("加载更多数据对比","进入if内部");
                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

//    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    /**
     * 初始化尾部组件
     */
    private void initFoot(){

        foot = View.inflate(getContext(), R.layout.listview_reresh_foot,null);

        foot.measure(0,0);

        //listview尾部组件的高度
        ll_refresh_foot_Height = foot.getMeasuredHeight();

        foot.setPadding(0,-ll_refresh_foot_Height,0,0);


        //加载到ListView中
        addFooterView(foot);



    }

    /**
     * 初始化头部组件
     */
    private void initHead(){


        head = (LinearLayout) View.inflate(getContext(),R.layout.listview_head_container,null);

        //listview刷新头的根布局
        ll_refresh_head_root = head.findViewById(R.id.ll_listview_head_root);

        //获取刷新头布局的子组件
        //刷新状态的文字
        tv_state = head.findViewById(R.id.tv_listview_head_state_dec);
       //刷新时间
        tv_time = head.findViewById(R.id.tv_listview_head_refresh_time);
       //刷新图片
        iv_arrow = head.findViewById(R.id.iv_listview_head_arrow);
        //刷新的进度
        pb_loading = head.findViewById(R.id.pb_listview_head_loading);



        //隐藏刷新头的根布局,轮播图还要显示
        //获取刷新头组件的高度
        ll_refresh_head_root.measure(0,0);

        //获取测量的高度

        ll_refresh_head_root_Height = ll_refresh_head_root.getMeasuredHeight();

        ll_refresh_head_root.setPadding(0,-ll_refresh_head_root_Height,0,0);


        addHeaderView(head);


    }

    /**
     * 用户自己选择是否启用下拉刷新
     * @param isPullrefresh
     * true 启用下拉刷新　false不用下拉刷新
     */
    public  void setIsRefreshHead(boolean isPullrefresh){
        isEnablePullRefresh = isPullrefresh;

    }

    /**
     * 轮播图的view
     * @param view
     */
    @Override
    public void addHeaderView(View view)
    {
        //判断　，如果你使用下拉刷新，把头布局加下拉刷新容器中，否则加载原生Listview中
        if (isEnablePullRefresh){
            //轮播图的组件
            lunbotu = view;
             head.addView(view);
            //addHeaderView(view);
        }else {
            super.addHeaderView(view);
        }



    }
}
