package com.formakidov.rssreader.fragment;

import java.text.ParseException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.formakidov.rssreader.Constants;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.RssDataTask;
import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.tools.Tools;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class NewsDetailFragment extends Fragment implements Constants, OnClickListener {
	private RssItem news;
	private ImageView picture;
	private TextView title;
	private TextView pubDate;
	private TextView link;
	private TextView description;
	private WebView webView;
	private Button switchBtn;
	private boolean isWebViewVisible = false;
	private boolean siteIsLoaded = false;
	private ProgressBar progress;
	private TextView errorMessage;
	private FrameLayout webViewLayout;

    public NewsDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);	
		setRetainInstance(true);
        if (getArguments().containsKey(EXTRA_NEWS_INDEX)) {
        	this.news = RssDataTask.rssItems.get(getArguments().getInt(EXTRA_NEWS_INDEX));
        }
    }

    @SuppressLint("SetJavaScriptEnabled") 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.fragment_news, container, false);

		ActionBar mainActionBar = getActivity().getActionBar();
		mainActionBar.setDisplayHomeAsUpEnabled(true);
		mainActionBar.setHomeButtonEnabled(true);
		if (news.hasDefTitle()) {
			mainActionBar.setTitle(news.getDefTitle());
		}
		
		picture = (ImageView) v.findViewById(R.id.picture);
		Tools.imageLoader.loadImage(news.getImageUrl(), new SimpleImageLoadingListener() {	
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				picture.setImageResource(R.drawable.no_image);
			}
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap b) {
				if (null != b) {
					picture.setImageBitmap(b);
				}
			}
		});
		title = (TextView) v.findViewById(R.id.title);
		title.setText(news.getTitle());
		pubDate = (TextView) v.findViewById(R.id.pubdate);
		String pub = news.getPubDate();				
		try {
			Date date = Tools.RFC822_DATE_FORMAT.parse(pub);
			pub = dateFormatPubDate.format(date) + " " + format24.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		pubDate.setText(pub);
		
		link = (TextView) v.findViewById(R.id.link);
		link.setText(news.getLink());
		link.setOnClickListener(this);
		
		description = (TextView) v.findViewById(R.id.description);
		description.setText(news.getDescription());
		
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
		
		progress = (ProgressBar) v.findViewById(R.id.webview_progress);
		errorMessage = (TextView) v.findViewById(R.id.error_message);
		switchBtn = (Button) v.findViewById(R.id.btn_show_hide);
		switchBtn.setText(SHOW);
		switchBtn.setOnClickListener(this);
		
		return v;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			webView.stopLoading();
			getActivity().finish();
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
					webView.loadUrl(news.getLink());
					changeProgressVisibility(true);
					webView.postDelayed(new Runnable() {	
						
						@Override
						public void run() {
							if (!siteIsLoaded) {
								changeProgressVisibility(false);
								changeErrorMessageVisibility(true);
							}
						}
					}, SITE_RESPONSE_TIMEOUT);
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
    
	private void changeProgressVisibility(boolean vis) {
		progress.setVisibility(vis ? View.VISIBLE : View.GONE);
	}
	
	private void changeErrorMessageVisibility(boolean vis) {
		errorMessage.setVisibility(vis ? View.VISIBLE : View.GONE);
	}
	
	private class WebClient extends WebViewClient {
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			siteIsLoaded = true;
			changeProgressVisibility(false);
			changeErrorMessageVisibility(false);
		}
	}
}
