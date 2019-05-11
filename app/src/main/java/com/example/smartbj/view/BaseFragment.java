package com.example.smartbj.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartbj.activity.MainActivity;



/**
 * Created by long on 2018/8/23.
 */

public abstract class BaseFragment extends Fragment {

    protected MainActivity mainActivity; //上下文

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();//获取fragment所在Activity


    }

    //覆盖此方法完成界面的显示
    public abstract View initView();

    //子类覆盖此方法,数据的初始化
    public void initData(){

    }

    //子类覆盖此方法,完成事件添加
    public  void initEvent()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = initView();

        return root;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //初始化事件和数据
        super.onActivityCreated(savedInstanceState);
        initData();
        initEvent();
    }
}
