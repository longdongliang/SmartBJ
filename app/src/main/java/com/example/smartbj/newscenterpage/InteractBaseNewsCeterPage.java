package com.example.smartbj.newscenterpage;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.smartbj.activity.MainActivity;
import com.example.smartbj.newscenterpage.BaseNewsCenterPage;

//

public class InteractBaseNewsCeterPage extends BaseNewsCenterPage {


    public InteractBaseNewsCeterPage(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    public View initView() {

        //要展示的内容
        TextView tv = new TextView(mainActivity);
        tv.setText("互动的内容");
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}
