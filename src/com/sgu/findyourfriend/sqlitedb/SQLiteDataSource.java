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
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.model.TempMessage;
import com.sgu.findyourfriend.model.User;

public class SQLiteDataSource {
	
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	
	private String[] allMessageCols = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_MESSAGE, MySQLiteHelper.COLUMN_SENDER_ID,
			MySQLiteHelper.COLUMN_SENDER_NAME,
			MySQLiteHelper.COLUMN_RECEVIER_ID,
			MySQLiteHelper.COLUMN_RECEVIER_NAME, MySQLiteHelper.COLUMN_SMS_DATE };

//	private String[] allTempleMessageCols = { MySQLiteHelper.COLUMN_ID,
//			MySQLiteHelper.COLUMN_MESSAGE,
//			MySQLiteHelper.COLUMN_SMS_DATE };
	
//	private String[] allFriendCols = {
//			MySQLiteHelper.COLUMN_ID,
//			MySQLiteHelper.COLUMN_NAME,
//			MySQLiteHelper.COLUMN_GENDER,
//			MySQLiteHelper.COLUMN_ADDRESS,
//			MySQLiteHelper.COLUMN_BIRTHDAY,
//			MySQLiteHelper.COLUMN_SCHOOL,
//			MySQLiteHelper.COLUMN_WORKPLACE,
//			MySQLiteHelper.COLUMN_EMAIL,
//			MySQLiteHelper.COLUMN_FBLINK,
//			MySQLiteHelper.COLUMN_PUBLIC,
//			MySQLiteHelper.COLUMN_LATITUDE,
//			MySQLiteHelper.COLUMN_LONGITUDE,
//			MySQLiteHelper.COLUMN_AVATAR,
//			MySQLiteHelper.COLUMN_GCMID,
//			MySQLiteHelper.COLUMN_FRIENDSTATE
//	};
//	
//	private String[] allAccountCols = {
//			MySQLiteHelper.COLUMN_ID,
//			MySQLiteHelper.COLUMN_PHONENUMBER
//	};
	
	
	
	
	public SQLiteDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public boolean isOpen() {
		if (null == database) return false;
		return database.isOpen();
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
				allMessageCols, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
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
				allMessageCols, null, null, null, null, null);

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
	
	
	
	// ----------------------------------------------------------
	// temple message data source
	
//	public void createTempleMessage(String message) {
//		Log.i("template created", "ok");
//		
//		ContentValues values = new ContentValues();
//		values.put(MySQLiteHelper.COLUMN_MESSAGE, message);
//		values.put(MySQLiteHelper.COLUMN_SMS_DATE,
//				System.currentTimeMillis());
//
//		long insertId = database.insert(MySQLiteHelper.TABLE_TEMPLE_MESSAGES,
//				null, values);
//
//		Log.i("CREATE SMS WITH ID ", String.valueOf(insertId));
//
//		Cursor cursor = database.query(MySQLiteHelper.TABLE_TEMPLE_MESSAGES,
//				allTempleMessageCols, MySQLiteHelper.COLUMN_ID + " = " + insertId,
//				null, null, null, null);
//		cursor.close();
//	}

//	public void deleteAllTempleMessage() {
//		database.delete(MySQLiteHelper.TABLE_TEMPLE_MESSAGES, null, null);
//	}
//
//	public List<TempMessage> getAllTempleMessages() {
//		List<TempMessage> messages = new ArrayList<TempMessage>();
//
//		Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_TEMPLE_MESSAGES, null);
//		
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()) {
//			TempMessage sms = cursorToTempleMessage(cursor);
//			messages.add(sms);
//			cursor.moveToNext();
//		}
//		// make sure to close the cursor
//		cursor.close();
//		return messages;
//	}
//
//	private TempMessage cursorToTempleMessage(Cursor cursor) {
//		TempMessage message = new TempMessage(cursor.getLong(0),
//				cursor.getString(1), new Date(Long.parseLong(cursor
//						.getString(2))));
//		return message;
//	}
	
	
	//-------------------------------------------------------------
	// friend and account data source
	
//	public void saveMyProfile(Friend mineF) {
////		mineF.setAcceptState(-1); // for mine
//		addFriend(mineF);
//	}
//	
//	public void updateMyProfile(Friend mineF) {
////		mineF.setAcceptState(-1); // for mine
//		updateFriend(mineF);
//	}
//	
//	public Friend getMyProfile() {
//		Friend mine = null;
//		Cursor cursor = database.rawQuery(
//				"SELECT * FROM " + MySQLiteHelper.TABLE_FRIEND
//				+ " WHERE " + MySQLiteHelper.COLUMN_ID + "=" 
//						+ SettingManager.getInstance().getLastAccountIdLogin(), null);
//		cursor.moveToFirst();
//		if (!cursor.isAfterLast()) { 
//			mine = cursorToFriend(cursor);
//		}
//		
//		cursor.close();
//		return mine;
//	}
//
//	public ArrayList<Friend> getAllFriend() {
//		ArrayList<Friend> friends = new ArrayList<Friend>();
//		Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIEND,
//				allFriendCols, null, null, null, null, null);
//
//		int myId = SettingManager.getInstance().getLastAccountIdLogin();
//		
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()) {
//			Friend fr = cursorToFriend(cursor);
//			if (fr.getUserInfo().getId() != myId)
//				friends.add(fr);
//			cursor.moveToNext();
//		}
//		// make sure to close the cursor
//		cursor.close();
//		return friends;
//	}
//	
//
//	private Friend cursorToFriend(Cursor cursor) {
//		
//		User u = new User(
//				cursor.getInt(0),
//				cursor.getString(1), // name
//				cursor.getString(12), // avatar
//				cursor.getString(13), // gcm 
//				null, // last login
// 				cursor.getInt(2), // gender
// 				cursor.getString(3), // address
// 				new java.sql.Date(cursor.getLong(4)), // birthday
// 				cursor.getString(5), // school
// 				cursor.getString(6), // workplace
// 				cursor.getString(7), // email
// 				cursor.getString(8), // facebook
// 				cursor.getInt(9) == 1);
//		
//		return new Friend(u,
//				getPhoneNumber(cursor.getInt(0)),
//				false, cursor.getInt(14),
//				cursor.getDouble(10) == -1.0 ? null :
//					new LatLng(cursor.getDouble(10), cursor.getDouble(10)),
//					null);
//	}
//
//	private ArrayList<String> getPhoneNumber(int fID) {
//		ArrayList<String> phoneNumbers = new ArrayList<String>();
//
//		Cursor cursor =  database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_ACCOUNT 
//				+ " WHERE " + MySQLiteHelper.COLUMN_ID + "=" + fID, null);
//
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()) {
//			String pn = cursorToPhoneNumber(cursor);
//			phoneNumbers.add(pn);
//			cursor.moveToNext();
//		}
//		// make sure to close the cursor
//		cursor.close();
//		return phoneNumbers;
//	}
//
//	private String cursorToPhoneNumber(Cursor cursor) {
//		return cursor.getString(1);
//	}
//
//	public void saveOffFriend(List<Friend> tempFriends) {
//		for (Friend f : tempFriends) {
//			addFriend(f);
//		}
//	}
//	
//	public void updateOffFriends(List<Friend> tempFriends) {
//		for (Friend f : tempFriends) {
//			updateFriend(f);
//		}
//	}
//
//	public void removeFriend(int fIDCurrent) {
//		int rowid = database.delete(MySQLiteHelper.TABLE_FRIEND, 
//				MySQLiteHelper.COLUMN_ID + "=" + fIDCurrent, null);
//		Log.i("DELETE Friend WITH ID ", String.valueOf(rowid));
//		
//		
//		int rowid2 = database.delete(MySQLiteHelper.TABLE_ACCOUNT, 
//				MySQLiteHelper.COLUMN_ID + "=" + fIDCurrent, null);
//		Log.i("DELETE Phonenumber WITH ID ", String.valueOf(rowid2));
//	}
//
//	public void addFriend(Friend fr) {
//		Cursor cursor = database.rawQuery(
//				"SELECT * FROM " + MySQLiteHelper.TABLE_FRIEND
//				+ " WHERE " + MySQLiteHelper.COLUMN_ID + "=" 
//						+ fr.getUserInfo().getId(), null);
//		if (cursor.getCount() <= 0) {
//			// add really add
//			ContentValues values = new ContentValues();
//			values.put(MySQLiteHelper.COLUMN_ID, fr.getUserInfo().getId());
//			values.put(MySQLiteHelper.COLUMN_NAME, fr.getUserInfo().getName());
//			values.put(MySQLiteHelper.COLUMN_GENDER, fr.getUserInfo().getGender());
//			values.put(MySQLiteHelper.COLUMN_ADDRESS, fr.getUserInfo().getAddress());
//			values.put(MySQLiteHelper.COLUMN_BIRTHDAY, fr.getUserInfo().getBirthday().getTime());
//			values.put(MySQLiteHelper.COLUMN_SCHOOL, fr.getUserInfo().getSchool());
//			values.put(MySQLiteHelper.COLUMN_WORKPLACE, fr.getUserInfo().getWorkplace());
//			values.put(MySQLiteHelper.COLUMN_EMAIL, fr.getUserInfo().getEmail());
//			values.put(MySQLiteHelper.COLUMN_FBLINK, fr.getUserInfo().getFblink());
//			values.put(MySQLiteHelper.COLUMN_PUBLIC, fr.getUserInfo().isPublic() ? 1 : 0);
//			values.put(MySQLiteHelper.COLUMN_LATITUDE, 
//					(null != fr.getLastLocation()) ? fr.getLastLocation().latitude : -1);
//			values.put(MySQLiteHelper.COLUMN_LONGITUDE, 
//					(null != fr.getLastLocation()) ? fr.getLastLocation().longitude : -1);
//			values.put(MySQLiteHelper.COLUMN_AVATAR, fr.getUserInfo().getAvatar());
//			values.put(MySQLiteHelper.COLUMN_GCMID, fr.getUserInfo().getGcmId());
//			values.put(MySQLiteHelper.COLUMN_FRIENDSTATE, fr.getAcceptState());
//		
//			long insertId = database.insert(MySQLiteHelper.TABLE_FRIEND, null,
//					values);
//
//			Log.i("CREATE Friend WITH ID ", String.valueOf(insertId));
//			
//			if (insertId != -1) {
//				// add phone number
////				removeAllPhoneNumber();
//				for (String pnumber : fr.getNumberLogin()) {
//					ContentValues values2 = new ContentValues();
//					values2.put(MySQLiteHelper.COLUMN_ID, fr.getUserInfo().getId());
//					values2.put(MySQLiteHelper.COLUMN_PHONENUMBER, pnumber);
//					
//					Cursor cursor2 = database.rawQuery(
//							"SELECT * FROM " + MySQLiteHelper.TABLE_ACCOUNT
//							+ " WHERE " + MySQLiteHelper.COLUMN_ID + "=" 
//							+ fr.getUserInfo().getId() + " AND " 
//							+	MySQLiteHelper.COLUMN_PHONENUMBER + "=" + pnumber, null);
//					
//					if (cursor2.getCount() <= 0) {
//						long row = database.insert(MySQLiteHelper.TABLE_ACCOUNT, null, values2);
//						Log.i("CREATE Phone WITH ID ", String.valueOf(row));
//					}
//					
//					cursor2.close();
//				}
//			}
//		} else {
//			// update 
//			updateFriend(fr);
//			
//		}
//		
//		cursor.close();
//	}
//
////	private void removeAllPhoneNumber() {
////	}
//
//	public void updateFriend(Friend fr) {
//		Cursor cursor = database.rawQuery(
//				"SELECT * FROM " + MySQLiteHelper.TABLE_FRIEND
//				+ " WHERE " + MySQLiteHelper.COLUMN_ID + "=" 
//						+ fr.getUserInfo().getId(), null);
//		if (cursor.getCount() > 0) {
//			ContentValues values = new ContentValues();
//			values.put(MySQLiteHelper.COLUMN_NAME, fr.getUserInfo().getName());
//			values.put(MySQLiteHelper.COLUMN_GENDER, fr.getUserInfo().getGender());
//			values.put(MySQLiteHelper.COLUMN_ADDRESS, fr.getUserInfo().getAddress());
//			values.put(MySQLiteHelper.COLUMN_BIRTHDAY, fr.getUserInfo().getBirthday().getTime());
//			values.put(MySQLiteHelper.COLUMN_SCHOOL, fr.getUserInfo().getSchool());
//			values.put(MySQLiteHelper.COLUMN_WORKPLACE, fr.getUserInfo().getWorkplace());
//			values.put(MySQLiteHelper.COLUMN_EMAIL, fr.getUserInfo().getEmail());
//			values.put(MySQLiteHelper.COLUMN_FBLINK, fr.getUserInfo().getFblink());
//			values.put(MySQLiteHelper.COLUMN_PUBLIC, fr.getUserInfo().isPublic() ? 1 : 0);
//			values.put(MySQLiteHelper.COLUMN_LATITUDE, 
//					(null != fr.getLastLocation()) ? fr.getLastLocation().latitude : -1);
//			values.put(MySQLiteHelper.COLUMN_LONGITUDE, 
//					(null != fr.getLastLocation()) ? fr.getLastLocation().longitude : -1);
//			values.put(MySQLiteHelper.COLUMN_AVATAR, fr.getUserInfo().getAvatar());
//			values.put(MySQLiteHelper.COLUMN_GCMID, fr.getUserInfo().getGcmId());
//			values.put(MySQLiteHelper.COLUMN_FRIENDSTATE, fr.getAcceptState());
//			
//			
//			int rowupdate = database.update(MySQLiteHelper.TABLE_FRIEND, values, 
//					MySQLiteHelper.COLUMN_ID + "=" + fr.getUserInfo().getId(), null);
//			
//			Log.i("UPDATE Friend WITH ID ", String.valueOf(rowupdate));
//		}
//		
//		cursor.close();
//		
//	}

	
}
