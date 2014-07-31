package com.sgu.findyourfriend.mgr;

public interface Config {

	// CONSTANTS
	static final String YOUR_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/register.php";

	static final String SEND_MESSAGE_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/send_message.php";

	static final String SEND_LOCATION_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/send_location.php";

	static final String GET_FRIENDS_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/get_friend_list.php";

	static final String GET_LOCATION_HISTORY_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/get_locations_history.php";

	// Google project id
	static final String GOOGLE_SENDER_ID = "100128594910"; // Place here your
															// Google project id

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCM Android Example";

	static final String DISPLAY_MESSAGE_ACTION = "com.androidexample.gcm.DISPLAY_MESSAGE";
	
	static final String UPDATE_MESSAGE_WIDGET_ACTION = "com.androidexample.gcm.UPDATE_MESSAGE_WIDGET";

	static final String EXTRA_MESSAGE = "message";

	static final String GCM_ID = "APA91bEDWaCSVnmptfykW5bhPpjCzWGxDHP6nr4dHGNHQ2HYzZfhJQ6RC-EuL22Pffjf2kF9isJyS2M5j6thqh60EALon5oYYbQzvi48FaQo5bkjE0lgtFuGT_LNXHeCur4TRgMZn88D2EOrixs1eYiVvU55iBxQ_w";

	
	
	static final String PREFIX = "REQUEST_1025";
	
	static final String REQUEST_SHARE_MESSAGE = PREFIX + "_SHARE";
	
	static final String REQUEST_FRIEND_MESSAGE = PREFIX + "_FRIEND";
	
	
	static final String PREFIX_FRIEND_REQUEST = "FRIEND_REQUEST";
	static final String PREFIX_SHARE_REQUEST = "SHARE_REQUEST";
	static final String PREFIX_FRIEND_RESPONSE = "FRIEND_RESPONSE";
	static final String PREFIX_SHARE_RESPONSE = "SHARE_RESPONSE";
	
	
}
