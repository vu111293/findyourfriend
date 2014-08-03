package com.sgu.findyourfriend.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.Toast;

/**
 * Utility is a just an ordinary class to have some Utility methods
 * 
 * @author Adil Soomro
 * 
 */

public class Utility {

	// public static enum TYPE {
	// SHARE, FRIEND, MESSAGE, REQUEST, RESPONSE_YES, RESPONSE_NO
	// };
	//

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
			builder.append(" " + days + "d");
		if (hours > 0)
			builder.append(" " + hours + "h");
		if (mins > 0)
			builder.append(" " + mins + "'");
		if (secs > 0)
			builder.append(" " + secs + "''");
		return builder.toString();
	}

	public static void showMessage(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
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

}
