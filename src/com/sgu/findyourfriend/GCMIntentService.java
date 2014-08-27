package com.sgu.findyourfriend;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
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
		Utility.displayMessageOnScreen(context,
				"Your device registred with GCM");

		// if (MyProfileManager.getInstance().isLoaded()) {
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
				// Utility.showMessage(getApplicationContext(),
				// "Chức năng nhắn tin đã sẵn sàng");
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
		Log.i(TAG, "Received message");
		String message = intent.getExtras().getString("price");
		Utility.displayMessageOnScreen(context, message);

		// notifies user
		// generateNotification(context, message);
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		Utility.displayMessageOnScreen(context, message);
		// notifies user
		// generateNotification(context, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		Utility.displayMessageOnScreen(context,
				getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		Utility.displayMessageOnScreen(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

}
