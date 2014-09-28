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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sgu.findyourfriend.model.SimpleUserAndLocation;

public class SimpleUserAndLocationParser {

	private static String TAG = FriendJSONParser.class.getName();

	public static ArrayList<SimpleUserAndLocation> parse(JSONArray jSs) {
		int countryCount = jSs.length();
		ArrayList<SimpleUserAndLocation> FriendList = new ArrayList<SimpleUserAndLocation>();
		SimpleUserAndLocation s = null;

		// Taking each country, parses and adds to list object
		for (int i = 0; i < countryCount; i++) {
			try {
				// Call getCountry with country JSON object to parse the country
				s = getSimpleUserAndLocation((JSONObject) jSs.get(i));
				FriendList.add(s);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return FriendList;
	}

	public static SimpleUserAndLocation getSimpleUserAndLocation(JSONObject jS) {

		SimpleUserAndLocation s = null;

		try {
			int id = jS.getInt("id");
			Double lat = Double.parseDouble(jS.getString("lat"));
			Double lng = Double.parseDouble(jS.getString("lng"));
			s = new SimpleUserAndLocation(id, lat, lng);
			Log.i(TAG, id + ":" + lat + "," + lng);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return s;
	}
}
