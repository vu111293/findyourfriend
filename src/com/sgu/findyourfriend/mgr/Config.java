/*
 * Copyright (C) 2014 Tubor Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sgu.findyourfriend.mgr;

public class Config {

	public enum AppState {
		OFFLINE, ONLINE
	}

	public static AppState currentState = AppState.OFFLINE;

	public static final boolean MODE_OFFLINE = false;

	// Google project id
	public static final String GOOGLE_SENDER_ID = "googlecode";

	// config
	public static final int MIN_PASSWORD_LENGHT = 6;
	public static final int OLD_LIMIT = 10;
	public static final long MINS_OF_YEAR = 315360000000l;
	public static final String DEFAULT_PASSWORD = "123456";

	// post state
	public static int PHONE_REGISTED = 0;
	public static int ERROR_REGIST = 1; // not yet registed
	public static int SUCCESS = 2;
	public static int ERROR_NOT_FOUND = 3;
	public static int ERROR = 4;

	public static final String TAG = "GCM Android Example";

	public static final String DISPLAY_MESSAGE_ACTION = "com.androidexample.gcm.DISPLAY_MESSAGE";
	public static final String UPDATE_MESSAGE_WIDGET_ACTION = "com.androidexample.gcm.UPDATE_MESSAGE_WIDGET";
	public static final String LOCAL_MESSAGE_ACTION = "com.sgu.findyourfriend.LOCAL_MESSAGE";

	// call action intent
	public static final String UPDATE_UI = "com.sgu.findyourfriend.UPDATE_UI";
	public static final String UPDATE_TYPE = "com.sgu.findyourfriend.UPDATE_TYPE";
	public static final String UPDATE_ACTION = "com.sgu.findyourfriend.UPDATE_ACTION";

	public static final String MAIN_ACTION = "com.sgu.findyourfriend.MAIN_ACTION";
	public static final String EDIT_MESSAGE_ACTION = "com.sgu.findyourfriend.EDIT_MEESAGE";
	public static final String ZOOM_POSITION_ACTION = "com.sgu.findyourfriend.ZOOM_POSITION";
	public static final String ROUTE_ACTION = "com.sgu.findyourfriend.ROUTE";
	public static final String HISTORY_ACTION = "com.sgu.findyourfriend.HISTORY";

	public static final String NOTIFY_UI = "com.sgu.findyourfriend.NOTIFY_UI";
	public static final String MESSAGE_NOTIFY = "com.sgu.findyourfriend.MESSAGE_NOTIFY";
	public static final String FRIEND_REQUEST_NOTIFY = "com.sgu.findyourfriend.FREQUEST_NOTIFY";
	public static final String FRIEND_ID = "com.sgu.findyourfriend.FRIEND_ID";
	
	
	public static final int SHOW = 0;
	public static final int HIDE = 1;

	public static final String EXTRA_MESSAGE = "message";

	public static final String GCM_ID = "APA91bEDWaCSVnmptfykW5bhPpjCzWGxDHP6nr4dHGNHQ2HYzZfhJQ6RC-EuL22Pffjf2kF9isJyS2M5j6thqh60EALon5oYYbQzvi48FaQo5bkjE0lgtFuGT_LNXHeCur4TRgMZn88D2EOrixs1eYiVvU55iBxQ_w";

	public static final String PREFIX = "REQUEST_1025";

	public static final String REQUEST_SHARE_MESSAGE = PREFIX + "_SHARE";

	public static final String REQUEST_FRIEND_MESSAGE = PREFIX + "_FRIEND";

	public static final String PREFIX_FRIEND_REQUEST = "FRIEND_REQUEST";
	public static final String PREFIX_SHARE_REQUEST = "SHARE_REQUEST";
	public static final String PREFIX_FRIEND_RESPONSE = "FRIEND_RESPONSE";
	public static final String PREFIX_SHARE_RESPONSE = "SHARE_RESPONSE";

	public static final String PARTERN_GET_MESSAGE = "@GETMESSAGEFIX@";

	public static final String ADMIN_NAME = "Quản trị viên";

	public static final int MESSAGE_SERVICE = 1000;

	public static final String PREFIX_LOCATION_IN_MESSAGE = "location_message_000 ";

	

}
