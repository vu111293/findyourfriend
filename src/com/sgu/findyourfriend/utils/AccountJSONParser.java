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
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountJSONParser {

	public List<HashMap<String, Object>> parse(JSONObject jObject) {
		JSONArray jAccounts = null;
		try {
			jAccounts = jObject.getJSONArray("accounts");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return getAccounts(jAccounts);
	}

	private List<HashMap<String, Object>> getAccounts(JSONArray jAccounts) {
		int countryCount = jAccounts.length();
		List<HashMap<String, Object>> AccountList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> Account = null;

		// Taking each country, parses and adds to list object
		for (int i = 0; i < countryCount; i++) {
			try {
				// Call getCountry with country JSON object to parse the country
				Account = getAccount((JSONObject) jAccounts.get(i));
				AccountList.add(Account);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return AccountList;

	}

	// Parsing the Country JSON object
	private HashMap<String, Object> getAccount(JSONObject jAccount) {

		HashMap<String, Object> Account = new HashMap<String, Object>();
		String number = "";
		String password = "";

		try {
			number = jAccount.getString("number");
			password=jAccount.getString("password");
			
			Account.put("number", number);
			Account.put("password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return Account;
	}
}
