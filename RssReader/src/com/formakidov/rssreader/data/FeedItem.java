package com.formakidov.rssreader.data;

import com.formakidov.rssreader.Tools;

public class FeedItem {
	private String name;
	private String url;
	private String uuid;

	public FeedItem(String name, String url) {
		this.uuid = Tools.getRandomUUID();
		this.name = name;
		this.url = url;
	}

	public FeedItem(String uuid, String name, String url) {
		this.uuid = uuid;
		this.name = name;
		this.url = url;
	}

	public String getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
