package com.formakidov.rssreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.fragment.NewsDetailFragment;
import com.formakidov.rssreader.tools.Tools;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (null == savedInstanceState) {
            Bundle arguments = new Bundle();
            arguments.putString(NewsDetailFragment.EXTRA_NEWS_UUID,
                    getIntent().getStringExtra(NewsDetailFragment.EXTRA_NEWS_UUID));
            NewsDetailFragment fragment = new NewsDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.news_detail_container, fragment)
                    .commit();
        }
    }

	@Override
	public void onBackPressed() {
		Tools.previousActivityAnimation(this);
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
