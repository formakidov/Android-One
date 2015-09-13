package com.formakidov.rssreader.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.adapter.NewsAdapter;
import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.task.RssDataTask;
import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.ItemClickSupport;
import com.formakidov.rssreader.tools.Tools;

import java.util.ArrayList;
import java.util.List;

public class NewsListFragment extends Fragment implements Constants {
    private Callbacks mCallbacks = sDummyCallbacks;
	private AsyncTask<Void, Void, List<RssItem>> loadOldNewsTask;
	private RssDataTask rssDataTask;
	private NewsAdapter adapter;
	private SwipeRefreshLayout swipeRefreshLayout;
	private TextView tvErrorMessage;
	private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setHasOptionsMenu(true);
		setRetainInstance(true);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.news_list, parent, false);
		adapter = new NewsAdapter(this, new ArrayList<RssItem>());
		url = getActivity().getIntent().getStringExtra(EXTRA_FEED_URL);
		setupView(v);
		setRefreshing(true);
		loadOldNews();
		
		return v;
	}

	private void setupView(final View v) {
		final RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		ItemClickSupport support = ItemClickSupport.addTo(recyclerView);
		support.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
			@Override
			public void onItemClicked(RecyclerView recyclerView, int position, View v) {
				if (adapter.getItemCount() != 0) {
					mCallbacks.onItemSelected(adapter.getItem(position).getUUID());
				}
			}
		});

		tvErrorMessage = (TextView) v.findViewById(R.id.error_message);

		swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				//TODO show progress in details
				if (Tools.isNetworkAvailable(getContext())) {
					loadFreshNews(url);
				} else {
					Snackbar.make(v, R.string.error_check_network_connection, Snackbar.LENGTH_LONG).show();
					setRefreshing(false);
				}
			}
		});

		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_green_light,
				android.R.color.holo_red_light,
				android.R.color.holo_blue_light,
				android.R.color.holo_orange_light);
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
				} else if (null == adapter || adapter.getItemCount() == 0) {
					if (!Tools.isNetworkAvailable(getActivity())) {
						showErrorMessage(getString(R.string.error_check_network_connection));
					} else {
						//TODO: can't load rss feed
						showErrorMessage(getString(R.string.error_check_url));
					}
				}
				setRefreshing(false);
			}
		};
		rssDataTask.execute(url);
	}
	
	private void updateNews(List<RssItem> result) {
		if (hasNewNews(result)) {
			saveNewsInDatabase(result);
			adapter.reset(result);
		}
	}
	
	private boolean hasNewNews(List<RssItem> list)  {
		//TODO replace with check pubdate/lastBuildDate
		if (adapter.getItemCount() > 0 &&
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

	private void showErrorMessage(String message) {
		tvErrorMessage.setVisibility(View.VISIBLE);
		tvErrorMessage.setText(message);
	}

	private void hideErrorMessage() {
		tvErrorMessage.setVisibility(View.INVISIBLE);
		tvErrorMessage.setText(EMPTY_STRING);
	}

	public interface Callbacks {
		void onItemSelected(String uuid);
		void loadFirstNews(String uuid);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String uuid) { }
		@Override
		public void loadFirstNews(String uuid) { }
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}
		mCallbacks = (Callbacks) activity;
	}

    @Override
    public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}
}
