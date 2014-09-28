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
package com.sgu.findyourfriend.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.model.TempMessage;

public class TempMessagesDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteTempHelper dbHelper;
	private String[] allColumns = { MySQLiteTempHelper.COLUMN_ID,
			MySQLiteTempHelper.COLUMN_MESSAGE,
			MySQLiteTempHelper.COLUMN_SMS_DATE };

	public TempMessagesDataSource(Context context) {
		dbHelper = new MySQLiteTempHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createMessage(String message) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteTempHelper.COLUMN_MESSAGE, message);
		values.put(MySQLiteTempHelper.COLUMN_SMS_DATE,
				System.currentTimeMillis());

		long insertId = database.insert(MySQLiteTempHelper.TABLE_MESSAGES,
				null, values);

		Log.i("CREATE SMS WITH ID ", String.valueOf(insertId));

		Cursor cursor = database.query(MySQLiteTempHelper.TABLE_MESSAGES,
				allColumns, MySQLiteTempHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.close();
	}

	public void deleteAllMessage() {
		database.delete(MySQLiteTempHelper.TABLE_MESSAGES, null, null);
	}

	public List<TempMessage> getAllMessages() {
		List<TempMessage> messages = new ArrayList<TempMessage>();

		// Cursor cursor = database.query(MySQLiteTempHelper.TABLE_MESSAGES,
		// 		allColumns, null, null, null, null, null);

		Cursor cursor = database.rawQuery("select * from " + MySQLiteTempHelper.TABLE_MESSAGES, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TempMessage sms = cursorToMessage(cursor);
			messages.add(sms);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return messages;
	}

	private TempMessage cursorToMessage(Cursor cursor) {
		TempMessage message = new TempMessage(cursor.getLong(0),
				cursor.getString(1), new Date(Long.parseLong(cursor
						.getString(2))));
		return message;
	}
}
