package com.formakidov.rssreader.fragment;

import android.content.Context;
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
import com.formakidov.rssreader.task.GetLastBuildDateTask;
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
	private TextView errorMessage;
	private String url;
	private boolean firstLoad = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setHasOptionsMenu(true);
		setRetainInstance(true);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.news_list, parent, false);
		adapter = new NewsAdapter(getContext(), new ArrayList<RssItem>());
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

		errorMessage = (TextView) v.findViewById(R.id.error_message);

		swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (Tools.isNetworkAvailable(getContext())) {
					loadFreshNews(url, true);
				} else {
					if (adapter.getItemCount() == 0) {
						showErrorMessage(getString(R.string.error_check_network_connection));
					} else {
						Snackbar.make(v, R.string.error_check_network_connection, Snackbar.LENGTH_LONG).show();
					}
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
				return manager.getAllNews(url);
			}
			@Override
			protected void onPostExecute(final List<RssItem> result) {
				if (null == getActivity() || null == adapter) return;
				if (null == result || 0 == result.size()) {
					loadFreshNews(url, false);
				} else {
					if (!Tools.isNetworkAvailable(getContext())) {
						adapter.reset(result);
						loadFirstNews(result.get(0).getUUID());
						setRefreshing(false);
						if (null != getView()) {
							Snackbar.make(getView(), R.string.error_check_network_connection, Snackbar.LENGTH_LONG).show();
						}
					} else {
						new GetLastBuildDateTask() {
							@Override
							protected void onPostExecute(Long lastBuildDate) {
								if (null == getActivity() || null == adapter) return;
								if (lastBuildDate > result.get(0).getRssBuildDate()) {
									loadFreshNews(url, false);
								} else {
									adapter.reset(result);
									loadFirstNews(result.get(0).getUUID());
									setRefreshing(false);
								}
							}
						}.execute(url);
					}
				}
			}
		}.execute();
	}

	private void loadFirstNews(String uuid) {
		if (firstLoad) {
			mCallbacks.showNewsInDetails(uuid);
			firstLoad = false;
		}
	}

	private void loadFreshNews(String url, final boolean checkBuildDate) {
		cancelLoadFreshNewsTask();
		rssDataTask = new RssDataTask() {
			@Override
			protected void onPostExecute(List<RssItem> result) {
				if (null == getActivity() || null == adapter) return;
				if (null != result && result.size() > 0) {
					hideErrorMessage();
					updateNews(result, checkBuildDate);
				} else if (adapter.getItemCount() == 0) {
					if (!Tools.isNetworkAvailable(getActivity())) {
						showErrorMessage(getString(R.string.error_check_network_connection));
					} else {
						showErrorMessage(getString(R.string.error_incorrect_url));
					}
					setRefreshing(false);
				}
			}
		};
		rssDataTask.execute(url);
	}
	
	private void updateNews(final List<RssItem> result, boolean check) {
		if (check) {
			new GetLastBuildDateTask() {
				@Override
				protected void onPostExecute(Long lastBuildDate) {
					if (null == getActivity() || null == adapter) return;
					if (adapter.getItemCount() > 0 && lastBuildDate > adapter.getItem(0).getRssBuildDate()) {
						saveNewsInDatabase(result);
						adapter.reset(result);
						setRefreshing(false);
					}
				}
			}.execute(url);
		} else {
			saveNewsInDatabase(result);
			adapter.reset(result);
			setRefreshing(false);
			loadFirstNews(result.get(0).getUUID());
		}
	}

	private void saveNewsInDatabase(final List<RssItem> items) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				DatabaseManager manager = DatabaseManager.getInstance(getActivity());
				manager.deleteAllNews(url);
				manager.addAllNews(items);
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				mCallbacks.showNewsInDetails(adapter.getItem(0).getUUID());
			}
		}.execute();
	}

	private void showErrorMessage(String message) {
		errorMessage.setVisibility(View.VISIBLE);
		errorMessage.setText(message);
	}

	private void hideErrorMessage() {
		errorMessage.setVisibility(View.INVISIBLE);
		errorMessage.setText(EMPTY_STRING);
	}

	public interface Callbacks {
		void onItemSelected(String uuid);
		void showNewsInDetails(String uuid);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String uuid) { }
		@Override
		public void showNewsInDetails(String uuid) { }
	};

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (!(context instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}
		mCallbacks = (Callbacks) context;
	}

    @Override
    public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}
}
