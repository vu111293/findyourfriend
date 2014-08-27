package com.sgu.findyourfriend.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.screen.MainActivity;
import com.sgu.findyourfriend.screen.MapFragment;

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

	public static final String[] sender = new String[] { "Lalit", "RobinHood",
			"Captain", "HotVerySpicy", "Dharmendra", "PareshMayani", "Abhi",
			"SpK", "CapDroid" };
	public static final String[] messages = new String[] { "Aah! thats cool",
			"Tu really CoLor 6e", "Get Lost!!", "@AdilSoomro @AdilSoomro",
			"Lets see what the Rock is cooking..!!", "Yeah! thats great.",
			"Awesome Awesome!", "@RobinHood.", "Lalit ka Dillllll...!!!",
			"I'm fine, thanks, what about you?" };

	public static final String INVITE_MESSAGE = "Bạn có muốn sử dụng app.";

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
//		if (secs >= 0)
//			builder.append(" " + secs + "giây");
		return builder.toString();
	}

	public static void showMessage(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showMessageOnCenter(Context ctx, String msg) {
		Toast toast = Toast.makeText(ctx, msg,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
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

	public static void showAlertDialog(Context context, String title,
			String message, Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Set Dialog Title
		alertDialog.setTitle(title);

		// Set Dialog Message
		alertDialog.setMessage(message);

		if (status != null)
			// Set alert dialog icon
			// alertDialog
			// .setIcon((status) ? R.drawable.success : R.drawable.fail);

			// Set OK Button
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});

		// Show Alert Message
		alertDialog.show();
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
		int w = source.getWidth();
		int h = source.getHeight();
		int r = Math.min(w, h);
		return Bitmap.createBitmap(source, (w - r) / 2, (h - r) / 2, r, r);
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

	public static BitmapDescriptor combileLocationIcon(Context ctx, final Friend f,
			boolean isSelected) {
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

	private static BitmapDescriptor combileBorder(Bitmap bmAvatar, Bitmap brbitmap) {
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
				drawable.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
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
						return true;
					}

		}
		return false;
	}

	// debug method
	// Notifies UI to display a message.
	public static void displayMessageOnScreen(Context context, String message) {
		Intent intent = new Intent(Config.DISPLAY_MESSAGE_ACTION);
		intent.putExtra(Config.EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
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

		Intent notificationIntent = new Intent(context, MainActivity.class);
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
}
