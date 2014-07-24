package com.sgu.findyourfriend.net;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.FriendRequest;
import com.sgu.findyourfriend.model.History;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.utils.FriendJSONParser;
import com.sgu.findyourfriend.utils.HistoryJSONParser;
import com.sgu.findyourfriend.utils.UserJSONParser;

public class PostData {

	public static final String TAG = PostData.class.getName();

	// **Appcode
	private static final String appCode = "AHC99-97JA8-AK009-AIJD1";

	// **Links
	private static final String userManagerLink = "http://truongtoan.uphero.com/usermanager.php";
	private static final String accountManagerLink = "http://truongtoan.uphero.com/accountmanager.php";
	private static final String historyManagerLink = "http://truongtoan.uphero.com/historymanager.php";
	private static final String friendManagerLink = "http://truongtoan.uphero.com/friendmanager.php";
	private static final String loginLink = "http://truongtoan.uphero.com/login.php";

	// **Define Types

	private static final String userCreateType = "CREATE";
	private static final String userEditType = "EDIT";
	private static final String userDeleteType = "DELETE";
	private static final String userGetUserListType = "GET_USER_LIST";
	private static final String userGetUserByNumberType = "GET_USER_BY_NUMBER";
	private static final String userGetUserByIdType = "GET_USER_BY_ID";
	private static final String userGetUserStateType = "GET_USER_STATE";

	private static final String accountCreateType = "CREATE";
	private static final String accountChangePasswordType = "CHANGE_PASSWORD";
	private static final String accountDeleteOneType = "DELETE_ONE";
	private static final String accountDeleteAllType = "DELETE_ALL";
	private static final String accountCheckExistType = "CHECK_EXIST_ACCOUNT";

	private static final String historyGetHistoryType = "GET_HISTORY";
	private static final String historyGetLastLocationType = "GET_LAST_LOCATION";
	private static final String historyCreateHistoryType = "CREATE_HISTORY";

	private static final String friendCreateFriendType = "CREATE_FRIEND";
	private static final String friendGetFriendListType = "GET_FRIEND_LIST";
	private static final String friendSetShareType = "SET_SHARE";
	private static final String friendGetShareType = "GET_SHARE";

	public static boolean userCreate(Context ctx, String name, int gender,
			String province, String email, String avatar, String gcmid) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userCreateType);
		params.put("name", name);
		params.put("gender", gender + "");
		params.put("province", province);
		params.put("email", email);
		params.put("avatar", avatar);
		params.put("gcmid", gcmid);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static boolean userEdit(Context ctx, int id, String name,
			int gender, String province, String email, String avatar,
			String gcmid) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userEditType);
		params.put("id", id + "");
		params.put("name", name);
		params.put("gender", gender + "");
		params.put("province", province);
		params.put("email", email);
		params.put("avatar", avatar);
		params.put("gcmid", gcmid);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];

		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static boolean userDelete(Context ctx, int id) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userDeleteType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static ArrayList<User> userGetUserList(Context ctx) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userGetUserListType);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		ArrayList<User> list = new ArrayList<User>();
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			try {
				JSONObject json = new JSONObject(jsonResponse);
				list = (ArrayList<User>) UserJSONParser.parse(json);
				Log.i(TAG, "Parse complete " + list.size());
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return list;
	}

	public static User userGetUserById(Context ctx, int id) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userGetUserByIdType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		User user = null;
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			try {
				JSONObject json = new JSONObject(jsonResponse);
				user = UserJSONParser.getUser(json);
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return user;
	}

	public static int userGetUserState(Context ctx, int id) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userGetUserStateType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		int state = 0;
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			try {
				state = Integer.parseInt(jsonResponse);
			} catch (Exception e) {
				Log.d(TAG, "Parse error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return state;
	}

	public static boolean accountCreate(Context ctx, int id, String number,
			String password) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", accountCreateType);
		params.put("id", id + "");
		params.put("number", number);
		params.put("password", password);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];

		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static boolean accountChangePassword(Context ctx, int id,
			String number, String password) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", accountChangePasswordType);
		params.put("id", id + "");
		params.put("number", number);
		params.put("password", password);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static User accountGetUserByNumber(Context ctx, String number) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userGetUserByNumberType);
		params.put("number", number);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		User user = null;
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			try {
				JSONObject json = new JSONObject(jsonResponse);
				user = UserJSONParser.getUser(json);
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return user;
	}

	public static boolean accountDeleteCurrentAccount(Context ctx, int id,
			String number) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", accountDeleteOneType);
		params.put("id", id + "");
		params.put("number", number);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static boolean accountDeleteAllAccount(Context ctx, int id) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", accountDeleteAllType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static boolean accountCheckExist(Context ctx, int id, String number) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", accountCheckExistType);
		params.put("id", id + "");
		params.put("number", number);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static ArrayList<History> historyGetUserHistory(Context ctx, int id) {
		String serverUrl = historyManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", historyGetHistoryType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		ArrayList<History> list = new ArrayList<History>();
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			try {
				JSONObject json = new JSONObject(jsonResponse);
				list = (ArrayList<History>) HistoryJSONParser.parse(json);
				Log.i(TAG, "Parse complete " + list.size());
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return list;
	}

	public static LatLng historyGetLastUserLacation(Context ctx, int id) {
		String serverUrl = historyManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", historyGetLastLocationType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		History hi = null;
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			try {
				JSONObject json = new JSONObject(jsonResponse);
				hi = HistoryJSONParser.getHistory(json);
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return hi == null ? null : hi.getLocation();
	}

	public static boolean historyCreate(Context ctx, int id, LatLng latlong) {
		String serverUrl = historyManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", historyCreateHistoryType);
		params.put("id", id + "");
		params.put("latitude", latlong.latitude + "");
		params.put("longtitude", latlong.longitude + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static ArrayList<Friend> friendGetFriendList(Context ctx, int id) {
		String serverUrl = friendManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", friendGetFriendListType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		ArrayList<Friend> list = new ArrayList<Friend>();
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			try {
				JSONObject json = new JSONObject(jsonResponse);
				list = FriendJSONParser.parse(json);
				Log.i(TAG, "Parse complete " + list.size());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return list;
	}

	public static boolean friendCreate(Context ctx, int uid, int fid) {
		String serverUrl = friendManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", friendCreateFriendType);
		params.put("uid", uid + "");
		params.put("fid", fid + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static boolean friendSetShare(Context ctx, int uid, int fid,
			int share) {
		String serverUrl = friendManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", friendSetShareType);
		params.put("uid", uid + "");
		params.put("fid", fid + "");
		params.put("share", share + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
		} else {
			Log.i(TAG, "No result!");
		}
		return (jsonResponse + "").equals("1");
	}

	public static int friendGetShare(Context ctx, int uid, int fid) {
		String serverUrl = friendManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", friendGetShareType);
		params.put("uid", uid + "");
		params.put("fid", fid + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		int share = -1;
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			jsonResponse = jsonResponse.substring(0, 1);
			try {
				share = Integer.parseInt(jsonResponse);
			} catch (Exception e) {
				Log.i(TAG, "Parse error");
			}

		} else {
			Log.i(TAG, "No result!");
		}
		return share;
	}

	public static int login(Context ctx, String number, String password) {
		String serverUrl = loginLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("number", number);
		params.put("password", password);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0].replaceAll("\"", "");
		} else {
			Log.i(TAG, "No result!");
		}
		Log.i(TAG, "User login: " + jsonResponse);
		if (jsonResponse.equals("")) {
			return -1;
		} else {
			return Integer.parseInt(jsonResponse);
		}
	}

	
	
	
	
	// ----------------- function require --------------------------//
	
	
	// get friend request list 
	// ham get friends co lấy tất cả bạn bè chưa?
	public static ArrayList<FriendRequest> getFriendRequestList(Context ctx, int id) {
		// some here
		
		
		
		return new ArrayList<FriendRequest>();
	}
	
	
	
	public static void sendMessage(Context ctx, int senderID, int receiverID, String msg) {
		
	}
	
	public static void acceptFriendRequest(Context ctx, int senderID, int receiverID) {
		// content message       ex:      FRIEND_ACCEPT + APPID
		
	}
	
	public static void acceptShareRequest(Context ctx, int senderID, int receiverID) {
		// content message       ex:      SHARE_ACCEPT + APPID
		
	}
	
	public static void friendRequest(Context ctx, int senderID, int receiverID) {
		// content message       ex:      FRIEND_REQUEST + APPID
		
	}
	
	public static void shareRequest(Context ctx, int senderID, int receiverID) {
		// content message        ex:     SHARE_REQUEST + APPID
		
	}
	
}
