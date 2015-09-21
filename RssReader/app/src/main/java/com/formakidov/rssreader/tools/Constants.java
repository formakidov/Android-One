package com.formakidov.rssreader.tools;

import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Constants {
	String REGEX_DELETE_TAGS = "(<(/?)[a-zA-Z0-9][^>]*>)";
	String REGEX_GET_LINK = "\\s*(?i)\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	String URL_VALIDATION_REGEX = "(http://)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?";

	SimpleDateFormat dateFormatShort = new SimpleDateFormat("d MMM");
	SimpleDateFormat dateFormatFull = new SimpleDateFormat("d MMMM HH:mm");
	SimpleDateFormat RFC822_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

	String APP_PREFERENCES = "prefs_rss_reader";
	String EXTRA_NEWS_UUID = "com.formakidov.rssreader.NEWS_UUID";
	String EXTRA_FEED_URL = "com.formakidov.rssreader.FEED_URL";
	String EXTRA_FEED_NAME = "com.formakidov.rssreader.FEED_NAME";
	String FIRST_START = "com.formakidov.rssreader.FIRST_START";
	String WEBVIEW_VISIBILITY = "com.formakidov.rssreader.webview_visibility";
	String SHARE_VISIBILITY = "com.formakidov.rssreader.share_visibility";

	String FEED_POSITION = "feed_position";
	String FEED_UUID = "feed_uuid";
	String FEED_NAME = "feed_name";
	String FEED_URL = "feed_url";

	String EMPTY_STRING = "";

	int FAB_ANIMATION_DURATION = 200;
	int CHANGE_VIEW_ANIMATION_DURATION = 250;
}