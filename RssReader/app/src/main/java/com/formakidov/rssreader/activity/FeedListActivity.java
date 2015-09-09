package com.formakidov.rssreader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.fragment.FeedListFragment;
import com.formakidov.rssreader.tools.Tools;

public class FeedListActivity extends AppCompatActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_list);

		Fragment fragment = new FeedListFragment();
		getSupportFragmentManager().beginTransaction()
        .add(R.id.fragmentContainer, fragment)
        .commit();
	}

	@Override
	public void onBackPressed() {
		Tools.previousActivityAnimation(this);
	}
}
