package com.formakidov.rssreader.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
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
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.RssDataTask;
import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.tools.Tools;

public class NewsListFragment extends Fragment implements OnRefreshListener, Constants {
    private Callbacks mCallbacks = sDummyCallbacks;
	private RssDataTask rssDataTask;
	private NewsAdapter adapter;
	private String url;
	private SwipeRefreshLayout swipeRefreshLayout;
	private ListView listView;
	private TextView errorMessage;	

    public interface Callbacks {
        public void onItemSelected(int index);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int index) {
        }
    };

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

	    errorMessage = (TextView) view.findViewById(R.id.error_message);
	    
	    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
	    swipeRefreshLayout.setOnRefreshListener(this);
	    swipeRefreshLayout.setColorSchemeResources(
	    		android.R.color.holo_green_light,
	            android.R.color.holo_red_light,
	            android.R.color.holo_blue_light,
	            android.R.color.holo_orange_light);
	    
		listView = (ListView)view.findViewById(R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        mCallbacks.onItemSelected(position);
		        //TODO set background on activated item 
				if (null != listView) {
					listView.setItemChecked(position, true);
				}
		        
		        //TODO ??
//				Intent i = new Intent(getActivity(), NewsPagerActivity.class);
//				i.putExtra(NewsFragment.EXTRA_NEWS_INDEX, position);
//				startActivity(i);
			}
		});
		
		adapter = new NewsAdapter(new ArrayList<RssItem>());
		listView.setAdapter(adapter);
		
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
			cancelTask();
			getActivity().finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
				if (null == getActivity()) return;
				if (result.size() > 0) {
					hideErrorMessage();
					updateNews(result);
				} else if (null == adapter || adapter.getCount() == 0) {
					if (!Tools.isNetworkAvailable(getActivity())) {
						showErrorMessage(ERROR_CHECK_NETWORK_ONNECTION);
					} else {
						//TODO: can't load rss feed
						showErrorMessage(ERROR_CHECK_URL);
					}
				}
				swipeRefreshLayout.post(new Runnable() {
					@Override
					public void run() {
						swipeRefreshLayout.setRefreshing(false);
					}
				});	
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
		adapter.clear();
		adapter.addAll(result);
	}

	@Override
	public void onRefresh() {
		executeTask();
	}
	
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }
    
    private class NewsAdapter extends ArrayAdapter<RssItem> {
		public NewsAdapter(ArrayList<RssItem> items) {
			super(getActivity(), 0, items);
		}

		@SuppressLint("InflateParams") 
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
        
        public ViewHolder(View view) {
        	title = (TextView) view.findViewById(R.id.title);
        	pubDate = (TextView) view.findViewById(R.id.pubDate);
        }
    }
}
