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

package com.sgu.findyourfriend;

import android.R.id;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SQLiteDatabaseManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.screen.MainActivity;
import com.sgu.findyourfriend.utils.Utility;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	private static MainActivity mainActivity = null;

	public GCMIntentService() {
		// Call extended class Constructor GCMBaseIntentService
		super(Config.GOOGLE_SENDER_ID);
	}

	public static void setMainActivity(MainActivity mainActivity) {
		GCMIntentService.mainActivity = mainActivity;
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, final String registrationId) {

		Log.i(TAG, "Device registered: regId = " + registrationId);
		// Utility.displayMessageOnScreen(context,
		// "Your device registred with GCM");

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
		// Utility.checkAppRunning(getApplicationContext());

	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		// String message = getString(R.string.gcm_deleted, total);
		// Utility.displayMessageOnScreen(context, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		// Utility.displayMessageOnScreen(context,
		// getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log error message
		Log.i(TAG, "Received recoverable error: " + errorId);
		// Utility.displayMessageOnScreen(context,
		// getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

}
