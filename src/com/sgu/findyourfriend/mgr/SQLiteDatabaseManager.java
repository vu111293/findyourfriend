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
package com.sgu.findyourfriend.mgr;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.model.TempMessage;
import com.sgu.findyourfriend.sqlitedb.MySQLiteBase;
import com.sgu.findyourfriend.sqlitedb.SQLiteDataSource;

public class SQLiteDatabaseManager extends BaseManager {

	private static SQLiteDatabaseManager instance;

	private SQLiteDataSource mDS;

	// private FriendAccountDataSource mDS;
	// private static TempMessagesDataSource tempMsgSQLite;

	public synchronized static SQLiteDatabaseManager getInstance() {
		if (instance == null) {
			instance = new SQLiteDatabaseManager();
		}
		return instance;
	}

	public boolean isDataBaseExist() {
		SQLiteDatabase checkDB = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(
					MySQLiteBase.getDataBaseName(), null,
					SQLiteDatabase.OPEN_READONLY);
			checkDB.close();
		} catch (SQLiteException e) {
			// database doesn't exist yet.
		}
		return checkDB != null ? true : false;
	}

	@Override
	public void init(Context context) {
		super.init(context);
		if (null == mDS) {
			mDS = new SQLiteDataSource(context);
			if (!mDS.isOpen())
				mDS.open();
		}
	}

//	public void quickSaveTempMessage(Context ctx, String message) {
//		if (null == mDS) {
//			mDS = new SQLiteDataSource(context);
//		}
//		if (!mDS.isOpen())
//			mDS.open();
//
//		mDS.createTempleMessage(message);
//
//		mDS.close();
//	}
//
//	public List<TempMessage> quickGetAllTempMessage(Context ctx) {
//		if (!mDS.isOpen())
//			mDS.open();
//
//		try {
//			return mDS.getAllTempleMessages();
//		} finally {
//			mDS.deleteAllTempleMessage();
//			mDS.close();
//		}
//	}

	public List<Message> getAllMessage() {
		if (!mDS.isOpen())
			mDS.open();

		try {
			return mDS.getAllMessages();
		} finally {
			mDS.close();
		}
	}

	public Message saveMessage(Message msg) {
		if (!mDS.isOpen())
			mDS.open();

		try {
			return mDS.createMessage(msg);
		} finally {
			mDS.close();
		}
	}

	public void removeMessage(Message msg) {
		if (!mDS.isOpen())
			mDS.open();

		try {
			mDS.deleteMessage(msg);
		} finally {
			mDS.close();
		}
	}
}
