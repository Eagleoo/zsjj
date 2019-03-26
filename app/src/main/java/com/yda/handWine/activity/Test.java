package com.yda.handWine.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.yda.handWine.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Test extends AppCompatActivity {

    @BindView(R.id.gif)
    ImageView gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        ButterKnife.bind(this);

//        try {
//            GifDrawable gifFromAssets = new GifDrawable(getResources(), R.drawable.mission);
//            gif.setImageDrawable(gifFromAssets);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // Resources file


    }

}
