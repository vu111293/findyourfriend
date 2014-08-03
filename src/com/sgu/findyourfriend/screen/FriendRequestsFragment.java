package com.sgu.findyourfriend.screen;

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
import android.widget.ListView;
import android.widget.ProgressBar;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.CustomAdapterFriendRequests;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.utils.Utility;

public class FriendRequestsFragment extends Fragment {

	private static boolean isRegister = false;
	private CustomAdapterFriendRequests adapter = null;
	private ListView lv = null;
	private Context ctx;
	private View rootView;
	private ProgressBar pbLoader;

	public FriendRequestsFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// check and notify
		if (SettingManager.getInstance().getNoNewRequest() > 0) {
			SettingManager.getInstance().setNoNewRequest(0);
			MessageManager.getInstance().sendUpdateRequestWidget();
		}

		rootView = inflater.inflate(R.layout.fragment_friendrequests,
				container, false);

		// set actionbar
		View bar = getActivity().getActionBar().getCustomView();
		bar.findViewById(R.id.imgFriendList).setVisibility(View.VISIBLE);
		bar.findViewById(R.id.imgSend).setVisibility(View.GONE);

		ctx = rootView.getContext();

		pbLoader = (ProgressBar) rootView.findViewById(R.id.pbLoader);
		pbLoader.setVisibility(View.VISIBLE);

		lv = (ListView) rootView.findViewById(R.id.listView_FriendRequests);
		adapter = new CustomAdapterFriendRequests(ctx,
				R.layout.custom_friendrequests,
				FriendManager.getInstance().requestFriends);
		lv.setAdapter(adapter);

		// load friend request list from server
		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				pbLoader.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
			}

		}).execute();

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

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
				adapter.notifyDataSetChanged();
			}
		}
	};
}
