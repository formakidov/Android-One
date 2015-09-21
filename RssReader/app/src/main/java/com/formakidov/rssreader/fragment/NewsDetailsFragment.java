package com.formakidov.rssreader.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.cocosw.bottomsheet.BottomSheet;
import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.listeners.SimpleImageLoadingListener;
import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.Tools;
import com.formakidov.rssreader.view.CircleImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;

import java.util.List;

import uk.co.deanwild.flowtextview.FlowTextView;

public class NewsDetailsFragment extends Fragment implements Constants {
	private RssItem news;
	private CircleImageView picture;
	private WebView webView;
	private SwipeRefreshLayout swipeRefreshLayout;
	private boolean isWebViewVisible = false;
	private boolean shareBottomSheetVisibility = false;
	private boolean startLoading;
	private FrameLayout webViewLayout;
	private ScrollView scrollView;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    	DatabaseManager manager = DatabaseManager.getInstance(getActivity());
		this.news = manager.getNews(getArguments().containsKey(EXTRA_NEWS_UUID) ?
				getArguments().getString(EXTRA_NEWS_UUID) :
				getActivity().getIntent().getStringExtra(EXTRA_NEWS_UUID));
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.fragment_news, container, false);
		if (null == news) return null;
		setupViews(v);
		
		return v;
    }

	@SuppressLint("SetJavaScriptEnabled")
	private void setupViews(final View v) {
		picture = (CircleImageView) v.findViewById(R.id.picture);
		final ProgressBar progress = (ProgressBar) v.findViewById(R.id.progress);
		Tools.imageLoader.loadImage(news.getImageUrl(), new SimpleImageLoadingListener() {
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				picture.setImageResource(R.drawable.no_image);
				progress.setVisibility(View.GONE);
				picture.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap b) {
				if (null == b) {
					picture.setImageResource(R.drawable.no_image);
				} else {
					picture.setImageBitmap(b);
				}
				progress.setVisibility(View.GONE);
				picture.setVisibility(View.VISIBLE);
			}
		});
		FlowTextView content = (FlowTextView) v.findViewById(R.id.flow_tv);
		content.setText(news.getTitle() + "\n\n" + news.getDescription() + "\n" +
				"(" + news.getFullFormattedPubDate() + ")");

		swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_green_light,
				android.R.color.holo_red_light,
				android.R.color.holo_blue_light,
				android.R.color.holo_orange_light);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (Tools.isNetworkAvailable(getContext())) {
					load();
				} else {
					Snackbar.make(v, R.string.error_check_network_connection, Snackbar.LENGTH_LONG).show();
					setRefreshing(false);
				}
			}
		});

		webView = (WebView) v.findViewById(R.id.webview);
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		webView.setWebViewClient(new WebClient());
		webView.setVerticalScrollBarEnabled(true);
		webView.setHorizontalScrollBarEnabled(true);
		WebSettings settings = webView.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		settings.setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
		settings.setJavaScriptEnabled(true);

		v.findViewById(R.id.card_btn_open_link).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeWebviewVisibility();
			}
		});

		scrollView = (ScrollView) v.findViewById(R.id.scrollView);
		webViewLayout = (FrameLayout) v.findViewById(R.id.webview_layout);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.news, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (isWebViewVisible) {
					changeWebviewVisibility();
					return true;
				} else {
					getActivity().onBackPressed();
					return true;
				}
			case R.id.share:
				share();
				return true;
			case R.id.browse:
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(news.getLink()));
				startActivity(i);
				return true;
			default:
				return false;
		}
	}

	public void share() {
		shareBottomSheetVisibility = true;
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, news.getTitle() + "\n" + news.getLink());

		final Activity activity = getActivity();
		BottomSheet.Builder builder = new BottomSheet.Builder(activity).grid();
		PackageManager pm = activity.getPackageManager();

		final List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);

		for (int i = 0; i < list.size(); i++) {
			builder.sheet(i, list.get(i).loadIcon(pm), list.get(i).loadLabel(pm));
		}

		builder.listener(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(@NonNull DialogInterface dialog, int which) {
				ActivityInfo activityInfo = list.get(which).activityInfo;
				ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
				Intent newIntent = (Intent) intent.clone();
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				newIntent.setComponent(name);
				activity.startActivity(newIntent);
				shareBottomSheetVisibility = false;
			}
		});
		builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				shareBottomSheetVisibility = false;
			}
		});
		builder.limit(R.integer.bs_initial_grid_row);
		builder.build().show();
	}

	private void changeWebviewVisibility() {
		webViewLayout.setVisibility(isWebViewVisible ? View.GONE : View.VISIBLE);
		scrollView.setVisibility(isWebViewVisible ? View.VISIBLE : View.GONE);
		if (!isWebViewVisible && !startLoading) {
			startLoading = true;
			load();
		}
		isWebViewVisible = !isWebViewVisible;
	}

	private void load() {
		webView.loadUrl(news.getLink());
		setRefreshing(true);
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
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(WEBVIEW_VISIBILITY, isWebViewVisible);
		outState.putBoolean(SHARE_VISIBILITY, shareBottomSheetVisibility);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (null == savedInstanceState) return;
		if (savedInstanceState.getBoolean(WEBVIEW_VISIBILITY, false)) {
			changeWebviewVisibility();
		}
		if (savedInstanceState.getBoolean(SHARE_VISIBILITY, false)) {
			share();
		}
		super.onViewCreated(view, savedInstanceState);
	}

	private class WebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return true;
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			setRefreshing(false);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			Class.forName("android.webkit.WebView").getMethod("onPause", (Class[]) null).invoke(webView, (Object[]) null);
		} catch (Throwable e) {
			webView.stopLoading();
			webView.pauseTimers();
			webView.clearCache(false);
			webView.destroy();
		}
	}
}
