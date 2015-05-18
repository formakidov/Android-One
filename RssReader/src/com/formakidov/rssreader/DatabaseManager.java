package com.formakidov.rssreader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.formakidov.rssreader.data.FeedItem;

public class DatabaseManager {	
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "RSSREADER.db";
	
	private static final String TABLE_NAME = "FEEDS";
	
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_NAME = "Name";
	private static final String COLUMN_URL = "Url";
	private static final String DB_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + 
													COLUMN_ID + " INTEGER PRIMARY KEY, " +
													COLUMN_NAME + " TEXT, " +
													COLUMN_URL + " TEXT);";
	
	private static final String DB_GET_ALL_FEEDS = "SELECT * FROM " + TABLE_NAME;

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
	
	private void deleteFile(long id) {
		mDatabaseHelper.getWritableDatabase().delete(TABLE_NAME, "_id = " + id, null);
	}
	
	public List<FeedItem> getFeeds() {
		List<FeedItem> items = null;
		Cursor cursor = mDatabaseHelper.getReadableDatabase().rawQuery(DB_GET_ALL_FEEDS, null);
		if (cursor != null) {
			items = new ArrayList<FeedItem>();
			while (cursor.moveToNext()) {		
				//TODO
				FeedItem item = new FeedItem("", "");
				items.add(item);
			}
			cursor.close();
		}
		return items;
	}
	
	private class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
