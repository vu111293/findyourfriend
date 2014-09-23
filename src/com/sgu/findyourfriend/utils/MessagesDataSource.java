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

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Message;

public class MessagesDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_MESSAGE, MySQLiteHelper.COLUMN_SENDER_ID,
			MySQLiteHelper.COLUMN_SENDER_NAME,
			MySQLiteHelper.COLUMN_RECEVIER_ID,
			MySQLiteHelper.COLUMN_RECEVIER_NAME, MySQLiteHelper.COLUMN_SMS_DATE };

	public MessagesDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Message createMessage(Message sms) {
		ContentValues values = new ContentValues();

		String message = sms.getMessage();
		if (null != sms.getLocation())
			message += Config.PREFIX_LOCATION_IN_MESSAGE
					+ sms.getLocation().latitude + " "
					+ sms.getLocation().longitude;

		values.put(MySQLiteHelper.COLUMN_MESSAGE, message);
		values.put(MySQLiteHelper.COLUMN_SENDER_ID, sms.getIdSender());
		values.put(MySQLiteHelper.COLUMN_SENDER_NAME, sms.getSenderName());
		values.put(MySQLiteHelper.COLUMN_RECEVIER_ID, sms.getIdReceiver());
		values.put(MySQLiteHelper.COLUMN_RECEVIER_NAME, sms.getReceiverName());
		values.put(MySQLiteHelper.COLUMN_SMS_DATE, sms.getSmsTime().getTime());

		long insertId = database.insert(MySQLiteHelper.TABLE_MESSAGES, null,
				values);

		Log.i("CREATE SMS WITH ID ", String.valueOf(insertId));

		Cursor cursor = database.query(MySQLiteHelper.TABLE_MESSAGES,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Message mes = cursorToMessage(cursor);
		cursor.close();
		return mes;
	}

	public void deleteMessage(Message sms) {
		long id = sms.getId();
		Log.i("@@@@@@@@@@@@@@@@: ", String.valueOf(sms.getId()));
		System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_MESSAGES, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Message> getAllMessages() {
		List<Message> messages = new ArrayList<Message>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_MESSAGES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Message sms = cursorToMessage(cursor);
			messages.add(sms);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return messages;
	}

	private Message cursorToMessage(Cursor cursor) {
		// new Message(id, message, isMine, idSender, senderName, idReceiver,
		// receiverName, smsTime)

		String msg = cursor.getString(1);
		LatLng location = null;

		int idLocation = msg.indexOf(Config.PREFIX_LOCATION_IN_MESSAGE);
		
		Log.i("DATA SOURCE", msg + " @ " + idLocation);
		
		if (idLocation >= 0) {
			String strLoc[] = msg.substring(idLocation).split(" ");
			Log.i("DATA SOURCE WITH LOCATION", msg.substring(idLocation));
//			
			if (strLoc.length == 3)
				location = new LatLng(Double.parseDouble(strLoc[1]),
						Double.parseDouble(strLoc[2]));
			msg = msg.substring(0, idLocation);
		}
		

		Message message = new Message(cursor.getLong(0), msg,
				cursor.getInt(2) == MyProfileManager.getInstance().getMyID(),
				cursor.getInt(2), cursor.getString(3), cursor.getInt(4),
				cursor.getString(5), location, new Date(
						Long.parseLong(cursor.getString(6))));
		return message;
	}
}
