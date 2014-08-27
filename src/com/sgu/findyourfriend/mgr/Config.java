package com.sgu.findyourfriend.mgr;

public interface Config {

	static final boolean MODE_OFFLINE = false;
	
	
	// CONSTANTS
	static final String YOUR_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/register.php";

	static final String SEND_MESSAGE_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/send_message.php";

	static final String SEND_LOCATION_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/send_location.php";

	static final String GET_FRIENDS_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/get_friend_list.php";

	static final String GET_LOCATION_HISTORY_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/get_locations_history.php";

	// Google project id
	static final String GOOGLE_SENDER_ID = "100128594910";

	// cofig
	static final int MIN_PASSWORD_LENGHT = 6;
	static final int OLD_LIMIT = 10;
	static final long MINS_OF_YEAR = 315360000000l;
	static final String DEFAULT_PASSWORD = "123456";

	
	// post state
	public static int PHONE_REGISTED = 0;
	public static int ERROR_REGIST = 1; // chua kich hoat
	public static int SUCCESS = 2;
	public static int ERROR_NOT_FOUND = 3;
	public static int ERROR = 4;
	
	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCM Android Example";

	static final String DISPLAY_MESSAGE_ACTION = "com.androidexample.gcm.DISPLAY_MESSAGE";
	static final String UPDATE_MESSAGE_WIDGET_ACTION = "com.androidexample.gcm.UPDATE_MESSAGE_WIDGET";
	static final String LOCAL_MESSAGE_ACTION = "com.sgu.findyourfriend.LOCAL_MESSAGE";

	// call action intent
	static final String UPDATE_UI = "com.sgu.findyourfriend.UPDATE_UI";
	static final String UPDATE_TYPE = "com.sgu.findyourfriend.UPDATE_TYPE";
	static final String UPDATE_ACTION = "com.sgu.findyourfriend.UPDATE_ACTION";

	static final String MAIN_ACTION = "com.sgu.findyourfriend.MAIN_ACTION";
	static final String EDIT_MESSAGE_ACTION = "com.sgu.findyourfriend.EDIT_MEESAGE";
	static final String ZOOM_POSITION_ACTION = "com.sgu.findyourfriend.ZOOM_POSITION";
	static final String ROUTE_ACTION = "com.sgu.findyourfriend.ROUTE";
	static final String HISTORY_ACTION = "com.sgu.findyourfriend.HISTORY";

	static final String NOTIFY_UI = "com.sgu.findyourfriend.NOTIFY_UI";
	static final String MESSAGE_NOTIFY = "com.sgu.findyourfriend.MESSAGE_NOTIFY";
	static final String FRIEND_REQUEST_NOTIFY = "com.sgu.findyourfriend.FREQUEST_NOTIFY";

	static final int SHOW = 0;
	static final int HIDE = 1;

	static final String EXTRA_MESSAGE = "message";

	static final String GCM_ID = "APA91bEDWaCSVnmptfykW5bhPpjCzWGxDHP6nr4dHGNHQ2HYzZfhJQ6RC-EuL22Pffjf2kF9isJyS2M5j6thqh60EALon5oYYbQzvi48FaQo5bkjE0lgtFuGT_LNXHeCur4TRgMZn88D2EOrixs1eYiVvU55iBxQ_w";

	static final String PREFIX = "REQUEST_1025";

	static final String REQUEST_SHARE_MESSAGE = PREFIX + "_SHARE";

	static final String REQUEST_FRIEND_MESSAGE = PREFIX + "_FRIEND";

	static final String PREFIX_FRIEND_REQUEST = "FRIEND_REQUEST";
	static final String PREFIX_SHARE_REQUEST = "SHARE_REQUEST";
	static final String PREFIX_FRIEND_RESPONSE = "FRIEND_RESPONSE";
	static final String PREFIX_SHARE_RESPONSE = "SHARE_RESPONSE";

	static final String PARTERN_GET_MESSAGE = "@GETMESSAGEFIX@";

	static final String ADMIN_NAME = "FYF app @";

	static final int MESSAGE_SERVICE = 1000;

}
