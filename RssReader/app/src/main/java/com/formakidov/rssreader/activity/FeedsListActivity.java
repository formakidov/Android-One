package com.formakidov.rssreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.fragment.FeedsListFragment;
import com.formakidov.rssreader.tools.Tools;

public class FeedsListActivity extends AppCompatActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_list);

		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle(getString(R.string.feeds));
		setSupportActionBar(toolbar);
		getSupportFragmentManager().beginTransaction()
        .add(R.id.fragmentContainer, new FeedsListFragment())
        .commit();
	}

	@Override
	public void onBackPressed() {
		Tools.previousActivityAnimation(this);
	}
}
