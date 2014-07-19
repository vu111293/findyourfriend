package com.sgu.findyourfriend.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.History;

public class HistoryJSONParser {
  
	public static List<History> parse(JSONObject jObject) {
		JSONArray jHistorys = null;
		try {
			jHistorys = jObject.getJSONArray("history");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return getHistorys(jHistorys);
	}

	private static List<History> getHistorys(JSONArray jHistorys) {
		int countryCount = jHistorys.length();
		List<History> HistoryList = new ArrayList<History>();
		History History = null;

		// Taking each country, parses and adds to list object
		for (int i = 0; i < countryCount; i++) {
			try {
				// Call getCountry with country JSON object to parse the country
				History = getHistory((JSONObject) jHistorys.get(i));
				HistoryList.add(History);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return HistoryList;

	}

	// Parsing the Country JSON object
	public static History getHistory(JSONObject jHistory) {

		Timestamp st;
		LatLng location = null;
		History history = null;

		try {
			st = Timestamp.valueOf(jHistory.getString("timest"));
			location = new LatLng(Double.parseDouble(jHistory.getString("latitude")),
					Double.parseDouble(jHistory.getString("longtitude")));
			history = new History(st, location);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return history;
	}
}
