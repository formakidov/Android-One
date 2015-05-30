package com.formakidov.rssreader.fragment;

import java.text.ParseException;
import java.util.Date;

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
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class NewsFragment extends Fragment implements Constants {
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);	
		setRetainInstance(true);
		this.news = RssDataTask.rssItems.get(getArguments().getInt(EXTRA_NEWS_INDEX));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_news, parent, false);

		ActionBar mainActionBar = getActivity().getActionBar();
		mainActionBar.setDisplayHomeAsUpEnabled(true);
		mainActionBar.setHomeButtonEnabled(true);
		mainActionBar.setDisplayShowTitleEnabled(false);
		
		picture = (ImageView) v.findViewById(R.id.picture);
		Tools.imageLoader.loadImage(news.getImageUrl(), new ImageLoadingListener() {			
			@Override
			public void onLoadingStarted(String arg0, View arg1) { }			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				picture.setImageResource(R.drawable.no_image);
			}
			@Override
			public void onLoadingCancelled(String arg0, View arg1) { }
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
		link.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(news.getLink()));
				startActivity(i);
			}
		});
		
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
		
		final FrameLayout webViewLayout = (FrameLayout) v.findViewById(R.id.webview_layout);
		Button btnBack = (Button) webViewLayout.findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (webView.canGoBack()) {
					webView.goBack();
				}
			}
		});
		Button btnForward = (Button) webViewLayout.findViewById(R.id.btn_forward);
		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (webView.canGoForward()) {
					webView.goForward();
				}
			}
		});
		
		progress = (ProgressBar) v.findViewById(R.id.webview_progress);
		errorMessage = (TextView) v.findViewById(R.id.error_message);
		switchBtn = (Button) v.findViewById(R.id.btn_show_hide);
		switchBtn.setText(SHOW);
		switchBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) { 
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
			}
		});
		
		return v;
	}
	
	private void changeProgressVisibility(boolean vis) {
		progress.setVisibility(vis ? View.VISIBLE : View.GONE);
	}
	
	private void changeErrorMessageVisibility(boolean vis) {
		errorMessage.setVisibility(vis ? View.VISIBLE : View.GONE);
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
	
	private class WebClient extends WebViewClient {
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			siteIsLoaded = true;
			changeProgressVisibility(false);
			changeErrorMessageVisibility(false);
		}
	}
	
	public static NewsFragment newInstance(int index) {
		NewsFragment fragment = new NewsFragment();
		Bundle args = new Bundle();
		args.putInt(EXTRA_NEWS_INDEX, index);
		fragment.setArguments(args);
		return fragment;
	}
}
