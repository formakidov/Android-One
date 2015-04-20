package com.formakidov.rssreader.fragment;

import java.text.ParseException;
import java.util.Date;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.formakidov.rssreader.Constants;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.RssDataTask;
import com.formakidov.rssreader.RssItem;
import com.formakidov.rssreader.Tools;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class NewsFragment extends Fragment implements Constants {
	public static final String EXTRA_NEWS_INDEX = "com.formakidov.rssreader.news_index";
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);			
		this.news = RssDataTask.rssItems.get(getArguments().getInt(EXTRA_NEWS_INDEX));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_news, parent, false);

		picture = (ImageView) v.findViewById(R.id.picture);
		Tools.imageLoader.loadImage(news.getImageUrl(), new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) { }
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) { }

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
		description = (TextView) v.findViewById(R.id.description);
		description.setText(news.getDescription());
		
		webView = (WebView) v.findViewById(R.id.webview);
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);		
//		webView.setBackgroundResource(R.color.bg_black); TODO
		webView.setWebViewClient(new WebClient());
		webView.setVerticalScrollBarEnabled(true);
		webView.setHorizontalScrollBarEnabled(true);
		webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.setInitialScale(50);
		
		switchBtn = (Button) v.findViewById(R.id.btn_show_hide);
		switchBtn.setText(SHOW);
		switchBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) { 
				if (isWebViewVisible) {
					isWebViewVisible = false;
					webView.setVisibility(View.INVISIBLE);
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
						webView.postDelayed(new Runnable() {	
							
							@Override
							public void run() {
								if (!siteIsLoaded) {
									//TODO show error
								}
							}
						}, 15 * SECOND);
					}
					webView.setVisibility(View.VISIBLE);
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
	
	private class WebClient extends WebViewClient {
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			siteIsLoaded = true;
			//TODO hide error
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
