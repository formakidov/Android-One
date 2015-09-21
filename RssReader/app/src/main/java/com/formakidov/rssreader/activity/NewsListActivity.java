package com.formakidov.rssreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.fragment.NewsDetailsFragment;
import com.formakidov.rssreader.fragment.NewsListFragment;
import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.Tools;

public class NewsListActivity extends AppCompatActivity implements NewsListFragment.Callbacks, Constants {
    private boolean mTwoPane;
    private String feedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        feedName = getIntent().getStringExtra(EXTRA_FEED_NAME);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(feedName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (null != findViewById(R.id.news_details)) {
            mTwoPane = true;
        }
    }

    @Override
    public void onItemSelected(String uuid) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(EXTRA_NEWS_UUID, uuid);
            arguments.putString(EXTRA_FEED_NAME, feedName);
            NewsDetailsFragment fragment = new NewsDetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.news_details, fragment)
                    .commit();
        } else {
            Intent detailIntent = new Intent(this, NewsDetailsActivity.class);
            detailIntent.putExtra(EXTRA_NEWS_UUID, uuid);
            detailIntent.putExtra(EXTRA_FEED_NAME, feedName);
    		startActivity(detailIntent);
            Tools.nextActivityAnimation(this);
        }
    }

	@Override
	public void showNewsInDetails(String uuid) {
		if (mTwoPane) {
			onItemSelected(uuid);
		}
	}

    @Override
    public void onBackPressed() {
        Tools.previousActivityAnimation(this);
    }
}
