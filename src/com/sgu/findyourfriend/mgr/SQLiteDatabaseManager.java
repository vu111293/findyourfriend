/*
 * 	 This file is part of Find Your Friend.
 *
 *   Find Your Friend is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Find Your Friend is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Find Your Friend.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sgu.findyourfriend.mgr;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.sqlitedb.MySQLiteBase;
import com.sgu.findyourfriend.sqlitedb.SQLiteDataSource;

public class SQLiteDatabaseManager extends BaseManager {

	private static SQLiteDatabaseManager instance;

	private SQLiteDataSource mDS;

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
