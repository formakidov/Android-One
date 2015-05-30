package com.formakidov.rssreader;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.formakidov.rssreader.data.FeedItem;

public class DatabaseManager {	
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "rssreader.db";
	
	private static final String TABLE_NAME_FEEDS = "feeds";
//	private static final String TABLE_NAME_NEWS = "news";
	
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_NAME = "Name";
	private static final String COLUMN_URL = "Url";
	
	private static final int COLUMN_ID_NUMBER = 0;
	private static final int COLUMN_NAME_NUMBER = 1;
	private static final int COLUMN_URL_NUMBER = 2;
	
//	private static final String COLUMN_TITLE = "title";
//	private static final String COLUMN_DESCRIPTION = "description";
//	private static final String COLUMN_IMAGE_URL = "image_url";
//	private static final String COLUMN_LINK = "link";
//	private static final String COLUMN_PUBDATE = "pubdate";
//	private static final String COLUMN_DEF_TITLE = "def_title";
//	private static final String COLUMN_DEF_DESCRIPTION = "def_description";
//	private static final String COLUMN_DEF_IMAGE_URL = "def_image_url";
//	private static final String COLUMN_DEF_LINK = "def_link";
	
	private static final String DB_CREATE_TABLE_FEEDS = "CREATE TABLE " + TABLE_NAME_FEEDS + " (" + 
													COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
													COLUMN_NAME + " TEXT NOT NULL, " +
													COLUMN_URL + " TEXT NOT NULL);";
	
	private static final String DB_GET_ALL_FEEDS = "SELECT * FROM " + TABLE_NAME_FEEDS;

	private static DatabaseManager instance = null;
	private DBHelper mDatabaseHelper;

	private DatabaseManager(Context context) {
		mDatabaseHelper = new DBHelper(context, context.getDatabasePath(DB_NAME).getPath(), null, DB_VERSION);
	}
	
	public static synchronized DatabaseManager getInstance(Context c) {
		if (instance == null) {
			instance = new DatabaseManager(c);
		}
		return instance;
	}
	
	public void addFeed(FeedItem item) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, item.getName());
		values.put(COLUMN_URL, item.getUrl());
		mDatabaseHelper.getWritableDatabase().insert(TABLE_NAME_FEEDS, null, values);
		mDatabaseHelper.close();
	}
	
	public void deleteFeed(String name) {
		mDatabaseHelper.getWritableDatabase().delete(TABLE_NAME_FEEDS, COLUMN_NAME + " = " + name, null);
		mDatabaseHelper.close();
	}
	
	public List<FeedItem> getFeeds() {
		List<FeedItem> items = null;
		Cursor cursor = mDatabaseHelper.getReadableDatabase().rawQuery(DB_GET_ALL_FEEDS, null);
		if (cursor != null) {
			items = new ArrayList<FeedItem>();
			while (cursor.moveToNext()) {
				String name = cursor.getString(COLUMN_NAME_NUMBER);
				String url = cursor.getString(COLUMN_URL_NUMBER);
				FeedItem item = new FeedItem(name, url);
				items.add(item);
			}
			cursor.close();
		}
		mDatabaseHelper.close();
		return items;
	}
	
	private class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE_TABLE_FEEDS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
