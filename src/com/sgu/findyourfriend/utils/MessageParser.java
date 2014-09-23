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

import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.model.SimpleUserAndLocation;

public class MessageParser {

	private static String TAG = FriendJSONParser.class.getName();

	public static ArrayList<Message> parse(JSONArray jSs) {
		int countryCount = jSs.length();
		ArrayList<Message> msglist = new ArrayList<Message>();
		Message s = null;

		// Taking each country, parses and adds to list object
		for (int i = 0; i < countryCount; i++) {
			try {
				// Call getCountry with country JSON object to parse the country
				s = getMsg((JSONObject) jSs.get(i));
				msglist.add(s);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return msglist;
	}

	public static Message getMsg(JSONObject jS) {

		Message s = null;

		try {
			String msg=jS.getString("msg");
			int idSender=jS.getInt("sender");
			int idReceiver=jS.getInt("recipient");
			Timestamp smsTime=Timestamp.valueOf(jS.getString("timest"));
			
			
//			String senderName = FriendManager.getInstance().getNameFriend(idSender);
//			String receiverName = FriendManager.getInstance().getNameFriend(idReceiver);
			
			
//			new Message(message, MyProfileManager.getInstance().getMyID() == idSender,
//					idSender, senderName, idReceiver, receiverName, location, smsTime)
			
			s = Utility.parseMessage(msg);
			s.setSmsTime(smsTime);
			// s = new Message(msg, false, idSender, idReceiver, smsTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return s;
	}
}
