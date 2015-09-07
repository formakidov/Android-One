package com.formakidov.rssreader.fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.formakidov.rssreader.Constants;
import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.RssDataTask;
import com.formakidov.rssreader.Tools;
import com.formakidov.rssreader.data.RssItem;

import java.util.ArrayList;
import java.util.List;

public class NewsListFragment extends Fragment implements Constants {
    private Callbacks mCallbacks = sDummyCallbacks;
	private RssDataTask rssDataTask;
	private NewsAdapter adapter;
	private SwipeRefreshLayout swipeRefreshLayout;
	private ListView listView;
	private TextView errorMessage;
	private String url;	

    public interface Callbacks {
        public void onItemSelected(String uuid);
        public void loadFirstNews(String uuid);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String uuid) { }
		@Override
		public void loadFirstNews(String uuid) { }
    };

    private AsyncTask<Void, Void, List<RssItem>> loadOldNewsTask;
    
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

		url = getActivity().getIntent().getStringExtra(EXTRA_FEED_URL);
	    errorMessage = (TextView) view.findViewById(R.id.error_message);
	    
	    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
	    swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

	    	@Override
	    	public void onRefresh() {
	    		//show progress in details
	    		loadFreshNews(url);
	    	}
		});
	    swipeRefreshLayout.setColorSchemeResources(
	    		android.R.color.holo_green_light,
	            android.R.color.holo_red_light,
	            android.R.color.holo_blue_light,
	            android.R.color.holo_orange_light);
	    
		listView = (ListView)view.findViewById(R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.isEmpty()) {
                    mCallbacks.onItemSelected(adapter.getItem(position).getUUID());
                }
		        //TODO set background on activated item
			}
		});
		
		adapter = new NewsAdapter(new ArrayList<RssItem>());
		listView.setAdapter(adapter);
		
		setRefreshing(true);
		
		loadOldNews();
		
		return view;
	}

    private void setRefreshing(final boolean refreshing) {
		swipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(refreshing);
			}
		});				
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			cancelLoadFreshNewsTask();
			cancelLoadOldNewsTask();
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
	private void cancelLoadFreshNewsTask() {
		if (null != rssDataTask) {
			rssDataTask.cancel(true);
			rssDataTask = null;
		}
	}
	
	private void cancelLoadOldNewsTask() {
		if (null != loadOldNewsTask) {
			loadOldNewsTask.cancel(true);
			loadOldNewsTask = null;
		}
	}
	
	private void loadOldNews() {
		cancelLoadOldNewsTask();
		loadOldNewsTask = new AsyncTask<Void, Void, List<RssItem>>() {

			@Override
			protected List<RssItem> doInBackground(Void... params) {
				DatabaseManager manager = DatabaseManager.getInstance(getActivity());
				return manager.getAllNews();
			}

			@Override
			protected void onPostExecute(List<RssItem> result) {
				if (null == result || 0 == result.size()) {
					loadFreshNews(url);
				} else {
					updateNews(result);
					setRefreshing(false);
				}
			}
		}.execute();
	}
	
	private void loadFreshNews(String url) {
		cancelLoadFreshNewsTask();
		rssDataTask = new RssDataTask() {

			@Override
			protected void onPostExecute(List<RssItem> result) {
				if (null == getActivity()) return;
				if (null != result && result.size() > 0) {
					hideErrorMessage();
					updateNews(result);
				} else if (null == adapter || adapter.getCount() == 0) {
					//TODO empty screen from libs for developers (with btns "try again"
					if (!Tools.isNetworkAvailable(getActivity())) {
						showErrorMessage(ERROR_CHECK_NETWORK_ONNECTION);
					} else {
						//TODO: can't load rss feed
						showErrorMessage(ERROR_CHECK_URL);
					}
				}
				setRefreshing(false);
			}
		};
		rssDataTask.execute(url);
	}

	private void showErrorMessage(String message) {
		errorMessage.setVisibility(View.VISIBLE);
		errorMessage.setText(message);
	}
	
	private void hideErrorMessage() {
		errorMessage.setVisibility(View.INVISIBLE);
		errorMessage.setText(EMPTY_STRING);
	}
	
	private void updateNews(List<RssItem> result) {
		if (hasNewNews(result)) {
			saveNewsInDatabase(result);
			adapter.clear();
			adapter.addAll(result);
		}
	}
	
	private boolean hasNewNews(List<RssItem> list)  {
		if (adapter.getCount() > 0 && 
				adapter.getItem(0).getTitle().equals(list.get(0).getTitle())) {
			return false;
		}
		return true;
	}
	
	private void saveNewsInDatabase(final List<RssItem> items) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				DatabaseManager manager = DatabaseManager.getInstance(getActivity());
				manager.deleteAllNews();
				manager.addAllNews(items);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mCallbacks.loadFirstNews(adapter.getItem(0).getUUID());
			}
		}.execute();
	}
	
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }
    
    private class NewsAdapter extends ArrayAdapter<RssItem> {
		private List<RssItem> items;

		public NewsAdapter(List<RssItem> items) {
			super(getActivity(), 0, items);
			this.items = items;
		}

		@SuppressLint("InflateParams") 
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			//TODO make good style
			View view = convertView;
			if (null == view) {
				view = getActivity().getLayoutInflater().inflate(R.layout.list_item_news, null);
				ViewHolder holder = new ViewHolder(view);
				view.setTag(holder);
			}
			final ViewHolder holder = (ViewHolder) view.getTag();

			RssItem item = getItem(position);
			
			String strTitle = item.getTitle().isEmpty() ? item.getDefTitle() : item.getTitle();			
			holder.title.setText(strTitle);
			holder.pubDate.setText(item.getPubDate());
			
			return view;
		}

		@Override
		public RssItem getItem(int position) {
			return items.get(position);
		}
		
		private List<RssItem> getItems() {
			return items;
		}
	}
	
	private static class ViewHolder {
        public final TextView title;
        public final TextView pubDate;
        
        public ViewHolder(View view) {
        	title = (TextView) view.findViewById(R.id.title);
        	pubDate = (TextView) view.findViewById(R.id.pubDate);
        }
    }
}
