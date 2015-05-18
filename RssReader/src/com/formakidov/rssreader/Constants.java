package com.formakidov.rssreader;

import java.text.SimpleDateFormat;

public interface Constants {
	public static final String ERROR_BAD_RSS = "Can not parse this rss";
	public static final String REGEX_DELETE_TAGS = "(<(/?)[a-zA-Z0-9][^>]*>)";
	public static final String REGEX_GET_LINK = "\\s*(?i)\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	
	public static final SimpleDateFormat format24 = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat dateFormatPubDate = new SimpleDateFormat("dd/MM/yyyy");
	public static final String SHOW = "Show website!";
	public static final String HIDE = "Hide website!";
	
	public static final int SECOND = 1000;
	public static final String EXTRA_NEWS_INDEX = "com.formakidov.rssreader.news_index";
	public static final String EXTRA_FEED_URL = "com.formakidov.rssreader.feed_url";
	public static final String URL_VALIDATON_REGEX = "(http://|https://)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?";
	public static final String INVALID_URL = "Invalid url";
}