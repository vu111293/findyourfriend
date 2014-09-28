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
package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.preference.MultiSelectListPreference;
import android.provider.CalendarContract;
import android.util.AttributeSet;

public class FriendMultiSelectPreference extends MultiSelectListPreference {
	ContentResolver cr;
	Cursor cursor;
	String[] projection = new String[] { CalendarContract.Calendars.NAME,
			CalendarContract.Calendars.CALENDAR_DISPLAY_NAME };
	String selection = "(" + CalendarContract.Calendars.VISIBLE + " = ?)";
	String[] selectionArgs = new String[] { "1" };

	@SuppressLint("NewApi")
	public FriendMultiSelectPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		List<CharSequence> entries = new ArrayList<CharSequence>();
		List<CharSequence> entriesValues = new ArrayList<CharSequence>();

		cr = context.getContentResolver();
		cursor = cr.query(CalendarContract.Calendars.CONTENT_URI, projection,
				selection, selectionArgs, null);

		while (cursor.moveToNext()) {
			String name = cursor.getString(0);
			String displayName = cursor.getString(1);

			entries.add(name);
			entriesValues.add(displayName);
		}

		setEntries(entries.toArray(new CharSequence[] {}));
		setEntryValues(entriesValues.toArray(new CharSequence[] {}));
	}

}
