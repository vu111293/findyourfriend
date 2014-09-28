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
