package com.formakidov.rssreader.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.Tools;
import com.formakidov.rssreader.fragment.NewsDetailFragment;

public class NewsDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (null == savedInstanceState) {
            Bundle arguments = new Bundle();
            arguments.putString(NewsDetailFragment.EXTRA_NEWS_UUID,
                    getIntent().getStringExtra(NewsDetailFragment.EXTRA_NEWS_UUID));
            NewsDetailFragment fragment = new NewsDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.news_detail_container, fragment)
                    .commit();
        }
    }

	@Override
	public void onBackPressed() {
		Tools.finishActivity(this);
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
        	this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
