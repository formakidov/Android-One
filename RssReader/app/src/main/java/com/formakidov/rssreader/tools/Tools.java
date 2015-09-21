package com.formakidov.rssreader.tools;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import com.formakidov.rssreader.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
	public static final ImageLoader imageLoader = ImageLoader.getInstance();
	
	public static void previousActivityAnimation(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
	}

	public static void nextActivityAnimation(Activity activity) {
		activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
	}
	
	public static void prepareTools(ImageLoaderConfiguration config) {
		imageLoader.init(config);
		Constants.RFC822_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
	}
	
	public static boolean isNetworkAvailable(Context context) {
		return ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo() != null;
	}
	
	public static String getRandomUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static boolean isValidUrl(String url) {
		Pattern p = Pattern.compile(Constants.URL_VALIDATION_REGEX);
	    Matcher m = p.matcher(url);
		return m.matches();
	}
}