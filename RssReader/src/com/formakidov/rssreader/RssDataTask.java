package com.formakidov.rssreader;

import java.util.ArrayList;
import java.util.List;

import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.interfaces.RssProgressListener;

import android.os.AsyncTask;

public class RssDataTask extends AsyncTask<String, Void, List<RssItem>> {
	public static List<RssItem> rssItems;
	private RssProgressListener listener;
	
	public RssDataTask(RssProgressListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected List<RssItem> doInBackground(String... urls) {
		try {
			listener.onProgressUpdate(0);
			rssItems = new RssParser(urls[0]).getItems(listener);
			return rssItems;
		} catch (Exception e) {
			return new ArrayList<RssItem>();
		}
	}
}
