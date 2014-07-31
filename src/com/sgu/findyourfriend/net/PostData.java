package com.sgu.findyourfriend.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.History;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.utils.FriendJSONParser;
import com.sgu.findyourfriend.utils.HistoryJSONParser;
import com.sgu.findyourfriend.utils.UserJSONParser;

public class PostData {

	public static final String TAG = PostData.class.getName();

	// **Appcode
	public static final String appCode = "AHC99-97JA8-AK009-AIJD1";

	// **Links
	public static final String host = "http://sgudev2014.comuv.com/";
	public static final String IMAGE_HOST = host + "uploads/images/";
	public static final String userManagerLink = host + "usermanager.php";
	public static final String accountManagerLink = host + "accountmanager.php";
	public static final String historyManagerLink = host + "historymanager.php";
	public static final String friendManagerLink = host + "friendmanager.php";
	public static final String chatsManagerLink = host + "chatsmanager.php";
	public static final String loginLink = host + "login.php";
	public static final String upLoadServerUri = host + "uploadtoserver.php";

	// **Define Types
	public static final String userCreateType = "CREATE";
	public static final String userEditType = "EDIT";
	public static final String userDeleteType = "DELETE";
	public static final String userGetUserListType = "GET_USER_LIST";
	public static final String userGetUserByIdType = "GET_USER_BY_ID";
	public static final String userGetUserStateType = "GET_USER_STATE";
	public static final String userChangePasswordType = "CHANGE_PASSWORD";
	public static final String userChangeAvatarType = "CHANGE_AVATAR";
	public static final String userGetUsersWithoutFriendType = "GET_NEW_USERLIST";
	public static final String userGetUsersByNameWithoutFriendType = "GET_USER_LIST_BY_NAME";

	public static final String accountCreateType = "CREATE";
	public static final String accountDeleteOneType = "DELETE_ONE";
	public static final String accountDeleteAllType = "DELETE_ALL";
	public static final String accountCheckExistType = "CHECK_EXIST_ACCOUNT";
	public static final String accountGetUserByNumberType = "GET_USER_BY_NUMBER";
	public static final String accountGetNumberByIdType = "GET_NUMBER_BY_ID";

	public static final String historyGetHistoryType = "GET_HISTORY";
	public static final String historyGetLastLocationType = "GET_LAST_LOCATION";
	public static final String historyCreateHistoryType = "CREATE_HISTORY";

	public static final String friendGetFriendListType = "GET_FRIEND_LIST";
	public static final String friendRemoveType = "FRIEND_REMOVE";

	public static final String chatsSendMsgType = "SEND_MSG";
	// public static final String chatsHelpMsgType = "HELP_MSG";
	public static final String sendFriendRequest = "SEND_FRIEND_REQUEST";
	public static final String sendFriendNotAccept = "SEND_FRIEND_NOT_ACCEPT";
	public static final String sendFriendAccept = "SEND_FRIEND_ACCEPT";
	public static final String sendShareRequest = "SEND_SHARE_REQUEST";
	public static final String sendShareNotAccept = "SEND_SHARE_NOT_ACCEPT";
	public static final String sendShareAccept = "SEND_SHARE_ACCEPT";

	public static final String shareRequest = "SHARE_REQUEST";
	public static final String friendRequest = "FRIEND_REQUEST";

	public static final String shareRespone = "SHARE_RESPONSE";
	public static final String friendRespone = "FRIEND_RESPONSE";

	public static int userCreate(Context ctx, String name, int gender,
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
		return Integer.parseInt(jsonResponse);
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
		return Integer.parseInt(jsonResponse) > 0;
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
		return Integer.parseInt(jsonResponse) > 0;
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

	public static ArrayList<User> userGetUsersByNameWithoutFriend(Context ctx,
			int myid, String name) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userGetUsersByNameWithoutFriendType);
		params.put("id", myid + "");
		params.put("name", name);
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

	public static HashMap<String, Integer> userGetUserListWithoutFriend(
			Context ctx, int myid, ArrayList<String> numberList) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		String numberStringList = "";
		for (String number : numberList) {
			numberStringList += ("." + number);
		}
		numberStringList = numberStringList.substring(1,
				numberStringList.length());
		params.put("appcode", appCode);
		params.put("type", userGetUsersWithoutFriendType);
		params.put("id", myid + "");
		params.put("numberlist", numberStringList);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		HashMap<String, Integer> numbersWithId = new HashMap<String, Integer>();
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			try {
				JSONArray json = new JSONArray(jsonResponse);
				for (int i = 0; i < json.length(); i++) {
					JSONArray a = new JSONArray(json.get(i).toString());
					numbersWithId.put(a.getString(0), a.getInt(1));
				}
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return numbersWithId;
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

	public static boolean userChangePassword(Context ctx, int id,
			String oldPass, String newPass) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userChangePasswordType);
		params.put("id", id + "");
		params.put("oldpass", oldPass);
		params.put("newpass", newPass);
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
		return Integer.parseInt(jsonResponse) > 0;
	}

	public static boolean userChangeAvatar(Context ctx, int id,
			String avatarlink) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userChangeAvatarType);
		params.put("id", id + "");
		params.put("avatar", avatarlink);
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
		return Integer.parseInt(jsonResponse) > 0;
	}

	public static boolean accountCreate(Context ctx, int id, String number) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", accountCreateType);
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
		return Integer.parseInt(jsonResponse) > 0;
	}

	public static User accountGetUserByNumber(Context ctx, String number) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", accountGetUserByNumberType);
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

	public static ArrayList<String> accountGetNumbersById(Context ctx, int id) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", accountGetNumberByIdType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		ArrayList<String> numbers = new ArrayList<String>();
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			try {
				JSONArray json = new JSONArray(jsonResponse);
				for (int i = 0; i < json.length(); i++) {
					numbers.add(json.getString(i));
				}
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return numbers;
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
		return Integer.parseInt(jsonResponse) > 0;
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
		return Integer.parseInt(jsonResponse) > 0;
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
		return Integer.parseInt(jsonResponse) > 0;
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

	public static LatLng historyGetLastUserLocation(Context ctx, int id) {
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
		return Integer.parseInt(jsonResponse) > 0;
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

	public static boolean friendRemove(Context ctx, int myid, int friendid) {
		String serverUrl = friendManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", friendRemoveType);
		params.put("uid", myid + "");
		params.put("fid", friendid + "");
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
		return Integer.parseInt(jsonResponse) > 0;
	}

	// public static boolean friendSetShare(Context ctx, int uid, int fid,
	// int share) {
	// String serverUrl = friendManagerLink;
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("appcode", appCode);
	// params.put("type", friendSetShareType);
	// params.put("uid", uid + "");
	// params.put("fid", fid + "");
	// params.put("share", share + "");
	// String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
	// params);
	// if (!jsonResponse.equals("")) {
	// // Remove ads from free host
	// String[] jssplit = jsonResponse
	// .split("\n<!-- Hosting24 Analytics Code -->");
	// jsonResponse = jssplit[0];
	// } else {
	// Log.i(TAG, "No result!");
	// }
	// return Integer.parseInt(jsonResponse)>0;
	// }

	// public static int friendGetShare(Context ctx, int uid, int fid) {
	// String serverUrl = friendManagerLink;
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("appcode", appCode);
	// params.put("type", friendGetShareType);
	// params.put("uid", uid + "");
	// params.put("fid", fid + "");
	// String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
	// params);
	// int share = -1;
	// if (!jsonResponse.equals("")) {
	// // Remove ads from free host
	// String[] jssplit = jsonResponse
	// .split("\n<!-- Hosting24 Analytics Code -->");
	// jsonResponse = jssplit[0];
	// jsonResponse = jsonResponse.substring(0, 1);
	// try {
	// share = Integer.parseInt(jsonResponse);
	// } catch (Exception e) {
	// Log.i(TAG, "Parse error");
	// }
	//
	// } else {
	// Log.i(TAG, "No result!");
	// }
	// return share;
	// }

	public static HashMap<String, Integer> sendMessage(Context ctx, int from,
			int to, String msg) {
		String serverUrl = chatsManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", chatsSendMsgType);
		params.put("senderid", from + "");
		params.put("recipientid", to + "");
		params.put("msg", msg);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		HashMap<String, Integer> report = new HashMap<String, Integer>();

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);
				report.put("success", json.getInt("success"));
				report.put("failure", json.getInt("failure"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return report;
	}

	public static HashMap<String, Integer> sendFriendRequest(Context ctx,
			int from, int to) {
		String serverUrl = chatsManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", sendFriendRequest);
		params.put("senderid", from + "");
		params.put("recipientid", to + "");
		params.put("msg", friendRequest + ":" + from + ":" + to);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		HashMap<String, Integer> report = new HashMap<String, Integer>();

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);
				report.put("success", json.getInt("success"));
				report.put("failure", json.getInt("failure"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return report;
	}

	public static HashMap<String, Integer> sendShareRequest(Context ctx,
			int from, int to) {
		String serverUrl = chatsManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", sendShareRequest);
		params.put("senderid", from + "");
		params.put("recipientid", to + "");
		params.put("msg", shareRequest + ":" + from + ":" + to);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		HashMap<String, Integer> report = new HashMap<String, Integer>();

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);
				report.put("success", json.getInt("success"));
				report.put("failure", json.getInt("failure"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return report;
	}

	public static boolean sendFriendAccept(Context ctx, int from, int to) {
		String serverUrl = chatsManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", sendFriendAccept);
		params.put("senderid", from + "");
		params.put("recipientid", to + "");
		params.put("msg", friendRespone + ":" + from + ":" + to + ":YES");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		// HashMap<String, Integer> report = new HashMap<String, Integer>();
		//
		// if (!jsonResponse.equals("")) {
		// try {
		// JSONObject json = new JSONObject(jsonResponse);
		// Log.i(TAG, jsonResponse);
		// report.put("success", json.getInt("success"));
		// report.put("failure", json.getInt("failure"));
		// } catch (JSONException e) {
		// // e.printStackTrace();
		// }
		// }
		try {
			return (new JSONObject(jsonResponse)).getInt("success") > 0;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean sendFriendNotAccept(Context ctx, int from, int to) {
		String serverUrl = chatsManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", sendFriendNotAccept);
		params.put("senderid", from + "");
		params.put("recipientid", to + "");
		params.put("msg", friendRespone + ":" + from + ":" + to + ":NO");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		// HashMap<String, Integer> report = new HashMap<String, Integer>();
		//
		// if (!jsonResponse.equals("")) {
		// try {
		// JSONObject json = new JSONObject(jsonResponse);
		// Log.i(TAG, jsonResponse);
		// report.put("success", json.getInt("success"));
		// report.put("failure", json.getInt("failure"));
		// } catch (JSONException e) {
		// // e.printStackTrace();
		// }
		// }
		try {
			return (new JSONObject(jsonResponse)).getInt("success") > 0;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static HashMap<String, Integer> sendShareAccept(Context ctx,
			int from, int to) {
		String serverUrl = chatsManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", sendShareAccept);
		params.put("senderid", from + "");
		params.put("recipientid", to + "");
		params.put("msg", shareRespone + ":" + from + ":" + to + ":YES");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		HashMap<String, Integer> report = new HashMap<String, Integer>();

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);
				report.put("success", json.getInt("success"));
				report.put("failure", json.getInt("failure"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return report;
	}

	public static HashMap<String, Integer> sendShareNotAccept(Context ctx,
			int from, int to) {
		String serverUrl = chatsManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", sendShareNotAccept);
		params.put("senderid", from + "");
		params.put("recipientid", to + "");
		params.put("msg", shareRespone + ":" + from + ":" + to + ":NO");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			// Remove ads from free host
			String[] jssplit = jsonResponse
					.split("\n<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		HashMap<String, Integer> report = new HashMap<String, Integer>();

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);
				report.put("success", json.getInt("success"));
				report.put("failure", json.getInt("failure"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return report;
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

	public static int uploadFile(String fullPath) {
		int serverResponseCode = 0;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(fullPath);

		if (!sourceFile.isFile()) {

			// dialog.dismiss();

			Log.e("uploadFile", "Source File not exist :" + fullPath);

			// runOnUiThread(new Runnable() {
			// public void run() {
			// messageText.setText("Source File not exist :"
			// +uploadFilePath + "" + uploadFileName);
			// }
			// });

			return 0;

		} else {
			try {

				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);
				URL url = new URL(upLoadServerUri);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fullPath);

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
						+ fullPath + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {

					// runOnUiThread(new Runnable() {
					// public void run() {
					//
					String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
							+ " http://www.androidexample.com/media/uploads/"
							+ fullPath;

					Log.i(TAG, msg);
					// messageText.setText(msg);
					// Toast.makeText(UploadToServer.this,
					// "File Upload Complete.",
					// Toast.LENGTH_SHORT).show();
					// }
					// });
				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				// dialog.dismiss();
				ex.printStackTrace();

				// runOnUiThread(new Runnable() {
				// public void run() {
				// messageText
				// .setText("MalformedURLException Exception : check script url.");
				// Toast.makeText(UploadToServer.this,
				// "MalformedURLException", Toast.LENGTH_SHORT)
				// .show();
				// }
				// });

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {

				// dialog.dismiss();
				e.printStackTrace();

				// runOnUiThread(new Runnable() {
				// public void run() {
				// messageText.setText("Got Exception : see logcat ");
				// Toast.makeText(UploadToServer.this,
				// "Got Exception : see logcat ",
				// Toast.LENGTH_SHORT).show();
				// }
				// });
				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}
			// dialog.dismiss();
			return serverResponseCode;

		} // End else block
	}
}