package com.formakidov.rssreader.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.formakidov.rssreader.FeedItem;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.Tools;
import com.formakidov.rssreader.activity.NewsListActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FeedListFragment extends ListFragment {
	private FeedAdapter adapter;

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
		
		adapter = new FeedAdapter((ArrayList<FeedItem>) feeds);		
		setListAdapter(adapter);
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
				inflater.inflate(R.menu.feed_list_item_context, menu);
				return true;
			}
			
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				//TODO
				switch (item.getItemId()) {
					case R.id.menu_item_delete_feed:												
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								adapter.deleteItem(i);
							}
						}
						mode.finish(); 
						adapter.notifyDataSetChanged();						
						return true;
						
					case R.id.menu_item_edit_feed:
						int count = 0;
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								count++;
							}
						}
						if (count == 1) {
							//TODO edit feed dialog
							mode.finish(); 
							adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getActivity(), R.string.choose_one_feed, Toast.LENGTH_LONG).show();
						}
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getActivity(), NewsListActivity.class);
		i.putExtra(NewsFragment.EXTRA_FEED_URL, adapter.getItem(position).getUrl());
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
		
		public void deleteItem(int position) {
			items.remove(position);
		}
	}
}
