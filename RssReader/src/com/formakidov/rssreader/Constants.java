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
	public static final int SITE_RESPONSE_TIMEOUT = 20 * SECOND;
	public static final String EXTRA_NEWS_INDEX = "com.formakidov.rssreader.NEWS_INDEX";
	public static final String EXTRA_FEED_URL = "com.formakidov.rssreader.FEED_URL";
	public static final String URL_VALIDATON_REGEX = "(http://|https://)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?";
	public static final String ERROR_INVALID_URL = "Invalid url";
	public static final String ERROR_NO_NAME = "Enter name of the rss feed";
	public static final String FEED_POSITION = "feed_position";
	public static final String FEED_UUID = "feed_uuid";
	public static final String FEED_NAME = "feed_name";
	public static final String FEED_URL = "feed_url";
	public static final String EMPTY_STRING = "";
	public static final String NO_NEWS = "No news.";
	public static final String ERROR_CHECK_NETWORK_ONNECTION = NO_NEWS + " Check your network connection.";
	public static final String ERROR_CHECK_URL = NO_NEWS + " Check RSS url.";
}