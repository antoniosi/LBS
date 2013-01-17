package com.lbs.tracker.db.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LBSDBAdapter {
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "LBS";
	private static final String LBS_TABLE_NAME = "LBS_STORE";
	private static final String LBS_TABLE_CREATE =
			"CREATE TABLE " + LBS_TABLE_NAME + " ( id integer primary key autoincrement, " +
			" time integer, " +
			" latitude real, " +
			" longitude real); ";
	
	private static final String TIME_KEY = "time";
	private static final String LATITUDE_KEY = "latitude";
	private static final String LONGITUDE_KEY = "longitude";
	
	private final Context context;
	private LBSDBHelper dbHelper;
	private SQLiteDatabase db;
	
	public LBSDBAdapter(Context context) {
		this.context = context;
	}

	public LBSDBAdapter open() throws SQLException {
		if (dbHelper == null) {
			dbHelper = new LBSDBHelper(context);
			db = dbHelper.getWritableDatabase();
			String dbPath = db.getPath();
			int version = db.getVersion();
		}

		return this;
	}

	public void close() {
		if (dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
			db = null;
		}
	}
	
	public long addLocation(long time, double latitude, double longitude) {
		ContentValues values = new ContentValues();
		values.put(TIME_KEY, time);
		values.put(LATITUDE_KEY, latitude);
		values.put(LONGITUDE_KEY, longitude);

		long retValue = db.insert(LBS_TABLE_NAME, null, values);
		return retValue;
	}
	
	public Cursor fetchLocations(long startTime, long endTime) throws SQLException {
		Cursor cursor = db.rawQuery("select * from " + LBS_TABLE_NAME + " where time >= :startTime and time <= :endTime", new String[] { String.valueOf(startTime), String.valueOf(endTime) });
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;

	}
	
	private static class LBSDBHelper extends SQLiteOpenHelper {

		public LBSDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(LBS_TABLE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + LBS_TABLE_NAME);
			onCreate(db);
		}
	}
}
