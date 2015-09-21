package com.formakidov.rssreader.tools;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;

import com.cocosw.bottomsheet.BottomSheet;
import com.formakidov.rssreader.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
	public static final ImageLoader imageLoader = ImageLoader.getInstance();
	public static final SimpleDateFormat RFC822_DATE_FORMAT = 
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);	
	
	public static void previousActivityAnimation(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
	}

	public static void nextActivityAnimation(Activity activity) {
		activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
	}
	
	public static void prepareTools(ImageLoaderConfiguration config) {
		imageLoader.init(config);
		RFC822_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
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
		Pattern p = Pattern.compile(Constants.URL_VALIDATON_REGEX);
	    Matcher m = p.matcher(url);
		return m.matches();
	}

	public static BottomSheet.Builder shareAction(@NonNull final Activity activity, @NonNull final Intent intent) {
		BottomSheet.Builder builder = new BottomSheet.Builder(activity).grid();
		PackageManager pm = activity.getPackageManager();

		final List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);

		for (int i = 0; i < list.size(); i++) {
			builder.sheet(i, list.get(i).loadIcon(pm), list.get(i).loadLabel(pm));
		}

		builder.listener(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(@NonNull DialogInterface dialog, int which) {
				ActivityInfo activityInfo = list.get(which).activityInfo;
				ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
				Intent newIntent = (Intent) intent.clone();
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				newIntent.setComponent(name);
				activity.startActivity(newIntent);
			}
		});
		builder.limit(R.integer.bs_initial_grid_row);
		return builder;
	}
}