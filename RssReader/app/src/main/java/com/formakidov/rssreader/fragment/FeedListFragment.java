package com.formakidov.rssreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.FeedAdapter;
import com.formakidov.rssreader.FeedDialog;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.activity.FeedListActivity;
import com.formakidov.rssreader.activity.NewsListActivity;
import com.formakidov.rssreader.data.FeedItem;
import com.formakidov.rssreader.listeners.HidingScrollListener;
import com.formakidov.rssreader.listeners.SimpleAnimationListener;
import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.ItemClickSupport;
import com.formakidov.rssreader.tools.Tools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

public class FeedListFragment extends Fragment implements Constants, FeedDialog.FeedDialogCallback {
	private FeedAdapter adapter;
	private FloatingActionButton fab;
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
			adapter.addItem(new FeedItem("onliner(" + adapter.getItemCount() + ")", "http://onliner.by/feed"));
		}

		setupViews(v);

		return v;
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

	@SuppressWarnings("deprecation")
	private void setupViews(final View v) {
		final Toolbar toolbar = (Toolbar) v.findViewById(R.id.tool_bar);
		((FeedListActivity) getActivity()).setSupportActionBar(toolbar);

		RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setItemAnimator(new DefaultItemAnimator());

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
				//TODO
//				toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator(2)).start();
			}

			@Override
			public void onShow() {
				fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
				isFabVisible = true;
				//TODO
//				toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
			}
		});

		ItemClickSupport support = ItemClickSupport.addTo(recyclerView);
		support.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
			@Override
			public void onItemClicked(RecyclerView recyclerView, int position, View v) {
				Intent i = new Intent(getActivity(), NewsListActivity.class);
				i.putExtra(EXTRA_FEED_URL, adapter.getItem(position).getUrl());
				startActivity(i);
				Tools.nextActivityAnimation(getActivity());
			}
		});
		support.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {
				//TODO dialog with delete/edit btns
//				showEditFeedDialog(position);
//				adapter.deleteItem(position);
				return false;
			}
		});
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

	@Override
	public void onFeedChanged(int position, FeedItem changedItem) {
		changeFabVisibility(false);
		adapter.itemChanged(position, changedItem);
	}

	@Override
	public void onFeedCreated(FeedItem newItem) {
		changeFabVisibility(false);
		adapter.addItem(newItem);
	}

	@Override
	public void onCancel() {
		changeFabVisibility(false);
	}
}
