package com.example.smartbj.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by long on 2018/8/16.
 * 本地存取
 */

public class SpTools {

    public  static  void setBoolean(Context context, String key, boolean value )
    {
       SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE,Context.MODE_PRIVATE);

        sp.edit().putBoolean(key,value).commit();//提交保存键值对

    }

    public  static boolean getBoolean(Context context, String key, boolean defValue )
    {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE,Context.MODE_PRIVATE);

        return sp.getBoolean(key,defValue);
    }

    public  static  void setString(Context context, String key, String value )
    {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE,Context.MODE_PRIVATE);

        sp.edit().putString(key,value).commit();//提交保存键值对

    }

    public  static String getString(Context context, String key, String defValue )
    {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE,Context.MODE_PRIVATE);

        return sp.getString(key,defValue);
    }
}
