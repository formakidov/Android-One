package com.formakidov.rssreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.fragment.NewsDetailsFragment;
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

        if (null != findViewById(R.id.news_details)) {
            mTwoPane = true;
        }
    }

    @Override
    public void onItemSelected(String uuid) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(NewsDetailsFragment.EXTRA_NEWS_UUID, uuid);
            NewsDetailsFragment fragment = new NewsDetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.news_details, fragment)
                    .commit();
        } else {
            Intent detailIntent = new Intent(this, NewsDetailActivity.class);
            detailIntent.putExtra(NewsDetailsFragment.EXTRA_NEWS_UUID, uuid);
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
