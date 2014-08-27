package com.sgu.findyourfriend.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int minutes = prefs.getInt("interval", 1000);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, ServiceUpdatePosition.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		am.cancel(pi);
		if (minutes > 0) {
			am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + minutes * 60 * 1000,
					minutes * 60 * 1000, pi);
		}

	}

}
