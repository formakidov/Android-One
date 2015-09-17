package com.formakidov.rssreader.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.fragment.NewsDetailsFragment;
import com.formakidov.rssreader.tools.Constants;

public class NewsDetailsActivity extends BaseActivity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(getIntent().getStringExtra(Constants.EXTRA_FEED_NAME));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (null == savedInstanceState) {
            Bundle arguments = new Bundle();
            arguments.putString(EXTRA_NEWS_UUID,
                    getIntent().getStringExtra(EXTRA_NEWS_UUID));
            NewsDetailsFragment fragment = new NewsDetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.news_details, fragment)
                    .commit();
        }
    }
}
