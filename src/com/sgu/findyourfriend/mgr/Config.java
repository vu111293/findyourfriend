package com.sgu.findyourfriend.mgr;

public interface Config {
	
	// CONSTANTS
	static final String YOUR_SERVER_URL =  "http://sgudev2014.comuv.com/cfindyourfriend/register.php";

	static final String SEND_MESSAGE_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/send_message.php";
	
	static final String SEND_LOCATION_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/send_location.php";
	
	static final String GET_FRIENDS_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/get_friend_list.php";		
	
	static final String GET_LOCATION_HISTORY_SERVER_URL = "http://sgudev2014.comuv.com/cfindyourfriend/get_locations_history.php";
	
	// Google project id
    static final String GOOGLE_SENDER_ID = "100128594910";  // Place here your Google project id

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Android Example";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.androidexample.gcm.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

	

	
}
