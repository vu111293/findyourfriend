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
package com.sgu.findyourfriend.sqlitedb;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.net.PostData;

public class MySQLiteHelper extends SQLiteOpenHelper {

	// message table
	public static final String TABLE_MESSAGES = "messages";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SENDER_ID = "IdSender";
	public static final String COLUMN_SENDER_NAME = "senderName";
	public static final String COLUMN_RECEVIER_ID = "IdReceiver";
	public static final String COLUMN_RECEVIER_NAME = "receiverName";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_SMS_DATE = "smsDate";

	// temple message table
	public static final String TABLE_TEMPLE_MESSAGES = "temp_messages";
	// public static final String COLUMN_ID = "_id";
	// public static final String COLUMN_MESSAGE = "message";
	// public static final String COLUMN_SMS_DATE = "sms_time";

	
	// friend table
	public static String TABLE_FRIEND = "friends";
	// public static String COLUMN_ID = "_id";
	public static String COLUMN_NAME = "name";
	public static String COLUMN_GENDER = "gender";
	public static String COLUMN_ADDRESS = "address";
	public static String COLUMN_BIRTHDAY = "birthday";
	public static String COLUMN_SCHOOL = "school";
	public static String COLUMN_WORKPLACE = "workplace";
	public static String COLUMN_EMAIL = "email";
	public static String COLUMN_FBLINK = "facebook";
	public static String COLUMN_PUBLIC = "public";
	public static String COLUMN_LATITUDE = "latitude";
	public static String COLUMN_LONGITUDE = "longitude";
	public static String COLUMN_AVATAR = "avatarpath";
	public static String COLUMN_GCMID = "gcmid";
	public static String COLUMN_FRIENDSTATE = "friendstate";

	// account table
	public static String TABLE_ACCOUNT = "accounts";
	// public static String COLUMN_ID = "_id";
	public static String COLUMN_PHONENUMBER = "phonenumber";

	private static final String MESSAGE_TABLE_CREATE = "create table "
			+ TABLE_MESSAGES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_MESSAGE
			+ " text not null, " + COLUMN_SENDER_ID + " integer not null, "
			+ COLUMN_RECEVIER_ID + " integer not null, " + COLUMN_SENDER_NAME
			+ " text not null, " + COLUMN_RECEVIER_NAME + " text not null, "
			+ COLUMN_SMS_DATE + " timestamp not null);";

	private static final String TEMP_MESSAGE_TABLE_CREATE = "create table "
			+ TABLE_TEMPLE_MESSAGES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_MESSAGE
			+ " text not null," + COLUMN_SMS_DATE + " timestamp not null);";

	private static final String FRIEND_TABLE_CREATE = "create table "
			+ TABLE_FRIEND + "(" + COLUMN_ID + " integer primary key, "
			+ COLUMN_NAME + " text not null, " + COLUMN_GENDER
			+ " integer not null, " + COLUMN_ADDRESS + " text not null, "
			+ COLUMN_BIRTHDAY + " integer null, " + COLUMN_SCHOOL
			+ " text null, " + COLUMN_WORKPLACE + " text null, " + COLUMN_EMAIL
			+ " text not null, " + COLUMN_FBLINK + " text null, "
			+ COLUMN_PUBLIC + " integer not null, " + COLUMN_LATITUDE
			+ " real null, " + COLUMN_LONGITUDE + " real null, "
			+ COLUMN_AVATAR + " text null, " + COLUMN_GCMID + " text null, "
			+ COLUMN_FRIENDSTATE + " integer null);";

	private static final String ACCOUNT_TABLE_CREATE = "create table "
			+ TABLE_ACCOUNT + "(" + COLUMN_ID + " integer not null, "
			+ COLUMN_PHONENUMBER + " integer not null, " + "PRIMARY KEY ("
			+ COLUMN_ID + ", " + COLUMN_PHONENUMBER + "));";

	
	private Context context;
	
	public MySQLiteHelper(Context context) {
		super(context, MySQLiteBase.getDataBaseName(), null,
				MySQLiteBase.DATABASE_VERSION);
		this.context = context;
		Log.i("DATABASE message: ", MySQLiteBase.getDataBaseName());
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(MESSAGE_TABLE_CREATE);
		database.execSQL(TEMP_MESSAGE_TABLE_CREATE);
		database.execSQL(FRIEND_TABLE_CREATE);
		database.execSQL(ACCOUNT_TABLE_CREATE);

		// import database
//		importDataMessage();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLE_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
		onCreate(db);
	}

	private void importDataMessage() {
		final SQLiteDataSource sqlite = new SQLiteDataSource(context);
		sqlite.open();
		
		(new AsyncTask<Void, Void, ArrayList<Message>> () {

			@Override
			protected ArrayList<Message> doInBackground(Void... arg0) {
				return PostData.getChatsById(context, MyProfileManager.getInstance().getMyID());
			}
			
			@Override
			protected void onPostExecute(ArrayList<Message> result) {
				for (Message msg : result) {
					sqlite.createMessage(msg);
				}
				
				sqlite.close();
			}
			
		}).execute();
	}

}
