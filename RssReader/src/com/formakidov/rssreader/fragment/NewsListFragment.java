package com.formakidov.rssreader.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import com.formakidov.rssreader.Constants;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.RssDataTask;
import com.formakidov.rssreader.RssItem;
import com.formakidov.rssreader.Tools;
import com.formakidov.rssreader.activity.NewsPagerActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class NewsListFragment extends ListFragment {
	private RssDataTask rssDataTask;
	private String url = "http://onliner.by/feed";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.build();
		ImageLoaderConfiguration config = 
				new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.build();
		Tools.prepareTools(getActivity(), config);

		rssDataTask = new RssDataTask() {
			@Override
			protected void onPostExecute(List<RssItem> result) {
				if (result.size() > 0) {
					//TODO something
					NewsAdapter adapter = new NewsAdapter((ArrayList<RssItem>) result);		
					setListAdapter(adapter);
				}
			}
		};
		rssDataTask.execute(url);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, parent, savedInstanceState);

		ListView listView = (ListView)v.findViewById(android.R.id.list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);		
		
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) { }
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.news_list_item_context, menu);
				return true;
			}
			
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_delete_news:						
						NewsAdapter adapter = (NewsAdapter)getListAdapter();							
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								RssDataTask.rssItems.remove(adapter.getItem(i));
							}
						}
						mode.finish(); 
						adapter.notifyDataSetChanged();
						
						return true;						
					default:
						return false;
				}
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) { }
		});
		
		return v;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.news_list_item_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		NewsAdapter adapter = (NewsAdapter)getListAdapter(); 
		RssItem newsItem = adapter.getItem(position);
		
		switch (item.getItemId()) {
			case R.id.menu_item_delete_news:
				RssDataTask.rssItems.remove(newsItem);
				adapter.notifyDataSetChanged();
				return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getActivity(), NewsPagerActivity.class);
		i.putExtra(NewsFragment.EXTRA_NEWS_INDEX, position);
		startActivity(i);
	}	
	
	private class NewsAdapter extends ArrayAdapter<RssItem> {

		public NewsAdapter(ArrayList<RssItem> items) {
			super(getActivity(), 0, items);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_news, null);
			}

			final RssItem item = getItem(position);
			String pub = item.getPubDate();				
			try {
				Date date = Tools.RFC822_DATE_FORMAT.parse(pub);
				pub = Constants.dateFormatPubDate.format(date) + " " + Constants.format24.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
			String title = item.getTitle().isEmpty() ? item.getDefTitle() : item.getTitle();
			SpannableString styledString = new SpannableString(title + "\n" + pub);
			styledString.setSpan(new RelativeSizeSpan(0.8f), title.length() + 1, title.length() + pub.length() + 1, 0);
			tvTitle.setText(styledString);
			
			final ImageView picture = (ImageView) convertView.findViewById(R.id.picture);
			String imageUrl = item.getImageUrl().isEmpty() ? item.getDefImageUrl() : item.getImageUrl();
			Tools.imageLoader.loadImage(imageUrl, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) { }
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) { }
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
					if (null != bitmap) {
						picture.setImageBitmap(Tools.getRoundedCornerBitmap(bitmap, 10));
					} else {
						picture.setImageResource(R.drawable.no_image);
					}
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) { }
			});
			
			return convertView;
		}

		@Override
		public RssItem getItem(int position) {
			return RssDataTask.rssItems.get(position);
		}
	}
}
