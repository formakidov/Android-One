package com.formakidov.rssreader.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.Tools;
import com.formakidov.rssreader.view.CircleImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import uk.co.deanwild.flowtextview.FlowTextView;

public class NewsDetailsFragment extends Fragment implements Constants, OnClickListener {
	private RssItem news;
	private CircleImageView picture;
	private WebView webView;
	private TextView btnOpenHideTv;
	private SwipeRefreshLayout swipeRefreshLayout;
	private boolean isWebViewVisible = false;
	private FlowTextView content;
	private boolean startLoading;
	private FrameLayout webViewLayout;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    	DatabaseManager manager = DatabaseManager.getInstance(getActivity());
        if (getArguments().containsKey(EXTRA_NEWS_UUID)) {
        	this.news = manager.getNews(getArguments().getString(EXTRA_NEWS_UUID));
        } else {
        	this.news = manager.getNews(getActivity().getIntent().getStringExtra(EXTRA_NEWS_UUID));
        }
    }

	@SuppressLint("SetJavaScriptEnabled") 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.fragment_news, container, false);
		if (null == news) return null;
		setupViews(v);
		
		return v;
    }

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
				if (null != b) {
					picture.setImageBitmap(b);
				} else {
					picture.setImageResource(R.drawable.no_image);
				}
				progress.setVisibility(View.GONE);
				picture.setVisibility(View.VISIBLE);
			}
		});
		content = (FlowTextView) v.findViewById(R.id.flow_tv);
		String title = news.getTitle();
		String pubdate = news.getFormattedPubDate();
		String description = news.getDescription();
		Spannable text = new SpannableString(title + " (" + pubdate + ")\n\n" + description);
		text.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		content.setText(text);

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

		v.findViewById(R.id.card_btn_show).setOnClickListener(this);
		v.findViewById(R.id.card_btn_browse).setOnClickListener(this);
		btnOpenHideTv = (TextView) v.findViewById(R.id.text_btn_show);
		btnOpenHideTv.setText(getString(R.string.open_here));

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
			case R.id.share:
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, news.getTitle() + "\n" + news.getLink());
				Tools.shareAction(getActivity(), shareIntent).build().show();
				return true;
			default:
				return false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.card_btn_show:
				changeWebviewVisibility();
				break;
			case R.id.card_btn_browse:
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(news.getLink()));
				startActivity(i);
				break;
			default:
				break;
		}
	}

	private void changeWebviewVisibility() {
		webViewLayout.setVisibility(isWebViewVisible ? View.GONE : View.VISIBLE);
		picture.setVisibility(isWebViewVisible ? View.VISIBLE : View.GONE);
		content.setVisibility(isWebViewVisible ? View.VISIBLE : View.GONE);
		btnOpenHideTv.setText(getString(isWebViewVisible ? R.string.open_here : R.string.hide));
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
