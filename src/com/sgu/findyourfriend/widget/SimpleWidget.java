package com.sgu.findyourfriend.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.screen.MainLoginActivity;
import com.sgu.findyourfriend.utils.PreferenceKeys;
import com.sgu.findyourfriend.utils.Utility;

public class SimpleWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context.getApplicationContext());
		ComponentName thisWidget = new ComponentName(
				context.getApplicationContext(), SimpleWidget.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		if (intent.getAction().equals(Config.DISPLAY_MESSAGE_ACTION)) {

			if (appWidgetIds != null && appWidgetIds.length > 0) {
				for (int widgetId : appWidgetIds) {

					String message = intent.getExtras().getString(
							Config.EXTRA_MESSAGE);

					RemoteViews remoteViews = new RemoteViews(context
							.getApplicationContext().getPackageName(),
							R.layout.main_widget_ui);

					// get instance prefs
					SettingManager.getInstance().init(context);

					if (Utility.verifyRequest(message)
							&& (Utility.getRequest(message).getType()
									.equals(Utility.FRIEND))) {
						// new friend request
						SettingManager.getInstance()
						.setNoNewRequest(
								SettingManager.getInstance()
										.getNoNewRequest() + 1);
						
						updateNewRequest(remoteViews);
					} else {
						// new message
						SettingManager.getInstance()
								.setNoNewMessage(
										SettingManager.getInstance()
												.getNoNewMesssage() + 1);

						// save message
						MessageManager.getInstance().quickSaveTempMessage(context, message);
						
						updateNewMessage(remoteViews);
					}
					appWidgetManager.updateAppWidget(widgetId, remoteViews);
				}
			}
			Toast.makeText(context, "the widget is updated", Toast.LENGTH_SHORT)
					.show();
		} else if (intent.getAction().equals(
				Config.UPDATE_MESSAGE_WIDGET_ACTION)) {
			if (appWidgetIds != null && appWidgetIds.length > 0) {
				for (int widgetId : appWidgetIds) {
					RemoteViews remoteViews = new RemoteViews(context
							.getApplicationContext().getPackageName(),
							R.layout.main_widget_ui);

					// get instance prefs
					SettingManager.getInstance().init(context);

					updateNewMessage(remoteViews);
					updateNewRequest(remoteViews);

					appWidgetManager.updateAppWidget(widgetId, remoteViews);
				}
			}
		}

		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// fetching our remote views
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.main_widget_ui);

		// get instance prefs
		SettingManager.getInstance().init(context);

		updateNewMessage(remoteViews);
		updateNewRequest(remoteViews);

		// emergency intent
		final Intent emerIntent = createIntent(context, appWidgetIds);
		emerIntent.putExtra(PreferenceKeys.EXTRA_EMERGENCY, true);
		emerIntent.setAction(PreferenceKeys.ACTION_START_EMERGENCY);
		final PendingIntent pendingEmergencyIntent = createPendingIntent(
				context, emerIntent);

		// update intent
		final Intent updateIntent = createIntent(context, appWidgetIds);
		updateIntent.putExtra(PreferenceKeys.EXTRA_EMERGENCY, false);
		updateIntent.setAction(PreferenceKeys.ACTION_START_UPDATE);
		final PendingIntent pendingUpdateIntent = createPendingIntent(context,
				updateIntent);

		// open main app
		Intent intentOpenApp = new Intent(context, MainLoginActivity.class);
		PendingIntent pendingOpenAppIntent = PendingIntent.getActivity(context,
				0, intentOpenApp, 0);

		remoteViews.setOnClickPendingIntent(R.id.imgNewMessage,
				pendingOpenAppIntent);

		remoteViews.setOnClickPendingIntent(R.id.imgNewRequest,
				pendingOpenAppIntent);

		remoteViews.setOnClickPendingIntent(R.id.txtMyAddress,
				pendingOpenAppIntent);

		remoteViews.setOnClickPendingIntent(R.id.bntEmergency,
				pendingEmergencyIntent);
		remoteViews
				.setOnClickPendingIntent(R.id.imgReload, pendingUpdateIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

		context.startService(updateIntent);
	}

	private Intent createIntent(Context context, int[] appWidgetIds) {
		Intent updateIntent = new Intent(context.getApplicationContext(),
				WidgetControlService.class);
		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
				appWidgetIds);
		return updateIntent;
	}

	private PendingIntent createPendingIntent(Context context,
			Intent updateIntent) {
		PendingIntent pendingIntent = PendingIntent.getService(
				context.getApplicationContext(), 0, updateIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	private void updateNewMessage(RemoteViews remoteViews) {
		if (SettingManager.getInstance().getNoNewMesssage() > 0)
			remoteViews.setImageViewResource(R.id.imgNewMessage,
					R.drawable.ic_message_small_trigger);
		else
			remoteViews.setImageViewResource(R.id.imgNewMessage,
					R.drawable.ic_message_small);

		remoteViews.setTextViewText(R.id.txtNewMessage, SettingManager
				.getInstance().getNoNewMesssage() + " mới");
	}

	private void updateNewRequest(RemoteViews remoteViews) {
		if (SettingManager.getInstance().getNoNewRequest() > 0)
			remoteViews.setImageViewResource(R.id.imgNewRequest,
					R.drawable.ic_check_small_trigger);
		else
			remoteViews.setImageViewResource(R.id.imgNewRequest,
					R.drawable.ic_check_small);

		remoteViews.setTextViewText(R.id.txtNewRequest,
				String.valueOf(SettingManager.getInstance().getNoNewRequest())
						+ " mới");
	}

}
