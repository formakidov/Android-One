package com.formakidov.rssreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.formakidov.rssreader.R;
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
    public void onItemSelected(int index) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(NewsDetailFragment.EXTRA_NEWS_INDEX, index);
            NewsDetailFragment fragment = new NewsDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.news_detail_container, fragment)
                    .commit();
        } else {
        	//TODO: NewsPagerActivity.class
            Intent detailIntent = new Intent(this, NewsDetailActivity.class);
            detailIntent.putExtra(NewsDetailFragment.EXTRA_NEWS_INDEX, index);
            startActivity(detailIntent);
        }
    }
}
