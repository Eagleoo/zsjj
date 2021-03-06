package com.yda.handWine;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.lzy.okgo.OkGo;
import com.mob.MobSDK;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2018/3/29.
 */

public class App extends Application {
    private  static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        MobSDK.init(this);
        OkGo.getInstance().init(this);
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }


}
