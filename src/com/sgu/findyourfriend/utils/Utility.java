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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.screen.MainActivity;
import com.sgu.findyourfriend.screen.MainLoginActivity;

/**
 * Utility is a just an ordinary class to have some Utility methods
 * 
 * @author Adil Soomro
 * 
 */

public class Utility {

	public static String SHARE = "com.sgu.findyourfriend.share";
	public static String FRIEND = "com.sgu.findyourfriend.friend";
	public static String MESSAGE = "com.sgu.findyourfriend.message";
	public static String REQUEST = "com.sgu.findyourfriend.request";
	public static String RESPONSE_YES = "com.sgu.findyourfriend.resposeyes";
	public static String RESPONSE_NO = "com.sgu.findyourfriend.responseno";
	public static String REMOVE = "com.sgu.findyourfriend.remove";

	public static String FRIEND_ID = "friendId";
	public static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

	public static final String[] sender = new String[] { "Lalit", "RobinHood",
			"Captain", "HotVerySpicy", "Dharmendra", "PareshMayani", "Abhi",
			"SpK", "CapDroid" };
	public static final String[] messages = new String[] { "Aah! thats cool",
			"Tu really CoLor 6e", "Get Lost!!", "@AdilSoomro @AdilSoomro",
			"Lets see what the Rock is cooking..!!", "Yeah! thats great.",
			"Awesome Awesome!", "@RobinHood.", "Lalit ka Dillllll...!!!",
			"I'm fine, thanks, what about you?" };

	public static final String INVITE_MESSAGE = "Bạn có muốn sử dụng app.";

	// dd/mm/yyyy
	public static String getSimpleDate(Date d) {
		return format.format(d);
	}

	// example: 30' 10s
	public static String convertMicTimeToString(Long mics) {
		return convertSecTimeToString(mics / 1000);
	}

	public static String convertSecTimeToString(Long sumSecs) {
		// int sumSecs = (int) (mics / 1000);
		int days = (int) (sumSecs / (24 * 60 * 60));
		sumSecs %= (24 * 60 * 60);
		int hours = (int) (sumSecs / (60 * 60));
		sumSecs %= (60 * 60);
		int mins = (int) (sumSecs / 60);
		sumSecs %= 60;
		Long secs = sumSecs;

		StringBuilder builder = new StringBuilder();
		if (days > 0)
			builder.append(" " + days + " ngày");
		if (hours > 0)
			builder.append(" " + hours + " giờ");
		if (mins >= 0)
			builder.append(" " + mins + " phút");
		// if (secs >= 0)
		// builder.append(" " + secs + "giây");
		return builder.toString();
	}

	public static void showMessage(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showMessageOnCenter(Context ctx, String msg) {
		Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,
				0, 0);
		toast.show();
	}

	// verify request
	public static boolean verifyRequest(String msg) {
		if (null == msg)
			return false;
		String res[] = msg.split(":");
		if (res.length != 3)
			return false;

		return res[0].endsWith("REQUEST");

	}

	// verify response
	public static boolean verifyResponse(String msg) {
		if (null == msg)
			return false;
		String res[] = msg.split(":");
		if (res.length != 4)
			return false;

		return res[0].endsWith("RESPONSE");
	}

	public static RReply getRequest(String msg) {

		String res[] = msg.split(":");

		if (res.length != 3)
			return null;

		String type = res[0].startsWith("FRIEND") ? FRIEND : SHARE;

		return (new Utility()).new RReply(type, Integer.parseInt(res[1]),
				Integer.parseInt(res[2]), true);
	}

	public static RReply getResponse(String msg) {

		String res[] = msg.split(":");

		if (res.length != 4)
			return null;

		String type = res[0].startsWith("FRIEND") ? FRIEND : SHARE;

		return (new Utility()).new RReply(type, Integer.parseInt(res[1]),
				Integer.parseInt(res[2]), res[3].equals("YES"));
	}

	// public static void showAlertDialog(Context context, String title,
	// String message, Boolean status) {
	// AlertDialog alertDialog = new AlertDialog.Builder(context).create();
	//
	// // Set Dialog Title
	// alertDialog.setTitle(title);
	//
	// // Set Dialog Message
	// alertDialog.setMessage(message);
	//
	// if (status != null)
	// // Set alert dialog icon
	// // alertDialog
	// // .setIcon((status) ? R.drawable.success : R.drawable.fail);
	//
	// // Set OK Button
	// alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	//
	// }
	// });
	//
	// // Show Alert Message
	// alertDialog.show();
	// }

	public static int ERROR = 0;
	public static int CONFIRM = 1;
	public static int WARNING = 2;

	public static void showDialog(int type, final Dialog dialog, String title,
			String content, String titleBtnLeft, OnClickListener leftOnClick,
			String titleBtnRight, OnClickListener rightOnClick) {
		showBaseDialog(type, dialog, title, content, titleBtnLeft, leftOnClick,
				titleBtnRight, rightOnClick, R.layout.dialog_custom);
	}

	public static void showScrollerDialog(int type, final Dialog dialog,
			String title, String content, String titleBtnLeft,
			OnClickListener leftOnClick, String titleBtnRight,
			OnClickListener rightOnClick) {
		showBaseDialog(type, dialog, title, content, titleBtnLeft, leftOnClick,
				titleBtnRight, rightOnClick, R.layout.dialog_scroller_custom);
	}

	public static void showBaseDialog(int type, final Dialog dialog,
			String title, String content, String titleBtnLeft,
			OnClickListener leftOnClick, String titleBtnRight,
			OnClickListener rightOnClick, int layoutId) {

		Button leftBtn, rightBtn;
		TextView txtTitle;
		TextView txtContent;

		Window W = dialog.getWindow();
		W.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		W.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		W.requestFeature(Window.FEATURE_NO_TITLE);
		W.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(layoutId);

		txtTitle = (TextView) dialog.findViewById(R.id.title);
		txtContent = (TextView) dialog.findViewById(R.id.content);
		leftBtn = (Button) dialog.findViewById(R.id.btnLeft);
		rightBtn = (Button) dialog.findViewById(R.id.btnRight);

		if (type == ERROR)
			txtTitle.setBackgroundColor(dialog.getContext().getResources()
					.getColor(R.color.red));
		else if (type == CONFIRM)
			txtTitle.setBackgroundColor(dialog.getContext().getResources()
					.getColor(R.color.green));
		else if (type == WARNING)
			txtTitle.setBackgroundColor(dialog.getContext().getResources()
					.getColor(R.color.yellow));

		txtTitle.setText(title);
		txtContent.setText(Html.fromHtml(content));

		// confirm default
		if (null == leftOnClick) {
			rightBtn.setVisibility(View.GONE);
			leftBtn.setText("Đóng");
			leftBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			// modify
		} else {

			leftBtn.setText(titleBtnLeft);
			leftBtn.setOnClickListener(leftOnClick);

			if (null == rightOnClick) {
				rightBtn.setVisibility(View.GONE);
			} else {
				rightBtn.setText(titleBtnRight);
				rightBtn.setOnClickListener(rightOnClick);
			}
		}

		dialog.show();
	}

	public static void showDialog(int type, final Dialog dialog, String title,
			String content, String titleBtn, OnClickListener onClick) {
		showDialog(type, dialog, title, content, titleBtn, onClick, "", null);
	}

	public static void showDialog(int type, final Dialog dialog, String title,
			String content) {
		showDialog(type, dialog, title, content, "", null, "", null);

	}

	public static void showScrollerDialog(int type, final Dialog dialog,
			String title, String content, String titleBtn,
			OnClickListener onClick) {
		showScrollerDialog(type, dialog, title, content, titleBtn, onClick, "",
				null);
	}

	public static void showScrollerDialog(int type, final Dialog dialog,
			String title, String content) {
		showScrollerDialog(type, dialog, title, content, "", null, "", null);

	}

	public static void showListDialog(int type, final Dialog dialog,
			String title, ArrayList<String> data,
			OnItemClickListener onItemClick) {

		ListView lv;
		Button btnClose;
		TextView txtTitle;

		Window W = dialog.getWindow();
		W.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		W.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		W.requestFeature(Window.FEATURE_NO_TITLE);
		W.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.dialog_list_custom);

		txtTitle = (TextView) dialog.findViewById(R.id.title);
		btnClose = (Button) dialog.findViewById(R.id.btnClose);
		lv = (ListView) dialog.findViewById(R.id.lv);

		if (type == ERROR)
			txtTitle.setBackgroundColor(dialog.getContext().getResources()
					.getColor(R.color.red));
		else if (type == CONFIRM)
			txtTitle.setBackgroundColor(dialog.getContext().getResources()
					.getColor(R.color.green));
		else if (type == WARNING)
			txtTitle.setBackgroundColor(dialog.getContext().getResources()
					.getColor(R.color.yellow));

		txtTitle.setText(title);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				dialog.getContext(), R.layout.item_option_list, data);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(onItemClick);

		btnClose = (Button) dialog.findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public class RReply {
		private String type;
		private int fromId;
		private int toId;
		private boolean res;

		public RReply(String type, int fromId, int toId, boolean res) {
			this.setType(type);
			this.setFromId(fromId);
			this.setToId(toId);
			this.setRes(res);
		}

		public int getFromId() {
			return fromId;
		}

		public void setFromId(int fromId) {
			this.fromId = fromId;
		}

		public int getToId() {
			return toId;
		}

		public void setToId(int toId) {
			this.toId = toId;
		}

		public boolean isRes() {
			return res;
		}

		public void setRes(boolean res) {
			this.res = res;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	// --------------------- utility image -----------------------------

	public static Bitmap dropRectBitmap(Bitmap source) {

		if (source.getWidth() > 200) {
			source = scaleDown(source, 200, true);
		}

		int w = source.getWidth();
		int h = source.getHeight();
		int r = Math.min(w, h);

		return Bitmap.createBitmap(source, (w - r) / 2, (h - r) / 2, r, r);
	}

	public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
			boolean filter) {
		float ratio = Math.min((float) maxImageSize / realImage.getWidth(),
				(float) maxImageSize / realImage.getHeight());
		int width = Math.round((float) ratio * realImage.getWidth());
		int height = Math.round((float) ratio * realImage.getHeight());

		Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height,
				filter);
		return newBitmap;
	}

	public static Bitmap rotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	public static Bitmap writeTextOnDrawable(Context context, int drawableId,
			String text) {

		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				drawableId).copy(Bitmap.Config.ARGB_8888, true);

		Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTypeface(tf);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(convertToPixels(context, 18));

		Rect textRect = new Rect();
		paint.getTextBounds(text, 0, text.length(), textRect);

		Canvas canvas = new Canvas(bm);

		// If the text is bigger than the canvas , reduce the font size
		if (textRect.width() >= (canvas.getWidth() - 4))
			paint.setTextSize(convertToPixels(context, 7));

		// Calculate the positions
		int xPos = (canvas.getWidth() / 2) - 2;

		// baseline to the center.
		int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint
				.ascent()) / 2));

		canvas.drawText(text, xPos, yPos, paint);

		return bm;
	}

	public static int convertToPixels(Context context, int nDP) {
		final float conversionScale = context.getResources()
				.getDisplayMetrics().density;

		return (int) ((nDP * conversionScale) + 0.5f);

	}

	public static BitmapDescriptor combileLocationIcon(Context ctx,
			final Friend f, boolean isSelected) {
		final Bitmap brbitmap;

		if (isSelected)
			brbitmap = BitmapFactory.decodeResource(ctx.getResources(),
					R.drawable.boder_location_selected);
		else
			brbitmap = BitmapFactory.decodeResource(ctx.getResources(),
					R.drawable.boder_location_unselected);

		return combileBorder(
				drawableToBitmap(FriendManager.getInstance().hmImageP.get(f
						.getUserInfo().getId())), brbitmap);
	}

	private static BitmapDescriptor combileBorder(Bitmap bmAvatar,
			Bitmap brbitmap) {
		int w = bmAvatar.getWidth();
		int h = bmAvatar.getHeight();

		int newWidth = 70;
		int newHeight = 70;

		float scaleWidth = ((float) newWidth) / w;
		float scaleHeight = ((float) newHeight) / h;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap bmAvatarResize = Bitmap.createBitmap(bmAvatar, 0, 0, w, h,
				matrix, false);
		return BitmapDescriptorFactory.fromBitmap(overlay(brbitmap,
				bmAvatarResize));
	}

	private static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
				bmp1.getHeight(), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);
		canvas.drawBitmap(bmp2, (canvas.getWidth() - bmp2.getWidth()) / 2,
				(canvas.getWidth() - bmp2.getWidth()) / 2, null);
		return bmOverlay;
	}

	private static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(),
				android.graphics.Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	// Checking for all possible internet providers
	public static boolean isConnectingToInternet(Context ctx) {

		ConnectivityManager connectivity = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						Config.currentState = Config.AppState.ONLINE;
						return true;
					}

		}
		Config.currentState = Config.AppState.OFFLINE;
		return false;
	}

	public static boolean checkConnectToNetworkForceQuit(final Activity act) {
		if (!isConnectingToInternet(act)) {
			final Dialog dialog = new Dialog(act);
			showDialog(ERROR, dialog, "Lỗi kết nối mạng",
					"Đóng ứng dụng, kiểm tra kết nối mạng và thử lại", "Đóng",
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							act.finish();
						}
					});

			return false;
		}

		return true;
	}

	public static boolean checkConnectToNetworkContinue(Context ctx) {
		if (!isConnectingToInternet(ctx)) {
			final Dialog dialog = new Dialog(ctx);
			showDialog(ERROR, dialog, "Lỗi kết nối mạng",
					"Bật kết nối mạng và thử lại", "Đóng",
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();

						}
					});

			return false;
		}

		return true;
	}

	// debug method
	// Notifies UI to display a message.
	public static void displayMessageOnScreen(Context context, String message) {
		Intent intent = new Intent(Config.DISPLAY_MESSAGE_ACTION);
		intent.putExtra(Config.EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	public static void displayNotification(final Context context,
			final String message) {

		if (Utility.verifyRequest(message)) {
			// check request message
			final RReply reply = Utility.getRequest(message);

			(new AsyncTask<Void, Void, Friend>() {

				@Override
				protected Friend doInBackground(Void... params) {
					return PostData.friendGetFriend(context, reply.getFromId());
				}

				@Override
				protected void onPostExecute(Friend fr) {

					if (reply.getType().equals(Utility.FRIEND))
						Utility.generateNotification(context,
								"Một yêu cầu kết bạn từ "
										+ fr.getUserInfo().getName(), "");
					else if (reply.getType().equals(Utility.SHARE)) {
						Utility.generateNotification(context,
								"Một yêu cầu chia sẻ từ "
										+ fr.getUserInfo().getName(), "");
					}
				}

			}).execute();

		} else if (Utility.verifyResponse(message)) {
			// check response message
			final RReply reply = Utility.getResponse(message);

			(new AsyncTask<Void, Void, Friend>() {

				@Override
				protected Friend doInBackground(Void... params) {
					return PostData.friendGetFriend(context, reply.getFromId());
				}

				@Override
				protected void onPostExecute(Friend fr) {

					if (reply.isRes()) {
						if (reply.getType().equals(Utility.FRIEND))
							Utility.generateNotification(context, fr
									.getUserInfo().getName()
									+ " đã là bạn của bạn.", "");
						else if (reply.getType().equals(Utility.SHARE))
							Utility.generateNotification(context, fr
									.getUserInfo().getName()
									+ " đã chấp nhận yêu cầu chia sẻ.", "");
					} else {
						if (reply.getType().equals(Utility.FRIEND))
							Utility.generateNotification(context, fr
									.getUserInfo().getName()
									+ " đã từ chối yêu cầu kết bạn.", "");
						else if (reply.getType().equals(Utility.SHARE))
							Utility.generateNotification(context, fr
									.getUserInfo().getName()
									+ " đã từ chối yêu cầu chia sẻ.", "");
					}
				}

			}).execute();

		} else {
			// check normal message

			(new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {

					int idSender = getSenderMessage(message);

					if (idSender == 0)
						return Config.ADMIN_NAME;

					return PostData.friendGetFriend(context, idSender)
							.getUserInfo().getName();
				}

				@Override
				protected void onPostExecute(String name) {

					Utility.generateNotification(context,
							"Bạn có một tin nhắn từ " + name, "");
				}
			}).execute();

		}

	}

	private static PowerManager.WakeLock wakeLock;

	public static void acquireWakeLock(Context context) {
		if (wakeLock != null)
			wakeLock.release();

		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "WakeLock");

		wakeLock.acquire();
	}

	public static void releaseWakeLock() {
		if (wakeLock != null)
			wakeLock.release();
		wakeLock = null;
	}

	/**
	 * Create a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	public static void generateNotification(Context context, String title,
			String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		// String title = context.getString(R.string.app_name);

		Intent notificationIntent = null;

		if (isActivityRunning(context, MainActivity.class))
			notificationIntent = new Intent(context, MainActivity.class);
		else
			notificationIntent = new Intent(context, MainLoginActivity.class);

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		SettingManager.getInstance().init(context);
		if (SettingManager.getInstance().isMessageRingtone()) {
			String uriRingtone = SettingManager.getInstance().getRingtoneUri();

			if (!uriRingtone.equals("")) {
				notification.sound = Uri.parse(uriRingtone);
			} else {
				notification.defaults |= Notification.DEFAULT_SOUND;
			}
			Log.i("NOTIFY", uriRingtone);
		}

		// Vibrate if vibrate is enabled
		if (SettingManager.getInstance().isVibrate())
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

	// validation
	public static boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	public static boolean isPhoneNumberCorrect(String pPhoneNumber) {
		Pattern pattern = Pattern
				.compile("((\\+[1-9]{3,4}|0[1-9]{4}|00[1-9]{3})\\-?)?\\d{8,20}");
		Matcher matcher = pattern.matcher(pPhoneNumber);

		if (matcher.matches())
			return true;

		return false;
	}

	public static File savebitmap(Bitmap bmp) {
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();
		OutputStream outStream = null;
		// String temp = null;
		File file = new File(extStorageDirectory, "temp_2014.png");
		if (file.exists()) {
			file.delete();
			file = new File(extStorageDirectory, "temp_2014.png");

		}

		try {
			outStream = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return file;
	}

	public static String getPath(Context ctx, Uri uri) {
		// just some safety built in
		if (uri == null) {
			// TODO perform some logging or show user feedback
			return null;
		}
		// try to retrieve the image from the media store first
		// this will only work for images selected from gallery
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = ((Activity) ctx).managedQuery(uri, projection, null,
				null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		// this is our fell back here
		return uri.getPath();
	}

	// require system check

	// ------------- map utilities
	// ------------------- zoom control ----------------------- //

	public static void zoomBoundPosition(List<LatLng> latlngs, GoogleMap mMap) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		for (LatLng latlng : latlngs) {
			builder.include(latlng);
		}

		LatLngBounds bounds = builder.build();

		int padding = 100; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

		mMap.animateCamera(cu);
	}

	public static void zoomToPosition(LatLng latLng, GoogleMap mMap) {
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 8);
		mMap.animateCamera(cu);
	}

	public static void zoomToPosition(Location location, GoogleMap mMap) {
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(
				new LatLng(location.getLatitude(), location.getLongitude()), 8);
		mMap.animateCamera(cu);
	}

	public static String getAddress(Context context, LatLng point) {
		if (!isConnectingToInternet(context))
			return "Chỉ xem vị trí khi kết nối mạng.";

		try {
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(context);
			if (point.latitude != 0 || point.longitude != 0) {
				addresses = geocoder.getFromLocation(point.latitude,
						point.longitude, 1);

				if (null == addresses)
					return "Không có sẵn.";

				Address address = addresses.get(0);

				String addressText = String.format(
						"%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address
								.getCountryName());

				return addressText;
			} else {
				// Toast.makeText(context, "latitude and longitude are null",
				// Toast.LENGTH_LONG).show();
				return "Không có sẵn.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// -------------------------- message parse
	public static Message parseMessage(String newMessage) {
		int idxPattern = newMessage.indexOf(Config.PARTERN_GET_MESSAGE);
		int senderId;
		String pureMessage;
		String name;
		LatLng location = null;

		if (idxPattern >= 0) {
			senderId = Integer.parseInt(newMessage.substring(0, idxPattern));
			pureMessage = newMessage.substring(idxPattern
					+ Config.PARTERN_GET_MESSAGE.length());
			int idLocation = pureMessage
					.indexOf(Config.PREFIX_LOCATION_IN_MESSAGE);
			if (idLocation >= 0) {
				String strLoc[] = pureMessage.substring(idLocation).split(" ");
				if (strLoc.length == 3)
					location = new LatLng(Double.parseDouble(strLoc[1]),
							Double.parseDouble(strLoc[2]));

				pureMessage = pureMessage.substring(0, idLocation);
			}

		} else {
			senderId = 0;
			pureMessage = newMessage;
		}

		name = senderId == 0 ? Config.ADMIN_NAME : FriendManager.getInstance()
				.getNameFriend(senderId);

		return new Message(pureMessage, senderId == MyProfileManager
				.getInstance().getMyID(), senderId, name, MyProfileManager
				.getInstance().getMyID(), MyProfileManager.getInstance()
				.getMyName(), location, new Date(System.currentTimeMillis()));
	}

	public static Message parseMessage(String newMessage, Friend sender,
			Friend receiver) {
		int idxPattern = newMessage.indexOf(Config.PARTERN_GET_MESSAGE);
		int senderId;
		String pureMessage;
		String name;
		LatLng location = null;

		if (idxPattern >= 0) {
			senderId = Integer.parseInt(newMessage.substring(0, idxPattern));
			pureMessage = newMessage.substring(idxPattern
					+ Config.PARTERN_GET_MESSAGE.length());
			int idLocation = pureMessage
					.indexOf(Config.PREFIX_LOCATION_IN_MESSAGE);
			if (idLocation >= 0) {
				String strLoc[] = pureMessage.substring(idLocation).split(" ");
				if (strLoc.length == 3)
					location = new LatLng(Double.parseDouble(strLoc[1]),
							Double.parseDouble(strLoc[2]));

				pureMessage = pureMessage.substring(0, idLocation);
			}

		} else {
			senderId = 0;
			pureMessage = newMessage;
		}

		name = null == sender ? Config.ADMIN_NAME : sender.getUserInfo()
				.getName();

		return new Message(pureMessage, senderId == receiver.getUserInfo()
				.getId(), senderId, name, receiver.getUserInfo().getId(),
				receiver.getUserInfo().getName(), location, new Date(
						System.currentTimeMillis()));
	}

	public static int getSenderMessage(String message) {
		int idxPattern = message.indexOf(Config.PARTERN_GET_MESSAGE);
		int senderId;

		if (idxPattern >= 0) {
			senderId = Integer.parseInt(message.substring(0, idxPattern));
		} else {
			senderId = 0;
		}

		return senderId;
	}

	public static boolean isActivityRunning(Context ctx, Class activityClass) {
		ActivityManager activityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = activityManager
				.getRunningTasks(Integer.MAX_VALUE);

		for (ActivityManager.RunningTaskInfo task : tasks) {
			if (activityClass.getCanonicalName().equalsIgnoreCase(
					task.baseActivity.getClassName()))
				return true;
		}

		return false;
	}

}
