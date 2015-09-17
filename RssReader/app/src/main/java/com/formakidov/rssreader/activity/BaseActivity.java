package com.formakidov.rssreader.activity;

import android.support.v7.app.AppCompatActivity;

import com.formakidov.rssreader.tools.Tools;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Tools.previousActivityAnimation(this);
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
