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
