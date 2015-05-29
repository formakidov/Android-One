package com.formakidov.rssreader.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.formakidov.rssreader.Constants;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.RssDataTask;
import com.formakidov.rssreader.activity.NewsPagerActivity;
import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.tools.Tools;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class NewsListFragment extends ListFragment {
	private RssDataTask rssDataTask;
	private MenuItem refreshItem;
	private NewsAdapter adapter;
	private MenuItem progressItem;
	private String url;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		url = getActivity().getIntent().getStringExtra(Constants.EXTRA_FEED_URL);
		executeTask();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, parent, savedInstanceState);

		ListView listView = (ListView)v.findViewById(android.R.id.list);
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		
		return v;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getActivity(), NewsPagerActivity.class);
		i.putExtra(NewsFragment.EXTRA_NEWS_INDEX, position);
		startActivity(i);
	}	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.news, menu);		
		ActionBar mainActionBar = getActivity().getActionBar();
		mainActionBar.setDisplayHomeAsUpEnabled(true);
		mainActionBar.setHomeButtonEnabled(true);
		mainActionBar.setDisplayShowTitleEnabled(false);
		
		refreshItem = menu.findItem(R.id.refresh);
		progressItem = menu.findItem(R.id.progress);
		refreshItem.setVisible(false);
		progressItem.setVisible(false);
		refreshItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.refresh:
					executeTask();
					return true;
				}
				return false;
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			cancelTask();
			getActivity().finish();
			return true;
		}
		return false;
	}
	
	private void cancelTask() {
		if (null != rssDataTask) {
			rssDataTask.cancel(true);
			rssDataTask = null;
		}
	}
	
	private void executeTask() {
		changeRefreshVisibility(false);
		cancelTask();
		rssDataTask = new RssDataTask() {

			@Override
			protected void onPostExecute(List<RssItem> result) {
				if (result.size() > 0) {
					changeRefreshVisibility(true);
					if (null == getActivity()) return;
					if (null == adapter) {
						adapter = new NewsAdapter((ArrayList<RssItem>) result);
						setListAdapter(adapter);
					} else {
						adapter.clear();
						adapter.addAll(result);
					}
				}
			}
		};
		rssDataTask.execute(url);
	}
	
	private void changeRefreshVisibility(boolean isVisible) {
		if (null != refreshItem && null != progressItem) {
			refreshItem.setVisible(isVisible ? true : false);
			progressItem.setVisible(isVisible ? false : true);
		}
	}
	
	private class NewsAdapter extends ArrayAdapter<RssItem> {

		public NewsAdapter(ArrayList<RssItem> items) {
			super(getActivity(), 0, items);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			//TODO make good style
			View view = convertView;
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(R.layout.list_item_news, null);
				ViewHolder holder = new ViewHolder(view);
				view.setTag(holder);
			}
			final ViewHolder holder = (ViewHolder) view.getTag();

			final RssItem item = getItem(position);
			String pub = item.getPubDate();				
			try {
				Date date = Tools.RFC822_DATE_FORMAT.parse(pub);
				pub = Constants.dateFormatPubDate.format(date) + " " + Constants.format24.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			String strTitle = item.getTitle().isEmpty() ? item.getDefTitle() : item.getTitle();
			SpannableString styledString = new SpannableString(strTitle + "\n" + pub);
			styledString.setSpan(new RelativeSizeSpan(0.8f), strTitle.length() + 1, strTitle.length() + pub.length() + 1, 0);			
			holder.title.setText(styledString);

			String imageUrl = item.getImageUrl().isEmpty() ? item.getDefImageUrl() : item.getImageUrl();
			Tools.imageLoader.loadImage(imageUrl, new ImageLoadingListener() {				
				@Override
				public void onLoadingStarted(String arg0, View arg1) { }				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) { }				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) { }				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
					if (null != bitmap) {
						holder.picture.setImageBitmap(Tools.getRoundedCornerBitmap(bitmap, 20));
					} else {
						holder.picture.setImageResource(R.drawable.no_image);
					}
				}
			});
			
			return view;
		}

		@Override
		public RssItem getItem(int position) {
			return RssDataTask.rssItems.get(position);
		}
	}
	
	private static class ViewHolder {
        public final TextView title;
        public final ImageView picture;
        
        public ViewHolder(View view) {
        	title = (TextView) view.findViewById(R.id.title);
        	picture = (ImageView) view.findViewById(R.id.picture);
        }
    }
}
