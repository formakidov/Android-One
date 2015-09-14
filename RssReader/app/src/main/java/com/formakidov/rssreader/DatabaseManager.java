package com.formakidov.rssreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.formakidov.rssreader.data.FeedItem;
import com.formakidov.rssreader.data.RssItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {	
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "rssreader.db";
	
	private static final String TABLE_NAME_FEEDS = "feeds";
	private static final String TABLE_NAME_NEWS = "news";
	
	private static final int COLUMN_ID_NUMBER = 0;
	private static final int COLUMN_UUID_NUMBER = 1;
	private static final int COLUMN_NAME_NUMBER = 2;
	private static final int COLUMN_URL_NUMBER = 3;
	
	private static final int COLUMN_IS_SAVED_NUMBER = 2;
	private static final int COLUMN_TITLE_NUMBER = 3;
	private static final int COLUMN_DESCRIPTION_NUMBER = 4;
	private static final int COLUMN_IMAGE_URL_NUMBER = 5;
	private static final int COLUMN_LINK_NUMBER = 6;
	private static final int COLUMN_PUBDATE_NUMBER = 7;
	private static final int COLUMN_DEF_TITLE_NUMBER = 8;
	private static final int COLUMN_DEF_DESCRIPTION_NUMBER = 9;
	private static final int COLUMN_DEF_IMAGE_URL_NUMBER = 10;
	private static final int COLUMN_DEF_LINK_NUMBER = 11;
	private static final int COLUMN_RSS_URL_NUMBER = 12;
	
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_UUID = "uuid";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_URL = "url";	
	
	private static final String COLUMN_IS_SAVED = "is_saved";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_IMAGE_URL = "image_url";
	private static final String COLUMN_LINK = "link";
	private static final String COLUMN_PUBDATE = "pubdate";
	private static final String COLUMN_DEF_TITLE = "def_title";
	private static final String COLUMN_DEF_DESCRIPTION = "def_description";
	private static final String COLUMN_DEF_IMAGE_URL = "def_image_url";
	private static final String COLUMN_DEF_LINK = "def_link";
	private static final String COLUMN_RSS_URL = "rss_url";

	private static final String DB_CREATE_TABLE_FEEDS = "CREATE TABLE " + TABLE_NAME_FEEDS + " (" + 
													COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
													COLUMN_UUID + " TEXT NOT NULL, " +
													COLUMN_NAME + " TEXT NOT NULL, " +
													COLUMN_URL + " TEXT NOT NULL);";
	
	private static final String DB_CREATE_TABLE_NEWS = "CREATE TABLE " + TABLE_NAME_NEWS + " (" + 
													COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
													COLUMN_UUID + " TEXT NOT NULL, " +
													COLUMN_IS_SAVED + " INTEGER DEFAULT 0, " +
													COLUMN_TITLE + " TEXT, " +
													COLUMN_DESCRIPTION + " TEXT, " +
													COLUMN_IMAGE_URL + " TEXT, " +
													COLUMN_LINK + " TEXT, " +
													COLUMN_PUBDATE + " TEXT, " +
													COLUMN_DEF_TITLE + " TEXT, " +
													COLUMN_DEF_DESCRIPTION + " TEXT, " +
													COLUMN_DEF_IMAGE_URL + " TEXT, " +
													COLUMN_DEF_LINK + " TEXT, " +
													COLUMN_RSS_URL + " TEXT NOT NULL);";

	private static DatabaseManager instance = null;
	private DBHelper mDatabaseHelper;

	private DatabaseManager(Context context) {
		mDatabaseHelper = new DBHelper(context, context.getDatabasePath(DB_NAME).getPath(), null, DB_VERSION);
	}
	
	public static synchronized DatabaseManager getInstance(Context c) {
		if (null == instance) {
			instance = new DatabaseManager(c);
		}
		return instance;
	}
	
	public void addFeed(FeedItem item) {
		ContentValues values = getFilledFeedValues(item);
		mDatabaseHelper.getWritableDatabase().insert(TABLE_NAME_FEEDS, null, values);
		mDatabaseHelper.close();
	}

	public void editFeed(FeedItem item) {
		ContentValues values = getFilledFeedValues(item);
		mDatabaseHelper.getWritableDatabase().update(TABLE_NAME_FEEDS, values, COLUMN_UUID + " LIKE '" + item.getUUID() + "'", null);
		mDatabaseHelper.close();
	}

	public void deleteFeed(String uuid) {
		mDatabaseHelper.getWritableDatabase().delete(TABLE_NAME_FEEDS, COLUMN_UUID + " LIKE '" + uuid + "'", null);
		mDatabaseHelper.close();
	}

	public List<FeedItem> getAllFeeds() {
		List<FeedItem> items = null;
		Cursor cursor = mDatabaseHelper.getReadableDatabase().query(TABLE_NAME_FEEDS, null, null, null, null, null, null);
		if (cursor != null) {
			items = new ArrayList<>();
			while (cursor.moveToNext()) {
				String uuid = cursor.getString(COLUMN_UUID_NUMBER);
				String name = cursor.getString(COLUMN_NAME_NUMBER);
				String url = cursor.getString(COLUMN_URL_NUMBER);
				FeedItem item = new FeedItem(uuid, name, url);
				items.add(item);
			}
			cursor.close();
		}
		mDatabaseHelper.close();
		return items;
	}
	
	public void addAllNews(List<RssItem> items) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		for (RssItem item : items) {
			if (!db.isOpen()) {
				db = mDatabaseHelper.getWritableDatabase();
			}
			ContentValues values = getFilledNewsValues(item);
			db.insert(TABLE_NAME_NEWS, null, values);
		}
		mDatabaseHelper.close();
	}
	
	public void deleteAllNews(String url) {
		mDatabaseHelper.getWritableDatabase().delete(TABLE_NAME_NEWS, COLUMN_RSS_URL + " LIKE '" + url + "'", null);
		mDatabaseHelper.close();
	}
	
	public List<RssItem> getAllNews(String url) {
		List<RssItem> items = null;
		Cursor cursor = mDatabaseHelper.getReadableDatabase().query(TABLE_NAME_NEWS, null, COLUMN_RSS_URL + " LIKE '" + url + "'", null, null, null, null);
		if (cursor != null) {
			items = new ArrayList<>();
			while (cursor.moveToNext()) {
				RssItem item = getFilledNews(cursor);
				items.add(item);
			}
			cursor.close();
		}
		mDatabaseHelper.close();
		return items;
	}
	
	public RssItem getNews(String uuid) {
		Cursor cursor = mDatabaseHelper.getReadableDatabase().query(TABLE_NAME_NEWS, null, COLUMN_UUID + " LIKE '" + uuid + "'", null, null, null, null);
		RssItem item = null;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				item = getFilledNews(cursor);
			}
			cursor.close();
		}
		mDatabaseHelper.close();
		return item;
	}
	
	public List<RssItem> getSavedNews(String url) {
		List<RssItem> items = null;
		Cursor cursor = mDatabaseHelper.getReadableDatabase().query(TABLE_NAME_NEWS, null,
				COLUMN_RSS_URL + " LIKE '" + url + "' AND " + COLUMN_IS_SAVED + " = 1",
				null, null, null, null);
		if (cursor != null) {
			items = new ArrayList<>();
			while (cursor.moveToNext()) {
				RssItem item = getFilledNews(cursor);
				items.add(item);
			}
			cursor.close();
		}
		mDatabaseHelper.close();
		return items;
	}
	
	private ContentValues getFilledFeedValues(FeedItem item) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_UUID, item.getUUID());
		values.put(COLUMN_NAME, item.getName());
		values.put(COLUMN_URL, item.getUrl());
		return values;
	}
	
	private ContentValues getFilledNewsValues(RssItem item) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_UUID, item.getUUID());
		values.put(COLUMN_IS_SAVED, item.isSaved() ? 1 : 0);
		values.put(COLUMN_TITLE, item.getTitle());
		values.put(COLUMN_DESCRIPTION, item.getDescription());
		values.put(COLUMN_IMAGE_URL, item.getImageUrl());
		values.put(COLUMN_LINK, item.getLink());
		values.put(COLUMN_PUBDATE, item.getFormattedPubDate());
		values.put(COLUMN_DEF_TITLE, item.getDefTitle());
		values.put(COLUMN_DEF_DESCRIPTION, item.getDefDescription());
		values.put(COLUMN_DEF_IMAGE_URL, item.getDefImageUrl());
		values.put(COLUMN_DEF_LINK, item.getDefLink());
		values.put(COLUMN_RSS_URL, item.getRssUrl());
		return values;
	}
	
	private RssItem getFilledNews(Cursor cursor) {
		RssItem item = new RssItem(cursor.getString(COLUMN_RSS_URL_NUMBER), cursor.getString(COLUMN_UUID_NUMBER));
		item.setTitle(cursor.getString(COLUMN_TITLE_NUMBER));
		item.setDescription(cursor.getString(COLUMN_DESCRIPTION_NUMBER));
		item.setSaved(cursor.getInt(COLUMN_IS_SAVED_NUMBER) != 0);
		item.setLink(cursor.getString(COLUMN_LINK_NUMBER));
		item.setImageUrl(cursor.getString(COLUMN_IMAGE_URL_NUMBER));
		item.setPubDate(cursor.getString(COLUMN_PUBDATE_NUMBER));
		item.setDefTitle(cursor.getString(COLUMN_DEF_TITLE_NUMBER));
		item.setDefDescription(cursor.getString(COLUMN_DEF_DESCRIPTION_NUMBER));
		item.setDefImageUrl(cursor.getString(COLUMN_DEF_IMAGE_URL_NUMBER));
		item.setDefLink(cursor.getString(COLUMN_DEF_LINK_NUMBER));
		return item;
	}
	
	private class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE_TABLE_FEEDS);
			db.execSQL(DB_CREATE_TABLE_NEWS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}