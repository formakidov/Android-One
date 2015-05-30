package com.formakidov.rssreader.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

public class NewsListFragment extends Fragment implements OnRefreshListener {
	private RssDataTask rssDataTask;
	private NewsAdapter adapter;
	private String url;
	private SwipeRefreshLayout swipeRefreshLayout;
	private ListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		ActionBar mainActionBar = getActivity().getActionBar();
		mainActionBar.setDisplayHomeAsUpEnabled(true);
		mainActionBar.setHomeButtonEnabled(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.news_list, parent, false);

	    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
	    swipeRefreshLayout.setOnRefreshListener(this);
	    swipeRefreshLayout.setColorSchemeResources(
	    		android.R.color.holo_green_light,
	            android.R.color.holo_red_light,
	            android.R.color.holo_blue_light,
	            android.R.color.holo_orange_light);
	    
		listView = (ListView)view.findViewById(R.id.list);
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(getActivity(), NewsPagerActivity.class);
				i.putExtra(NewsFragment.EXTRA_NEWS_INDEX, position);
				startActivity(i);
			}
		});
		
		url = getActivity().getIntent().getStringExtra(Constants.EXTRA_FEED_URL);
		executeTask();
		
		swipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(true);
			}
		});		
		
		return view;
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
		cancelTask();
		rssDataTask = new RssDataTask() {

			@Override
			protected void onPostExecute(List<RssItem> result) {
				if (result.size() > 0 && null != getActivity()) {
					updateNews(result);
				}
				swipeRefreshLayout.setRefreshing(false);
			}
		};
		rssDataTask.execute(url);
	}

	private void updateNews(List<RssItem> result) {
		if (null == adapter) {
			adapter = new NewsAdapter((ArrayList<RssItem>) result);
			listView.setAdapter(adapter);
		} else {
			adapter.clear();
			adapter.addAll(result);
		}
	}

	@Override
	public void onRefresh() {
		executeTask();
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
			holder.title.setText(strTitle);
			holder.pubDate.setText(pub);
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
        public final TextView pubDate;
        public final ImageView picture;
        
        public ViewHolder(View view) {
        	title = (TextView) view.findViewById(R.id.title);
        	picture = (ImageView) view.findViewById(R.id.picture);
        	pubDate = (TextView) view.findViewById(R.id.pubDate);
        }
    }
}
