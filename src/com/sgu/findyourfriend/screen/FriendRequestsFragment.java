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
package com.sgu.findyourfriend.screen;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.CustomAdapterFriendRequests;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.utils.Utility;

public class FriendRequestsFragment extends Fragment {

	private static boolean isRegister;
	private CustomAdapterFriendRequests adapter;
	private ListView lv = null;
	private Context ctx;
	private View rootView;
	private Context context;

	public FriendRequestsFragment() {
		isRegister = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// check and notify
		if (SettingManager.getInstance().getNoNewRequest() > 0) {
			SettingManager.getInstance().setNoNewRequest(0);

			// send broadcast intent update widget
			Intent intent = new Intent(Config.UPDATE_MESSAGE_WIDGET_ACTION);
			context.sendBroadcast(intent);

			// intent hide nofify
			// send broadcast hide notify
			Intent intent2 = new Intent(Config.NOTIFY_UI);
			intent2.putExtra(Config.FRIEND_REQUEST_NOTIFY, Config.HIDE);
			context.sendBroadcast(intent2);

		}

		rootView = inflater.inflate(R.layout.fragment_friendrequests,
				container, false);

		ctx = rootView.getContext();

		lv = (ListView) rootView.findViewById(R.id.listView_FriendRequests);
		adapter = new CustomAdapterFriendRequests(ctx,
				R.layout.custom_friendrequests, new ArrayList<Friend>(
						FriendManager.getInstance().hmRequestFriends.values()));
		lv.setAdapter(adapter);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.context = activity;

		if (!isRegister) {
			activity.registerReceiver(mHandleMessageReceiver, new IntentFilter(
					com.sgu.findyourfriend.mgr.Config.UPDATE_UI));
			isRegister = true;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isRegister)
			getActivity().unregisterReceiver(mHandleMessageReceiver);
	}

	// --------------handle message ----------------------------------
	// handle message
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctx, Intent intent) {
			if (intent.getStringExtra(
					(com.sgu.findyourfriend.mgr.Config.UPDATE_TYPE)).equals(
					Utility.FRIEND)) {

				Log.i("friend", "REQUEST update");
				adapter = new CustomAdapterFriendRequests(ctx,
						R.layout.custom_friendrequests, new ArrayList<Friend>(
								FriendManager.getInstance().hmRequestFriends
										.values()));
				lv.setAdapter(adapter);
			}
		}
	};
}
