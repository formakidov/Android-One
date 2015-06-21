package com.formakidov.rssreader.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.Tools;
import com.formakidov.rssreader.fragment.FeedListFragment;

public class FeedListActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_list);
		
		Fragment fragment = new FeedListFragment();
		getFragmentManager().beginTransaction()
        .add(R.id.fragmentContainer, fragment)
        .commit();
	}

	@Override
	public void onBackPressed() {
		Tools.finishActivity(this);
	}
}
