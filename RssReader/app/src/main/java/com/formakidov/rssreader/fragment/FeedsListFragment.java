package com.formakidov.rssreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.FeedDialog;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.activity.NewsListActivity;
import com.formakidov.rssreader.adapter.FeedAdapter;
import com.formakidov.rssreader.data.FeedItem;
import com.formakidov.rssreader.listeners.HidingScrollListener;
import com.formakidov.rssreader.listeners.SimpleAnimationListener;
import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.ItemClickSupport;
import com.formakidov.rssreader.tools.Tools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

public class FeedsListFragment extends Fragment implements Constants, FeedDialog.FeedDialogCallback {
	private FeedAdapter adapter;
	private FloatingActionButton fab;
	private RecyclerView recyclerView;
	private TextView errorMessage;
	private boolean isFabVisible = true;

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
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.feeds_list, parent, false);

		DatabaseManager manager = DatabaseManager.getInstance(getActivity());
		List<FeedItem> feeds = manager.getAllFeeds();
		adapter = new FeedAdapter(this, feeds);

		//TODO remove me before release!!!
		while (adapter.getItemCount() < 5) {
			adapter.addItem(new FeedItem("NU", "http://nu.nl/rss"));
			adapter.addItem(new FeedItem("NU Sport", "http://nu.nl/rss/sport"));
			adapter.addItem(new FeedItem("NU Tech", "http://nu.nl/rss/tech"));
			adapter.addItem(new FeedItem("onliner", "http://onliner.by/feed"));
			adapter.addItem(new FeedItem("itcuties", "http://itcuties.com/feed"));
		}

		setupViews(v);

		SharedPreferences prefs = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		boolean firstStart = prefs.getBoolean(FIRST_START, true);
		if (firstStart) {
			showAddFeedDialog();
			prefs.edit().putBoolean(FIRST_START, false).apply();
		}

		if (adapter.getItemCount() == 0) {
			changeErrorMessageVisibility(true);
		}

		return v;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int columns = getSpanCount(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
		((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(columns);
	}

	@SuppressWarnings("deprecation")
	private void setupViews(final View v) {
		recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
		recyclerView.setAdapter(adapter);
		boolean isLand = getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), getSpanCount(isLand));
		recyclerView.setLayoutManager(gridLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		errorMessage = (TextView) v.findViewById(R.id.error_message);

		fab = (FloatingActionButton) v.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				view.postDelayed(new Runnable() {
					@Override
					public void run() {
						showAddFeedDialog();
					}
				}, FAB_ANIMATION_DURATION);
			}
		});

		recyclerView.setOnScrollListener(new HidingScrollListener() {
			@Override
			public void onHide() {
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
				fab.animate().translationY(fab.getHeight() + lp.bottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
				isFabVisible = false;
			}

			@Override
			public void onShow() {
				fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
				isFabVisible = true;
			}
		});

		ItemClickSupport support = ItemClickSupport.addTo(recyclerView);
		support.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
			@Override
			public void onItemClicked(RecyclerView recyclerView, int position, View v) {
				Intent i = new Intent(getActivity(), NewsListActivity.class);
				FeedItem item = adapter.getItem(position);
				i.putExtra(EXTRA_FEED_URL, item.getUrl());
				i.putExtra(EXTRA_FEED_NAME, item.getName());
				startActivity(i);
				Tools.nextActivityAnimation(getActivity());
			}
		});
		support.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {
				showMenuDialog(position);
				return false;
			}
		});
	}

	private int getSpanCount(boolean isLand) {
		int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		switch(screenSize) {
			case Configuration.SCREENLAYOUT_SIZE_XLARGE:
				return isLand ? 3 : 2;
			case Configuration.SCREENLAYOUT_SIZE_LARGE:
				return isLand ? 2 : 1;
			default:
				return 1;
		}
	}

	private void showMenuDialog(final int position) {
		String[] values = new String[] { getString(R.string.edit), getString(R.string.delete)};
		ArrayAdapter<String> dialogAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, values);
		new MaterialDialog.Builder(getActivity())
				.adapter(dialogAdapter, new MaterialDialog.ListCallback() {
					@Override
					public void onSelection(MaterialDialog materialDialog, View view, int pos, CharSequence charSequence) {
						switch (pos) {
							case 0:
								showEditFeedDialog(position);
								break;
							case 1:
								adapter.deleteItem(position);
								if (adapter.getItemCount() == 0) {
									changeErrorMessageVisibility(true);
								}
								break;
						}
						materialDialog.dismiss();
					}
				})
				.build()
				.show();
	}

	private void showAddFeedDialog() {
		changeFabVisibility(true);
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		FeedDialog feedDialog = new FeedDialog(this);
		feedDialog.show(ft, getString(R.string.add_feed));
	}

	public void showEditFeedDialog(int position) {
		changeFabVisibility(true);
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		FeedItem feedItem = adapter.getItem(position);
		args.putInt(FEED_POSITION, position);
		args.putString(FEED_UUID, feedItem.getUUID());
		args.putString(FEED_NAME, feedItem.getName());
		args.putString(FEED_URL, feedItem.getUrl());
		FeedDialog feedDialog = new FeedDialog(this);
		feedDialog.setArguments(args);
		feedDialog.show(ft, getString(R.string.edit_feed));
	}

	private void changeFabVisibility(final boolean isHide) {
		if (!isFabVisible) return;
		Animation fabAnimation = AnimationUtils.loadAnimation(getContext(), isHide ? R.anim.design_fab_out : R.anim.design_fab_in);
		fabAnimation.setDuration(FAB_ANIMATION_DURATION);
		fab.startAnimation(fabAnimation);
		fabAnimation.setAnimationListener(new SimpleAnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				if (!isHide) {
					fab.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (isHide) {
					fab.setVisibility(View.GONE);
				}
			}
		});
	}

	public void changeErrorMessageVisibility(boolean isShow) {
		if (isShow) {
			errorMessage.setVisibility(View.VISIBLE);
		} else if (errorMessage.getVisibility() == View.VISIBLE){
			errorMessage.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onFeedChanged(int position, FeedItem changedItem) {
		changeFabVisibility(false);
		adapter.itemChanged(position, changedItem);
	}

	@Override
	public void onFeedCreated(FeedItem newItem) {
		changeFabVisibility(false);
		adapter.addItem(newItem);
		changeErrorMessageVisibility(false);
	}

	@Override
	public void onCancel() {
		changeFabVisibility(false);
	}
}
