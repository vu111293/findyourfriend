package com.sgu.findyourfriend.screen;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.CustomAdapterFriendRequests;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.SettingManager;

public class FriendRequestsFragment extends Fragment implements
		OnItemClickListener {
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
		lv.setOnItemClickListener(this);

		// Data.add(new FriendRequest(1, "Nguyễn Chí Hùng", "",false));
		// Data.add(new FriendRequest(2, "Quốc Vũ", "", false));
		// Data.add(new FriendRequest(3, "Trương Toàn", "", false));

		// load friend request list from server
		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub

				// Data.add(new FriendRequest(
				// 12,
				// "Nguyễn Chí Hùng",
				// "http://findicons.com/files/icons/175/halloween_avatar/128/bat.png",
				// false));
				// Data.add(new FriendRequest(
				// 2,
				// "Quốc Vũ",
				// "http://findicons.com/files/icons/175/halloween_avatar/128/freddie.png",
				// false));
				// Data.add(new FriendRequest(
				// 7,
				// "Trương Toàn",
				// "http://findicons.com/files/icons/175/halloween_avatar/128/frankenstein.png",
				// false));

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
	public void onItemClick(AdapterView<?> parent, View view, int pos, long arg3) {

	}

}
