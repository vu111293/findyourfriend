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
