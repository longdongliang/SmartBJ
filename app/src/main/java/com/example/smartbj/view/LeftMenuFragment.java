package com.example.smartbj.view;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smartbj.R;
import com.example.smartbj.domain.NewsCenterData;

import java.util.ArrayList;
import java.util.List;

/**左侧菜单的Fragment
 * Created by long on 2018/8/23.
 */

public class LeftMenuFragment extends BaseFragment {


    public interface  OnSwitchPageListener{
       void switchPage(int selectionIndex);
    }

    private OnSwitchPageListener switchListener;

    public void setOnSwitchPageListener(OnSwitchPageListener listener){

    }

    private List<NewsCenterData.NewsData> data = new ArrayList<NewsCenterData.NewsData>(); //新闻中心左侧菜单数据

    private  MyAdapter adapter;

    private  ListView lv_leftData;

    private  int selectPosition; //选中的位置

    @Override
    public void initEvent() {
        lv_leftData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //保存选中的位置
                selectPosition = position;

                //更新界面
                adapter.notifyDataSetChanged();

                //控制新闻中心，四个页面的显示
               // mainActivity.getMainMenuFragment().leftMenuClickSwitchPage(selectPosition);
                if (switchListener != null) {
                    switchListener.switchPage(selectPosition);
                }else{
                    mainActivity.getMainMenuFragment().leftMenuClickSwitchPage(selectPosition);
                }
                //切换SlidingMenu的开关
                mainActivity.getSlidingMenu().toggle();
            }
        });
        super.initEvent();
    }

    @Override
    public View initView() {

        lv_leftData = new ListView(mainActivity);

        lv_leftData.setPadding(0,245,0,0);

        return lv_leftData;
    }

    public void setLeftMenuData(List<NewsCenterData.NewsData> data){
        this.data = data;
        adapter.notifyDataSetChanged();//设置好数据后，通知道界面刷新数据，进行显示
    }

    @Override
    public void initData() {
        //组织数据
        adapter = new MyAdapter();
        lv_leftData.setAdapter(adapter);
        super.initData();
    }

    private  class MyAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return data.size();
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
            TextView tv_currentView;
            //显示数据
            if (convertView == null)
            {
                tv_currentView = (TextView) View.inflate(mainActivity,R.layout.lefimenu_list_item,null);

            }else{
                tv_currentView = (TextView) convertView;
            }

            //设置数据
            tv_currentView.setText(data.get(position).title);

            //判断是否被选中
            tv_currentView.setEnabled(position == selectPosition);

          return tv_currentView;
        }
    }
}
