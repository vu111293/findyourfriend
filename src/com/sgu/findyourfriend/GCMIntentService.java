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

package com.sgu.findyourfriend;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SQLiteDatabaseManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.screen.MainActivity;
import com.sgu.findyourfriend.utils.Utility;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(Config.GOOGLE_SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, final String registrationId) {

		Log.i(TAG, "Device registered: regId = " + registrationId);
		Log.i(TAG, "myrpfile update!");

		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				MyProfileManager.getInstance().init(getApplicationContext(),
						SettingManager.getInstance().getLastAccountIdLogin(),
						false);

				PostData.updateGcmId(getApplicationContext(), SettingManager
						.getInstance().getLastAccountIdLogin(), registrationId);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				Log.i(TAG, "update gcmid on onregister");
				MyProfileManager.getInstance().setMyGCMID(registrationId);
			}

		}).execute();
	}

	/**
	 * Method called on device unregistred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		Utility.displayMessageOnScreen(context,
				getString(R.string.gcm_unregistered));
	}

	/**
	 * Method called on Receiving a new message from GCM server
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {

		final String message = intent.getExtras().getString("price");
		Log.i(TAG, "Received message " + message);

		Utility.displayNotification(context, message);

		if (Utility.isActivityRunning(getApplicationContext(),
				MainActivity.class)) {
			Log.i("TEST", "app running");
			Utility.displayMessageOnScreen(context, message);
		} else {
			Log.i("TEST", "app out");
			// save temple message

			if (!Utility.verifyRequest(message)
					&& !Utility.verifyResponse(message)) {

				(new AsyncTask<Void, Void, Void>() {

					Friend sender = null;
					Friend receiver = null;

					@Override
					protected Void doInBackground(Void... params) {
						SettingManager.getInstance().init(
								getApplicationContext());

						int idSender = Utility.getSenderMessage(message);
						if (idSender != 0)
							sender = PostData.friendGetFriend(
									getApplicationContext(), idSender);

						receiver = PostData.friendGetFriend(
								getApplicationContext(), SettingManager
										.getInstance().getLastAccountIdLogin());
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						if (null != receiver) {
							Log.i("TEST", "save message offline");
							SQLiteDatabaseManager.getInstance().init(
									getApplicationContext());
							SQLiteDatabaseManager.getInstance().saveMessage(
									Utility.parseMessage(message, sender,
											receiver));
						}

					}
				}).execute();
			}
		}
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log error message
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

}
