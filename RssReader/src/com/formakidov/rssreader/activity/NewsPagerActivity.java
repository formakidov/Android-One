package com.formakidov.rssreader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.RssDataTask;
import com.formakidov.rssreader.RssItem;
import com.formakidov.rssreader.fragment.NewsFragment;

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
				return NewsFragment.newInstance(pos);
			}
		});

		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			public void onPageScrollStateChanged(int state) { }

			public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) { }

			public void onPageSelected(int pos) {
				RssItem news = RssDataTask.rssItems.get(pos);
				
				if (!news.getDefTitle().isEmpty()) {
					setTitle(news.getDefTitle());
				}
			}
		});

		int pos = (Integer) getIntent().getSerializableExtra(NewsFragment.EXTRA_NEWS_INDEX);
		mViewPager.setCurrentItem(pos);
	}
}
