package com.formakidov.rssreader.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.formakidov.rssreader.Constants;
import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.FeedDialog;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.activity.NewsListActivity;
import com.formakidov.rssreader.data.FeedItem;
import com.formakidov.rssreader.tools.Tools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FeedListFragment extends ListFragment implements Constants {
	private FeedAdapter adapter;

	@SuppressWarnings("deprecation")
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

		DatabaseManager manager = DatabaseManager.getInstance(getActivity());		
		List<FeedItem> feeds = manager.getFeeds();
		
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
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }		
			@Override
			public void onDestroyActionMode(ActionMode mode) { }
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.feed_list_item_context, menu);
				return true;
			}
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_delete_feed:												
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								adapter.deleteItem(i);
							}
						}
						mode.finish();
						return true;						
					case R.id.menu_item_edit_feed:
						int count = 0;
						int itemPosEdit = 0;
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								itemPosEdit = i;
								count++;
							}
						}
						if (count == 1) {
							openEditFeedDialog(itemPosEdit);
						} else {
							mode.finish();
							Toast.makeText(getActivity(), R.string.choose_one_feed, Toast.LENGTH_LONG).show();
						}
						return true;						
					default:
						return false;
				}
			}
		});
		
		return v;
	}
	
	private void openAddFeedDialog() {
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		FeedDialog feedDialog = new FeedDialog(this);
		feedDialog.show(ft, getString(R.string.add_feed));
	}
	
	private void openEditFeedDialog(int position) {
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		FeedItem feedItem = adapter.getItem(position);
		args.putInt(FEED_POSITION, position);
		args.putString(FEED_NAME, feedItem.getName());
		args.putString(FEED_URL, feedItem.getUrl());
		FeedDialog feedDialog = new FeedDialog(this);
		feedDialog.setArguments(args);
		feedDialog.show(ft, getString(R.string.edit_feed));		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.feeds, menu);
		MenuItem addItem = menu.findItem(R.id.add);
		addItem.setVisible(true);
		addItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.add:
					openAddFeedDialog();
					return true;
				}
				return false;
			}			
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getActivity(), NewsListActivity.class);
		i.putExtra(EXTRA_FEED_URL, adapter.getItem(position).getUrl());
		startActivity(i);
	}

	public void addFeed(FeedItem newItem) {
		adapter.addItem(newItem);
		DatabaseManager manager = DatabaseManager.getInstance(getActivity());
		List<FeedItem> list = manager.getFeeds();
		list.clear();
	}
	
	public void feedChanged(int position, FeedItem changedItem) {
		if (-1 != position) {
			DatabaseManager manager = DatabaseManager.getInstance(getActivity());		
			manager.deleteFeed(changedItem.getName());
			manager.addFeed(changedItem);
			adapter.deleteItem(position);
			adapter.addItem(changedItem, position);
		}
	}
	
	private class FeedAdapter extends ArrayAdapter<FeedItem> {
		private List<FeedItem> items;
		
		public FeedAdapter(ArrayList<FeedItem> items) {
			super(getActivity(), 0, items);
			this.items = items;
		}

		@SuppressLint("InflateParams") 
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(R.layout.list_item_feed, null);
				ViewHolder holder = new ViewHolder(view);
				view.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) view.getTag();

			//TODO make good style
			FeedItem item = getItem(position);
			String name = item.getName();
			String url = item.getUrl();
			holder.feedName.setText(name + " >> " + url);
			
			return view;
		}

		@Override
		public FeedItem getItem(int position) {
			return items.get(position);
		}
		
		public void deleteItem(int position) {
			DatabaseManager manager = DatabaseManager.getInstance(getActivity());		
			manager.deleteFeed(getItem(position).getName());
			items.remove(position);
		}

		public void addItem(FeedItem newItem, int position) {
			DatabaseManager manager = DatabaseManager.getInstance(getActivity());		
			manager.addFeed(newItem);
			insert(newItem, position);
		}
		
		public void addItem(FeedItem newItem) {
			DatabaseManager manager = DatabaseManager.getInstance(getActivity());		
			manager.addFeed(newItem);
			add(newItem);
		}
	}

	private static class ViewHolder {
        public final TextView feedName;

        public ViewHolder(View view) {
        	feedName = (TextView) view.findViewById(R.id.feed_name);
        }
    }
}
