package com.formakidov.rssreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.fragment.NewsDetailsFragment;
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
            arguments.putString(NewsDetailsFragment.EXTRA_NEWS_UUID,
                    getIntent().getStringExtra(NewsDetailsFragment.EXTRA_NEWS_UUID));
            NewsDetailsFragment fragment = new NewsDetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.news_details, fragment)
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
