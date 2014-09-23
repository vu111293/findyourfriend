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
package com.sgu.findyourfriend.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.History;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.model.SimpleUserAndLocation;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.utils.FriendJSONParser;
import com.sgu.findyourfriend.utils.HistoryJSONParser;
import com.sgu.findyourfriend.utils.MessageParser;
import com.sgu.findyourfriend.utils.SimpleUserAndLocationParser;
import com.sgu.findyourfriend.utils.UserJSONParser;

public class PostData {

	public static final String TAG = PostData.class.getName();

	// **Appcode
	public static final String appCode = "AHC99-97JA8-AK009-AIJD1";

	// **Links
	public static final String host = "http://sgulab.com/apps/findyourfriend/";
	public static final String IMAGE_HOST = host + "uploads/images/";
	public static final String userManagerLink = host + "usermanager.php";
	public static final String accountManagerLink = host + "accountmanager.php";
	public static final String historyManagerLink = host + "historymanager.php";
	public static final String friendManagerLink = host + "friendmanager.php";
	public static final String chatsManagerLink = host + "chatsmanager.php";
	public static final String loginLink = host + "login.php";
	public static final String upLoadServerUri = host + "uploadtoserver.php";
	public static final String helpServerUri = host;

	// **Define Types
	public static final String userCreateType = "CREATE";
	public static final String userCreatePassLoginType = "CREATE_LOGIN";
	public static final String userEditType = "EDIT";
	public static final String userDeleteType = "DELETE";
	public static final String userGetUserListType = "GET_USER_LIST";
	public static final String userGetUserByIdType = "GET_USER_BY_ID";
	public static final String userGetUserStateType = "GET_USER_STATE";
	public static final String userChangePasswordType = "CHANGE_PASSWORD";
	public static final String userChangeAvatarType = "CHANGE_AVATAR";
	public static final String userGetUsersWithoutFriendType = "GET_NEW_USERLIST";
	public static final String userUpdateGcmIdType = "UPDATE_GCMID";
	public static final String userSetPublicType = "SET_PUBLIC";
	public static final String userForgetPasswordTy­pe = "FORGET_PASSWORD";
	public static final String userGetProfileType = "GET_PROFILE";
	public static final String historyRemoveType = "REMOVE_HISTORY";
	public static final String chatsGetChatsType = "GET_CHATS";
	public static final String stopShare = "STOP_SHARE";
	public static final String chatsGetChatsByIdType = "GET_CHATS_BY_ID";

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
	private static final String friendGetFriendInfoType = "GET_FRIEND_BY_FID";

	public static final String chatsSendMsgType = "SEND_MSG";
	// public static final String chatsHelpMsgType = "HELP_MSG";
	public static final String sendFriendRequest = "SEND_FRIEND_REQUEST";
	public static final String sendFriendNotAccept = "SEND_FRIEND_NOT_ACCEPT";
	public static final String sendFriendAccept = "SEND_FRIEND_ACCEPT";
	public static final String sendShareRequest = "SEND_SHARE_REQUEST";
	public static final String sendShareNotAccept = "SEND_SHARE_NOT_ACCEPT";
	public static final String sendShareAccept = "SEND_SHARE_ACCEPT";
	public static final String accountCheckExistNumberType = "CHECK_EXIST_NUMBER";

	public static final String findInDistance = "FIND_IN_DISTANCE";
	public static final String findInCurAddress = "FIND_IN_CURRENT_ADDRESS";
	public static final String findInAddress = "FIND_IN_ADDRESS";

	public static final String userGetUsersBySthgWithoutFriendType = "GET_USER_LIST_BY_STHG";

	public static final String shareRequest = "SHARE_REQUEST";
	public static final String friendRequest = "FRIEND_REQUEST";

	public static final String shareRespone = "SHARE_RESPONSE";
	public static final String friendRespone = "FRIEND_RESPONSE";

	private static final String resendEmailType = "RESEND";

	// resend email
	public static int resendEmail(Context ctx, String phoneNumber) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", resendEmailType);
		params.put("number", phoneNumber);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		Log.i(TAG, jsonResponse);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse);
	}

	// ---------------------------- add new
	public static boolean userForgetPasswork(Context ctx, int id, String email) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userForgetPasswordTy­pe);
		params.put("id", id + "");
		params.put("email", email);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);

		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;

	}

	public static int userCreate(Context ctx, String name, String avatar,
			String gcmid, int gender, String address, String birthday,
			String school, String workplace, String email, String fblink,
			String password, boolean isPublic, String phoneNumber) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userCreateType);
		params.put("name", name);
		params.put("avatar", avatar);
		params.put("gcmid", gcmid);
		params.put("gender", gender + "");
		params.put("address", address);
		params.put("birthday", birthday);
		params.put("school", school);
		params.put("workplace", workplace);
		params.put("email", email);
		params.put("fblink", fblink);
		params.put("password", password);
		params.put("ispublic", isPublic ? "1" : "0");
		params.put("number", phoneNumber);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		Log.i(TAG, jsonResponse);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse);
	}

	public static int userCreatePasswordLogin(Context ctx, int id,
			String password) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userCreatePassLoginType);
		params.put("id", id + "");
		params.put("password", password);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		Log.i(TAG, jsonResponse);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse);
	}

	public static int userSetPublic(Context ctx, int id, int isPublic) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userSetPublicType);
		params.put("id", id + "");
		params.put("ispublic", isPublic + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		Log.i(TAG, jsonResponse);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse);
	}

	public static boolean userEdit(Context ctx, int id, String name,
			String avatar, String gcmid, int gender, String address,
			String birthday, String school, String workplace, String email,
			String fblink, boolean isPublic) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userEditType);
		params.put("id", id + "");
		params.put("name", name);
		params.put("avatar", avatar);
		params.put("gcmid", gcmid);
		params.put("gender", gender + "");
		params.put("address", address);
		params.put("birthday", birthday);
		params.put("school", school);
		params.put("workplace", workplace);
		params.put("email", email);
		params.put("fblink", fblink);
		params.put("ispublic", isPublic ? "1" : "0");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
	}

	public static boolean updateGcmId(Context ctx, int id, String gcmid) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userUpdateGcmIdType);
		params.put("id", id + "");
		params.put("gcmid", gcmid);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
	}

	public static boolean userDelete(Context ctx, int id) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userDeleteType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
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
			try {
				JSONObject json = new JSONObject(jsonResponse);
				user = UserJSONParser.getUser(json, false);
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
			jsonResponse = jsonResponse.replaceAll("\n", "");
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
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
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
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
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
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
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
			try {
				JSONObject json = new JSONObject(jsonResponse);
				user = UserJSONParser.getUser(json, false);
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
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
	}

	public static boolean accountDeleteAllAccount(Context ctx, int id) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", accountDeleteAllType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
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
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
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
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
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

	public static Friend friendGetFriend(Context ctx, int fid) {
		String serverUrl = friendManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", friendGetFriendInfoType);
		params.put("fid", fid + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		Friend f = null;
		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				f = FriendJSONParser.getFriend(json);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return f;
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
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
	}

	public static boolean sendMessage(Context ctx, int from, int to, String msg) {
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
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);
				return json.getInt("success") > 0;
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean sendFriendRequest(Context ctx, int from, int to) {
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
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);
				return json.getInt("success") > 0;
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean sendShareRequest(Context ctx, int from, int to) {
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
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);
				return json.getInt("success") > 0;
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return false;
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
			Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);

				return json.getInt("success") > 0;
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);

				return json.getInt("success") > 0;
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean sendShareAccept(Context ctx, int from, int to) {
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
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);
				return json.getInt("success") > 0;
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return false;
	}

	public boolean sendShareNotAccept(Context ctx, int from, int to) {
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
			// Log.i(TAG, jsonResponse);
		} else {
			Log.i(TAG, "No result!");
		}

		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				Log.i(TAG, jsonResponse);
				return json.getInt("success") > 0;
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return false;
	}

	public static int login(Context ctx, String number, String password) {
		String serverUrl = loginLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("number", number);
		params.put("password", password);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		Log.i(TAG, "User login: " + jsonResponse);
		if (jsonResponse.equals("")) {
			return -1;
		} else {
			jsonResponse = jsonResponse.replaceAll("\n", "");
			return mParseInt(jsonResponse);
		}
	}

	// -----------end add new
	@SuppressWarnings("resource")
	public static int uploadFile(Context ctx, String fullPath, String rename) {
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

		// getimage
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		// create a file to write bitmap data
		File f = new File(ctx.getCacheDir(), rename);
		if (f.exists()) {
			f.delete();
			f = new File(ctx.getCacheDir(), rename);
		}

		// Convert bitmap to byte array
		Bitmap bitmap = BitmapFactory.decodeFile(fullPath, options);

		// resize bitmap
		bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, bos);
		byte[] bitmapdata = bos.toByteArray();

		// write the bytes in file
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			fos.write(bitmapdata);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		fullPath = f.getPath();

		if (!sourceFile.isFile()) {
			Log.e("uploadFile", "Source File not exist :" + fullPath);
			return 0;

		} else {
			try {
				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(f);
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
				dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
						+ fullPath + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					Log.i("bytes", bytesRead + "");

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
					String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
							+ " http://www.androidexample.com/media/uploads/"
							+ fullPath;

					Log.i(TAG, msg);
				}

				// close the streams
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {
				ex.printStackTrace();
				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}
			return serverResponseCode;

		} // End else block
	}

	//
	// public static void upload(String selectedPath) throws IOException {
	//
	// HttpURLConnection connection = null;
	// DataOutputStream outputStream = null;
	// DataInputStream inputStream = null;
	//
	// Log.i(TAG, selectedPath);
	//
	// String pathToOurFile = selectedPath; //"/mnt/sdcard/temp_2014.png";
	// String urlServer = upLoadServerUri;
	// String lineEnd = "\r\n";
	// String twoHyphens = "--";
	// String boundary = "*****";
	//
	// int bytesRead, bytesAvailable, bufferSize;
	// byte[] buffer;
	// int maxBufferSize = 1 * 1024 * 1024;
	//
	// try {
	// FileInputStream fileInputStream = new FileInputStream(new File(
	// pathToOurFile));
	//
	// URL url = new URL(urlServer);
	// connection = (HttpURLConnection) url.openConnection();
	//
	// connection.setDoInput(true);
	// connection.setDoOutput(true);
	// connection.setUseCaches(false);
	//
	// connection.setRequestMethod("POST");
	//
	// connection.setRequestProperty("Connection", "Keep-Alive");
	// connection.setRequestProperty("Content-Type",
	// "multipart/form-data;boundary=" + boundary);
	//
	// outputStream = new DataOutputStream(connection.getOutputStream());
	// outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	// outputStream
	// .writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
	// + pathToOurFile + "\"" + lineEnd);
	// outputStream.writeBytes(lineEnd);
	//
	// bytesAvailable = fileInputStream.available();
	// bufferSize = Math.min(bytesAvailable, maxBufferSize);
	// buffer = new byte[bufferSize];
	//
	// bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	//
	// while (bytesRead > 0) {
	// Log.d(TAG, bytesRead + "");
	// outputStream.write(buffer, 0, bufferSize);
	// bytesAvailable = fileInputStream.available();
	// bufferSize = Math.min(bytesAvailable, maxBufferSize);
	// bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	// }
	//
	// outputStream.writeBytes(lineEnd);
	// outputStream.writeBytes(twoHyphens + boundary + twoHyphens
	// + lineEnd);
	//
	// int serverResponseCode = connection.getResponseCode();
	// String serverResponseMessage = connection.getResponseMessage();
	//
	// Log.i(TAG, "code " + serverResponseMessage + " " + serverResponseCode);
	//
	// fileInputStream.close();
	// outputStream.flush();
	// outputStream.close();
	// } catch (Exception ex) {
	// }
	// }
	
	private static int mParseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static boolean isPhoneRegisted(Context ctx, String number) {
		String serverUrl = accountManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", accountCheckExistNumberType);
		params.put("number", number);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
	}

	public static User userGetMyProfile(Context ctx, int id) {

		// Warning: Only for myself id!

		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userGetProfileType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		User user = null;
		if (!jsonResponse.equals("")) {
			try {
				JSONObject json = new JSONObject(jsonResponse);
				user = UserJSONParser.getUser(json, true);
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return user;
	}

	public static boolean userForgetPassword(Context ctx, String number) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userForgetPasswordTy­pe);
		params.put("number", number);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
	}

	public static ArrayList<SimpleUserAndLocation> findInDistance(Context ctx,
			Double yourlat, Double yourlng, Double distance) {

		Log.i(TAG, "find in distance " + distance);

		// Note: distance unit is meter (m)

		ArrayList<SimpleUserAndLocation> list = new ArrayList<SimpleUserAndLocation>();
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", findInDistance);
		params.put("lat", yourlat + "");
		params.put("lng", yourlng + "");
		params.put("d", distance + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);

		Log.i(TAG, "find in distance result: " + jsonResponse);
		if (!jsonResponse.equals("")) {
			try {
				Log.e(TAG, jsonResponse);
				JSONArray json = new JSONArray(jsonResponse);
				list = SimpleUserAndLocationParser.parse(json);
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}
		return list;
	}

	public static ArrayList<SimpleUserAndLocation> findInCurrentAddress(
			Context ctx, String address) {
		ArrayList<SimpleUserAndLocation> list = new ArrayList<SimpleUserAndLocation>();
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", findInCurAddress);
		params.put("addr", address);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		if (!jsonResponse.equals("")) {
			try {
				Log.e(TAG, jsonResponse);
				JSONArray json = new JSONArray(jsonResponse);
				list = SimpleUserAndLocationParser.parse(json);
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}
		return list;
	}

	public static ArrayList<User> userGetUsersByEveryThingWithoutFriend(
			Context ctx, int myid, String sthg) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userGetUsersBySthgWithoutFriendType);
		params.put("id", myid + "");
		params.put("sthg", sthg);
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		ArrayList<User> list = new ArrayList<User>();
		if (!jsonResponse.equals("")) {
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

	public static boolean historyRemove(Context ctx, int id) {
		String serverUrl = historyManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", historyRemoveType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
	}

	public static ArrayList<Message> getChatsById(Context ctx, int myid) {
		String serverUrl = chatsManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", chatsGetChatsByIdType);
		params.put("id", myid + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		jsonResponse = jsonResponse.replaceAll("\n", "");
		ArrayList<Message> msg = new ArrayList<Message>();
		if (!jsonResponse.equals("")) {
		} else {
			Log.i(TAG, "No result!");
		}

		if (!jsonResponse.equals("")) {
			try {
				JSONArray ar = new JSONArray(jsonResponse);
				msg = MessageParser.parse(ar);
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return msg;
	}

	public static boolean friendStopShare(Context ctx, int myid, int friendid) {
		String serverUrl = friendManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", stopShare);
		params.put("uid", myid + "");
		params.put("fid", friendid + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		Log.i("STOP SHARE", jsonResponse);

		jsonResponse = jsonResponse.replaceAll("\n", "");
		return mParseInt(jsonResponse) > 0;
	}

}