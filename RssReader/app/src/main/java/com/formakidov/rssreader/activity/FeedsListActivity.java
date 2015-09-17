package com.formakidov.rssreader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.fragment.FeedsListFragment;

public class FeedsListActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_list);

		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle(getString(R.string.feeds));
		setSupportActionBar(toolbar);
		Fragment fragment = new FeedsListFragment();
		getSupportFragmentManager().beginTransaction()
        .add(R.id.fragmentContainer, fragment)
        .commit();
	}
}
