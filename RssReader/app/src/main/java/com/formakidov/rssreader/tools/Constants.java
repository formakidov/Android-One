package com.formakidov.rssreader.tools;

import java.text.SimpleDateFormat;

public interface Constants {
	String ERROR_BAD_RSS = "Can not parse this rss";
	String REGEX_DELETE_TAGS = "(<(/?)[a-zA-Z0-9][^>]*>)";
	String REGEX_GET_LINK = "\\s*(?i)\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";

	SimpleDateFormat dateFormatPubDate = new SimpleDateFormat("d MMM");
	String SHOW = "Show website!";
	String HIDE = "Hide website!";
	
	String EXTRA_NEWS_INDEX = "com.formakidov.rssreader.NEWS_INDEX";
	String EXTRA_NEWS_UUID = "com.formakidov.rssreader.NEWS_UUID";
	String EXTRA_FEED_URL = "com.formakidov.rssreader.FEED_URL";
	String URL_VALIDATON_REGEX = "(http://|https://)([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?";
	String FEED_POSITION = "feed_position";
	String FEED_UUID = "feed_uuid";
	String FEED_NAME = "feed_name";
	String FEED_URL = "feed_url";
	String EMPTY_STRING = "";
	String NO_NEWS = "No news.";
	String ERROR_CHECK_NETWORK_CONNECTION = NO_NEWS + " Check your network connection.";
	String ERROR_CHECK_URL = NO_NEWS + " Check RSS url.";
	int FAB_ANIMATION_DURATION = 200;
}