package com.formakidov.rssreader;

public class FeedItem {
	private String name;
	private String url;
	
	public FeedItem(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
