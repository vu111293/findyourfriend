package com.sgu.findyourfriend.screen;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.utils.GpsDirection;
import com.sgu.findyourfriend.utils.Utility;

@SuppressLint("ValidFragment")
public class PositionDetailFragment extends Fragment {

	private Friend friend;
	private ImageView imgProfile;
	private TextView txtName;
	private TextView txtAddress;
	private TextView txtEmail;
	private TextView txtPhoneNumber;
	private TextView txtUpdateTime;
	private TextView txtAccuracy;

	private TextView txtDistance;
	private TextView txtWalkTime;
	private TextView txtMotobikeTime;

	private int friendId;

	private GpsDirection gpsDirection;

	private Context context;

	private MapFragment parentFragment;

	public PositionDetailFragment() {
	}

	public PositionDetailFragment(Fragment parent) {
		this.parentFragment = (MapFragment) parent;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_position_info,
				container, false);

		gpsDirection = new GpsDirection(getActivity());

		Bundle bundle = this.getArguments();
		friendId = bundle.getInt("friendId");
		friend = FriendManager.getInstance().hmMemberFriends.get(friendId);

		imgProfile = (ImageView) rootView.findViewById(R.id.imgAvatar);

		imgProfile.setImageDrawable(FriendManager.getInstance().hmImageP
				.get(friend.getUserInfo().getId()));

		txtName = (TextView) rootView.findViewById(R.id.txtUserName);
		txtAddress = (TextView) rootView.findViewById(R.id.txtAddress);
		txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
		txtPhoneNumber = (TextView) rootView.findViewById(R.id.txtPhone);
		txtUpdateTime = (TextView) rootView.findViewById(R.id.txtUpdateTime);
		txtAccuracy = (TextView) rootView.findViewById(R.id.txtAccuracy);

		txtDistance = (TextView) rootView.findViewById(R.id.txtDistanceInfo);
		txtWalkTime = (TextView) rootView.findViewById(R.id.txtWalkTimeInfo);
		txtMotobikeTime = (TextView) rootView
				.findViewById(R.id.txtMotobikeTimeInfo);

		txtName.setText(friend.getUserInfo().getName());
		txtAddress.setText(bundle.getString("address"));
		txtEmail.setText(friend.getUserInfo().getEmail());
		txtUpdateTime.setText(Utility.convertMicTimeToString(System
				.currentTimeMillis()
				- friend.getUserInfo().getLastLogin().getTime()));

		Log.i("Time", (System.currentTimeMillis() - friend.getUserInfo()
				.getLastLogin().getTime())
				+ " # "
				+ friend.getUserInfo().getLastLogin().toGMTString()
				+ " # " + (new Date().toGMTString()));

		txtAccuracy.setText(friend.getAccurency() + " km");

		txtPhoneNumber.setText(friend.getNumberLogin().get(0));

		txtDistance.setText("...");
		txtWalkTime.setText("...");
		txtMotobikeTime.setText("...");

		if (friend.getUserInfo().isPublic()) {
			User user = friend.getUserInfo();
			((TextView) rootView.findViewById(R.id.txtBirthday)).setText(user
					.getBirthday().getDay()
					+ "/"
					+ user.getBirthday().getMonth()
					+ "/"
					+ user.getBirthday().getYear());
			if (!user.getWorkplace().equals(""))
				((TextView) rootView.findViewById(R.id.txtFactory))
						.setText(user.getWorkplace());
			else
				((TextView) rootView.findViewById(R.id.txtFactory))
						.setText("không có");

			if (!user.getSchool().equals(""))
				((TextView) rootView.findViewById(R.id.txtSchool)).setText(user
						.getSchool());
			else
				((TextView) rootView.findViewById(R.id.txtSchool))
						.setText("không có");

			if (!user.getAddress().equals(""))
				((TextView) rootView.findViewById(R.id.txtAddress2))
						.setText(user.getAddress());
			else
				((TextView) rootView.findViewById(R.id.txtAddress2))
						.setText("không có");

			if (!user.getFblink().equals(""))
				((TextView) rootView.findViewById(R.id.txtFacebook))
						.setText(user.getFblink());
			else
				((TextView) rootView.findViewById(R.id.txtFacebook))
						.setText("không có");
		} else {
			((LinearLayout) rootView.findViewById(R.id.layoutOption))
					.setVisibility(View.GONE);

		}

		// get information to transport and accuracy at here
		gpsDirection
				.loadViewDirectionInfo(txtDistance, txtWalkTime,
						txtMotobikeTime, new LatLng(bundle.getDouble("myLat"),
								bundle.getDouble("myLng")), friend
								.getLastLocation());

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		context = activity.getApplicationContext();
		super.onAttach(activity);

	}

}
