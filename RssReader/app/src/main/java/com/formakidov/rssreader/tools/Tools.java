package com.formakidov.rssreader.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;

import com.formakidov.rssreader.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools implements Constants {
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
	
	public static void prepareTools(Context context, ImageLoaderConfiguration config) {
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
	
	public static boolean validateUrl(String url) {
		//TODO proper validation
		Pattern p = Pattern.compile(Constants.URL_VALIDATON_REGEX);
	    Matcher m = p.matcher(url);
		return m.matches();
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, pixels, pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
	
	public static Bitmap getRoundedCornerBitmap(Bitmap input, int pixels , int w , int h , 
			boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR) {
	    Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, w, h);
	    final RectF rectF = new RectF(rect);

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, pixels, pixels, paint);

	    if (squareTL){
	        canvas.drawRect(0, 0, w/2, h/2, paint);
	    }
	    if (squareTR){
	        canvas.drawRect(w/2, 0, w, h/2, paint);
	    }
	    if (squareBL){
	        canvas.drawRect(0, h/2, w/2, h, paint);
	    }
	    if (squareBR){
	        canvas.drawRect(w/2, h/2, w, h, paint);
	    }

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(input, 0, 0, paint);

	    return output;
	}	
}