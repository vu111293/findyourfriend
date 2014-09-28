/*
 * Copyright (C) 2014 Tubor Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sgu.findyourfriend.utils;

import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_MESSAGES = "messages";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SENDER_ID = "IdSender";
	public static final String COLUMN_SENDER_NAME = "senderName";
	public static final String COLUMN_RECEVIER_ID = "IdReceiver";
	public static final String COLUMN_RECEVIER_NAME = "receiverName";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_SMS_DATE = "smsDate";

	private static final String DATABASE_NAME_PREFIX = "com.sgu.findyourfriend.messages.";
			// + MyProfileManager.getInstance().getMyID() + "_2014.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MESSAGES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_MESSAGE
			+ " text not null, " + COLUMN_SENDER_ID + " integer not null, "
			+ COLUMN_RECEVIER_ID + " integer not null, " + COLUMN_SENDER_NAME
			+ " text not null, " + COLUMN_RECEVIER_NAME + " text not null, "
			+ COLUMN_SMS_DATE + " timestamp not null);";

	public MySQLiteHelper(Context context) {
		super(context, getDataBaseName() + "", null, DATABASE_VERSION);
		Log.i("DATABASE message: ", getDataBaseName());
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

	public static String getDataBaseName() {
		return  DATABASE_NAME_PREFIX + SettingManager.getInstance().getLastAccountIdLogin() + ".db";
	}
	
	
}
