package com.formakidov.rssreader.activity;

import android.support.v4.app.Fragment;

import com.formakidov.rssreader.fragment.FeedListFragment;

public class FeedListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new FeedListFragment();
	}
}
