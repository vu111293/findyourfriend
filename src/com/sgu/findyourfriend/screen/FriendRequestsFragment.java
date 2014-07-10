package com.sgu.findyourfriend.screen;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.CustomAdapter_FriendRequests;
import com.sgu.findyourfriend.model.FriendRequest;

public class FriendRequestsFragment extends Fragment {
	ArrayList<FriendRequest> Data = new ArrayList<FriendRequest>();
	CustomAdapter_FriendRequests Adapter = null;
	ListView lv = null;
	Context ctx;
	View rootView;

	public FriendRequestsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.activity_friendrequests,
				container, false);

		// set actionbar
		View bar = getActivity().getActionBar().getCustomView();
		bar.findViewById(R.id.imgFriendList).setVisibility(View.VISIBLE);
		bar.findViewById(R.id.txtSend).setVisibility(View.GONE);

		ctx = rootView.getContext();
		lv = (ListView) rootView.findViewById(R.id.listView_FriendRequests);

		Data.add(new FriendRequest("Nguyễn Chí Hùng", 100, ""));
		Data.add(new FriendRequest("Quốc Vũ", 1, ""));
		Data.add(new FriendRequest("Trương Toàn", 20, ""));
		Adapter = new CustomAdapter_FriendRequests(ctx,
				R.layout.custom_friendrequests, Data);

		lv.setAdapter(Adapter);
		Data.add(new FriendRequest("Nguyễn Chí Hùng", 100, ""));
		Data.add(new FriendRequest("Quốc Vũ", 1, ""));
		Data.add(new FriendRequest("Trương Toàn", 20, ""));
		Adapter.notifyDataSetChanged();
		return rootView;
	}

}
