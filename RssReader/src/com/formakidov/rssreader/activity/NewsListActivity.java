package com.formakidov.rssreader.activity;

import android.support.v4.app.Fragment;

import com.formakidov.rssreader.fragment.NewsListFragment;

public class NewsListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new NewsListFragment();
	}
}
