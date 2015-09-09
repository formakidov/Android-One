package com.formakidov.rssreader;

import android.os.AsyncTask;

import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.tools.RssParser;

import java.util.ArrayList;
import java.util.List;

public class RssDataTask extends AsyncTask<String, Void, List<RssItem>> {
	@Override
	protected List<RssItem> doInBackground(String... urls) {
		try {
			return new RssParser(urls[0]).getItems();
		} catch (Exception e) {
			return new ArrayList<RssItem>();
		}
	}
}
