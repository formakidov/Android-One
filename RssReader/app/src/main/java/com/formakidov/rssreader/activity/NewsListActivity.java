package com.formakidov.rssreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.Tools;
import com.formakidov.rssreader.fragment.NewsDetailFragment;
import com.formakidov.rssreader.fragment.NewsListFragment;

public class NewsListActivity extends Activity implements NewsListFragment.Callbacks {
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

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
            getFragmentManager().beginTransaction()
                    .replace(R.id.news_detail_container, fragment)
                    .commit();
        } else {
            Intent detailIntent = new Intent(this, NewsDetailActivity.class);
            detailIntent.putExtra(NewsDetailFragment.EXTRA_NEWS_UUID, uuid);
    		startActivity(detailIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
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
		Tools.finishActivity(this);
	}
}
