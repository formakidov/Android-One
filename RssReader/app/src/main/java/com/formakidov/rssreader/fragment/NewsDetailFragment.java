package com.formakidov.rssreader.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.Tools;
import com.formakidov.rssreader.view.CircleImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class NewsDetailFragment extends Fragment implements Constants, OnClickListener {
	private RssItem news;
	private CircleImageView picture;
	private TextView title;
	private TextView pubDate;
	private TextView link;
	private TextView description;
	private WebView webView;
	private Button switchBtn;
	private FrameLayout webViewLayout;
	private SwipeRefreshLayout swipeRefreshLayout;
	private boolean siteIsLoaded = false;
	private boolean isWebViewVisible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);	
		Log.d("loglc", "Details: onCreate");
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

		if (null == news) {
			return null;
		}
		
		picture = (CircleImageView) v.findViewById(R.id.picture);
		Tools.imageLoader.loadImage(news.getImageUrl(), new SimpleImageLoadingListener() {	
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				picture.setImageResource(R.drawable.no_image);
			}
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap b) {
				if (null != b) {
					picture.setImageBitmap(b);
				} else {
					picture.setImageResource(R.drawable.no_image);
				}
			}
		});
		title = (TextView) v.findViewById(R.id.title);
		title.setText(news.getTitle());
		pubDate = (TextView) v.findViewById(R.id.pubdate);
		pubDate.setText(news.getPubDate());
		
		link = (TextView) v.findViewById(R.id.link);
		link.setText(news.getLink());
		link.setOnClickListener(this);
		
		description = (TextView) v.findViewById(R.id.description);
		description.setText(news.getDescription());
		
		swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setColorSchemeResources(
	    		android.R.color.holo_green_light,
	            android.R.color.holo_red_light,
	            android.R.color.holo_blue_light,
	            android.R.color.holo_orange_light);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

	    	@Override
	    	public void onRefresh() {
	    		loadWebsite();
	    	}
		});
		
		webView = (WebView) v.findViewById(R.id.webview);
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		webView.setInitialScale(1);
		webView.setWebViewClient(new WebClient());
		webView.setVerticalScrollBarEnabled(true);
		webView.setHorizontalScrollBarEnabled(true);
		WebSettings settings = webView.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		settings.setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
		settings.setJavaScriptEnabled(true);
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);
		
		webViewLayout = (FrameLayout) v.findViewById(R.id.webview_layout);
		Button btnBack = (Button) webViewLayout.findViewById(R.id.btn_back);
		btnBack.setOnClickListener(this);
		Button btnForward = (Button) webViewLayout.findViewById(R.id.btn_forward);
		btnForward.setOnClickListener(this);
		
		switchBtn = (Button) v.findViewById(R.id.btn_show_hide);
		switchBtn.setText(SHOW);
		switchBtn.setOnClickListener(this);
		
		return v;
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
			webView.stopLoading();
			getActivity().onBackPressed();
			return true;
		case R.id.share:
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, news.getTitle() + "\n"+ news.getLink());
			shareIntent.setType("text/plain");
			startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_news)));
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.link:
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(news.getLink()));
			startActivity(i);
			break;
		case R.id.btn_back:
			if (webView.canGoBack()) {
				webView.goBack();
			}
			break;
		case R.id.btn_forward:
			if (webView.canGoForward()) {
				webView.goForward();
			}
			break;
		case R.id.btn_show_hide:
			if (isWebViewVisible) {
				isWebViewVisible = false;
				webViewLayout.setVisibility(View.INVISIBLE);
				picture.setVisibility(View.VISIBLE);
				title.setVisibility(View.VISIBLE);
				link.setVisibility(View.VISIBLE);
				pubDate.setVisibility(View.VISIBLE);
				description.setVisibility(View.VISIBLE);
				switchBtn.setText(SHOW);
			} else {
				isWebViewVisible = true;
				if (!siteIsLoaded) {
					loadWebsite();
				}
				webViewLayout.setVisibility(View.VISIBLE);
				picture.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				link.setVisibility(View.GONE);
				pubDate.setVisibility(View.GONE);
				description.setVisibility(View.GONE);
				switchBtn.setText(HIDE);
			}
			break;
		default:
			break;
		}
	}

	private void loadWebsite() {
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
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			setRefreshing(false);
			siteIsLoaded = true;
		}
	}
}
