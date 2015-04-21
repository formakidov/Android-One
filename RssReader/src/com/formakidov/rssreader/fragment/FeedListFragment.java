package com.formakidov.rssreader.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.formakidov.rssreader.FeedItem;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.Tools;
import com.formakidov.rssreader.activity.NewsListActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FeedListFragment extends ListFragment {
	
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
		//TODO
		List<FeedItem> feeds = new ArrayList<FeedItem>();
		feeds.add(new FeedItem("onliner.by", "http://www.onliner.by/feed"));
		feeds.add(new FeedItem("itcuties.com", "http://www.itcuties.com/feed"));
		
		FeedAdapter adapter = new FeedAdapter((ArrayList<FeedItem>) feeds);		
		setListAdapter(adapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, parent, savedInstanceState);

		ListView listView = (ListView)v.findViewById(android.R.id.list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);		
		
//		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
//			
//			@Override
//			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) { }
//			
//			@Override
//			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//				MenuInflater inflater = mode.getMenuInflater();
//				inflater.inflate(R.menu.feed_list_item_context, menu);
//				return true;
//			}
//			
//			@Override
//			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//				return false;
//			}
//			
//			@Override
//			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//				//TODO
//				switch (item.getItemId()) {
//					case R.id.menu_item_delete_feed:						
//						NewsAdapter adapter = (NewsAdapter)getListAdapter();							
//						for (int i = adapter.getCount() - 1; i >= 0; i--) {
//							if (getListView().isItemChecked(i)) {
//								RssDataTask.rssItems.remove(adapter.getItem(i));
//							}
//						}
//						mode.finish(); 
//						adapter.notifyDataSetChanged();
//						
//						return true;						
//					default:
//						return false;
//				}
//			}
//			
//			@Override
//			public void onDestroyActionMode(ActionMode mode) { }
//		});
		
		return v;
	}
	
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//		getActivity().getMenuInflater().inflate(R.menu.feed_list_item_context, menu);
//	}
	
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
//		int position = info.position;
//		FeedAdapter adapter = (FeedAdapter)getListAdapter(); 
//		//TODO
////		RssItem newsItem = adapter.getItem(position);
//		
//		switch (item.getItemId()) {
//			case R.id.menu_item_delete_feed:
//				//TODO
////				RssDataTask.rssItems.remove(newsItem);
////				adapter.notifyDataSetChanged();
//				return true;
//		}
//		return super.onContextItemSelected(item);
//	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getActivity(), NewsListActivity.class);
		//TODO
		i.putExtra(NewsFragment.EXTRA_FEED_URL, "http://www.onliner.by/feed");
		startActivity(i);
	}	
	
	private class FeedAdapter extends ArrayAdapter<FeedItem> {
		private ArrayList<FeedItem> items;
		
		public FeedAdapter(ArrayList<FeedItem> items) {
			super(getActivity(), 0, items);
			this.items = items;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_feed, null);
			}
			
			TextView feedName = (TextView) convertView.findViewById(R.id.feed_name);
			//TODO
			feedName.setText(getItem(position).getName() + "==" + getItem(position).getUrl());
			return convertView;
		}

		@Override
		public FeedItem getItem(int position) {
			return items.get(position);
		}
	}
}
