package com.yda.handWine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yda.handWine.ScreenAdaptation;
import com.yda.handWine.util.ActivityCollector;

public class BaseActivity extends AppCompatActivity {
    public Activity context;
    public SharedPreferences sp;
    public boolean isWidth() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this;
        Log.e("isWidth", "isWidth()" + isWidth());
        ScreenAdaptation.setCustomDesity(this, getApplication(), isWidth());
        ActivityCollector.addActivity(this);
        sp= getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
