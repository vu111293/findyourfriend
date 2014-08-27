package com.sgu.findyourfriend.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteTempHelper extends SQLiteOpenHelper {

	public static final String TABLE_MESSAGES = "temp_messages";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_SMS_DATE = "sms_time";

	private static final String DATABASE_NAME = "com.sgu.findyourfriend.messages.temp_message";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MESSAGES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_MESSAGE
			+ " text not null," + COLUMN_SMS_DATE + " timestamp not null);";

	public MySQLiteTempHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.i("DATABASE message: ", DATABASE_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		onCreate(db);
	}

}
