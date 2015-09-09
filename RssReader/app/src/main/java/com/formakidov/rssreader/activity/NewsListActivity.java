package com.formakidov.rssreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.fragment.NewsDetailFragment;
import com.formakidov.rssreader.fragment.NewsListFragment;
import com.formakidov.rssreader.tools.Tools;

public class NewsListActivity extends AppCompatActivity implements NewsListFragment.Callbacks {
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (null != findViewById(R.id.news_detail_container)) {
            mTwoPane = true;
        }
    }

    @Override
    public void onItemSelected(String uuid) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(NewsDetailFragment.EXTRA_NEWS_UUID, uuid);
            NewsDetailFragment fragment = new NewsDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.news_detail_container, fragment)
                    .commit();
        } else {
            Intent detailIntent = new Intent(this, NewsDetailActivity.class);
            detailIntent.putExtra(NewsDetailFragment.EXTRA_NEWS_UUID, uuid);
    		startActivity(detailIntent);
            Tools.nextActivityAnimation(this);
        }
    }

	@Override
	public void loadFirstNews(String uuid) {
		if (mTwoPane) {
			onItemSelected(uuid);
		}
	}

	@Override
	public void onBackPressed() {
		Tools.previousActivityAnimation(this);
	}
}
