package com.formakidov.rssreader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.RssDataTask;
import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.fragment.NewsDetailFragment;

public class NewsPagerActivity extends FragmentActivity {
	private ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);

		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			@Override
			public int getCount() {
				return RssDataTask.rssItems.size();
			}

			@Override
			public Fragment getItem(int pos) {
				//TODO
//				return NewsFragment.newInstance(pos);
//				Bundle arguments = new Bundle();
//	            arguments.putInt(NewsDetailFragment.EXTRA_NEWS_INDEX, pos);
//	            Fragment fragment = new NewsDetailFragment();
//	            fragment.setArguments(arguments);
//				return fragment;
				return null;
			}
		});

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {			
			@Override
			public void onPageScrollStateChanged(int state) { }			
			@Override
			public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) { }			
			@Override
			public void onPageSelected(int pos) {
				RssItem news = RssDataTask.rssItems.get(pos);
				
				if (!news.getDefTitle().isEmpty()) {
					setTitle(news.getDefTitle());
				}
			}
		});

		int pos = (Integer) getIntent().getSerializableExtra(NewsDetailFragment.EXTRA_NEWS_INDEX);
		mViewPager.setCurrentItem(pos);
	}
}
