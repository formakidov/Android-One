package com.formakidov.rssreader.tools;

import java.text.SimpleDateFormat;

public interface Constants {
	String REGEX_DELETE_TAGS = "(<(/?)[a-zA-Z0-9][^>]*>)";
	String REGEX_GET_LINK = "\\s*(?i)\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";

	SimpleDateFormat dateFormatPubDate = new SimpleDateFormat("d MMM");
	
	String EXTRA_NEWS_INDEX = "com.formakidov.rssreader.NEWS_INDEX";
	String EXTRA_NEWS_UUID = "com.formakidov.rssreader.NEWS_UUID";
	String EXTRA_FEED_URL = "com.formakidov.rssreader.FEED_URL";
	String URL_VALIDATON_REGEX = "(http://|https://)(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?";
	String FEED_POSITION = "feed_position";
	String FEED_UUID = "feed_uuid";
	String FEED_NAME = "feed_name";
	String FEED_URL = "feed_url";
	String EMPTY_STRING = "";
	int FAB_ANIMATION_DURATION = 200;
	int CHANGE_VIEW_ANIMATION_DURATION = 250;
}