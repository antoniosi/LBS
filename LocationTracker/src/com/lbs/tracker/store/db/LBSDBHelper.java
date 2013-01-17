package com.lbs.tracker.store.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LBSDBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "LBS";
	private static final String LBS_TABLE_NAME = "LBS_STORE";
	private static final String LBS_TABLE_CREATE =
			"CREATE TABLE " + LBS_TABLE_NAME + " ( id integer primary key autoincrement, " +
			" time integer, " +
			" latitude integer, " +
			" longitude integer); ";

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
