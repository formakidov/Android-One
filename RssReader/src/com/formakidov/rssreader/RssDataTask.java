package com.formakidov.rssreader;

import java.util.ArrayList;
import java.util.List;

import com.formakidov.rssreader.data.RssItem;

import android.os.AsyncTask;

public class RssDataTask extends AsyncTask<String, Void, List<RssItem>> {
	public static List<RssItem> rssItems;
	
	@Override
	protected List<RssItem> doInBackground(String... urls) {
		try {
			rssItems = new RssParser(urls[0]).getItems();
			return rssItems;
		} catch (Exception e) {
			return new ArrayList<RssItem>();
		}
	}
}
