package com.sgu.findyourfriend.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.History;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostManager;
import com.sgu.findyourfriend.utils.FriendJSONParser;
import com.sgu.findyourfriend.utils.HistoryJSONParser;
import com.sgu.findyourfriend.utils.UserJSONParser;

public class PostDataBak {

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
	private static final String historyCreateHistoryType = "CREATE_HISTORY";

	private static final String friendCreateFriendType = "CREATE_FRIEND";
	private static final String friendGetFriendListType = "GET_FRIEND_LIST";
	private static final String friendSetShareType = "SET_SHARE";
	private static final String friendGetShareType = "GET_SHARE";

	
	// truyền đối tượng Friend
	// app code là mã máy/ khi cài ứng dụng tự sinh mã
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

	// 
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

	// get user hay friend (list)? lay tat ca user tu server
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

	// -------------------------------------------------------------------------------------------
	// user hay friend
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

	
	// du
	// trạng thái đã kết bạn/ đang đợi/ chưa kết bạn
	public static int userGetUserState(Context ctx, int id) {
		String serverUrl = userManagerLink;
		Map<String, String> params = new HashMap<String, String>();
		params.put("appcode", appCode);
		params.put("type", userGetUserStateType);
		params.put("id", id + "");
		String jsonResponse = PostManager.tryPostWithJsonResult(ctx, serverUrl,
				params);
		int state = -1;
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

	// tạo tài khoản mới
	// có kèm thông tin người dùng (user)
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

	
	// dùng trong trường hợp nào?
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

	
	// ktra nguoi dung su dung app
	
	// dùng trong trường hợp nào
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

	
	
	// delete account by id
	// các thông tin số điện thoại liên quan/ data base giải quyết thế nào
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

	
	// delete những gì
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

	
	
	// dùng khi người dùng chưa có tài khoản? diễn giải
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

	
	// lay his list 
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
	
	// update khi nào
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
	
	// có cần hàm lấy ds user
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
				list = (ArrayList<Friend>) FriendJSONParser.parse(json);
				Log.i(TAG, "Parse complete " + list.size());
			} catch (Exception e) {
				Log.d(TAG, "Parse JSON error!");
			}

		} else {
			Log.i(TAG, "No result!");
		}

		return list;
	}

	
	// gủi lời mời kết bạn
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

	// gửi y/cầu chia sẻ
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

	
	// du
	// ktra chia sẻ / đã có khi lấy ds friend và update?
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

	
	// chấp nhận y/c chia sẻ và kết bạn.
	
	
	
	public static void updateLastLoation(Friend friend, Marker m) {
		
		class FriendUpdate extends AsyncTask<Void, Void, Void> {

			@Override
			protected Void doInBackground(Void... params) {
				// update friend from internet
				
				
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				
				
				// get result
			}
			
		};
		
		(new FriendUpdate()).execute();
		
		// upadte friend's last position
		
		// m.setTitle(title);
		// m.setSnippet(snippet)
		
	}
}
