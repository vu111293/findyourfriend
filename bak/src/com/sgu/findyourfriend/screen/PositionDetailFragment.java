package com.sgu.findyourfriend.screen;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sgu.findyourfriend.FriendManager;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.model.Friend;

public class PositionDetailFragment extends Fragment {

	private Friend friend;
	private TextView txtName;
	private TextView txtAddress;
	private TextView txtEmail;
	private TextView txtPhoneNumber;
	private TextView txtUpdateTime;
	private TextView txtAccuracy;
	private TextView txtWalkTime;
	private TextView txtCarTime;

	// include layout for horizontal friend list
	private View inc;
	
	private View rootView;

	// control button
	private Button btnMessage;
	private Button btnCall;
	private Button btnHistory;
	private Button btnUpdate;
	private Button btnRoute;
	private Button btnRequest;

	private int friendId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_position_info,
				container, false);

		Bundle bundle = this.getArguments();
		friendId = bundle.getInt("friendId");
		friend = FriendManager.instance.friends.get(friendId);

		txtName = (TextView) rootView.findViewById(R.id.txtUserName);
		txtAddress = (TextView) rootView.findViewById(R.id.txtAddress);
		txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
		txtPhoneNumber = (TextView) rootView.findViewById(R.id.txtPhone);
		txtUpdateTime = (TextView) rootView.findViewById(R.id.txtUpdateTime);
		txtAccuracy = (TextView) rootView.findViewById(R.id.txtAccuracy);
		txtWalkTime = (TextView) rootView.findViewById(R.id.txtWalkTime);
		txtCarTime = (TextView) rootView.findViewById(R.id.txtCarTime);

		txtName.setText(friend.getUserInfo().getName());
		txtAddress.setText(bundle.getString("address"));
		txtEmail.setText(friend.getUserInfo().getEmail());
		txtPhoneNumber.setText(friend.getUserInfo().getPhoneNumber());

		// add here
		// txtUpdateTime.setText();

		// get information to transport and accuracy at here

		this.rootView = rootView;
		setupControlView(rootView);

		// if ()
		// ((LinearLayout)
		// ((ViewGroup)view.findViewById(R.id.btnMessage).getParent())).setBackgroundColor(Color.RED);

		return rootView;
	}

	private void setupControlView(View view) {
		inc = view.findViewById(R.id.horizaltalController);
		btnMessage = (Button) inc.findViewById(R.id.btnMessage);
		btnCall = (Button) inc.findViewById(R.id.btnCall);
		btnHistory = (Button) inc.findViewById(R.id.btnHistory);
		btnUpdate = (Button) inc.findViewById(R.id.btnUpdate);
		btnRoute = (Button) inc.findViewById(R.id.btnRoute);
		btnRequest = (Button) inc.findViewById(R.id.btnRequest);

		btnMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				SendMessageFragment fragment = new SendMessageFragment();

				Bundle bundle = new Bundle();
				bundle.putInt("friendId", friendId);
				fragment.setArguments(bundle);

				((BaseContainerFragment) getParentFragment()).replaceFragment(
						fragment, true);
			}
		});

		btnCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				callIntent.setData(Uri.parse("tel:"
						+ FriendManager.instance.friends.get(friendId)
								.getUserInfo().getPhoneNumber()));
				getActivity().getApplicationContext().startActivity(callIntent);
			}
		});

		btnHistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (maskEn[2] == 1) {
				// hideControl();
				// mapController.drawHistoryTask(idFriendCur);
				// // onHistoryExcute(friendList.get(idFriendCur).getUserInfo()
				// // .getSteps());
				// }
			}
		});

		btnUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (maskEn[3] == 1) {
				// hideControl();
				// mapController.updatePositionTask(idFriendCur);
				// }
			}
		});

		btnRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MapFragment fragment = new MapFragment();

				Bundle bundle = new Bundle();
				bundle.putInt("friendId", friendId);
				bundle.putString("task", "route");

				fragment.setArguments(bundle);
				((BaseContainerFragment) getParentFragment()).replaceFragment(
						fragment, true);
			}
		});

		btnRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (maskEn[5] == 1) {
				// hideControl();
				// mapController.requestTask(idFriendCur);
				// }
			}
		});

			// check view visiable
			if (friendId == 0) {
				disableView(R.id.btnMessage);
				disableView(R.id.btnCall);
				disableView(R.id.btnRoute);
			} else {
				enableView(R.id.btnMessage);
				enableView(R.id.btnCall);
				enableView(R.id.btnRoute);
				if (friend.getShare() == 1) {
					disableView(R.id.btnRequest);
				} else {
					enableView(R.id.btnRequest);
				}
			}

	}

	private void disableView(int idControlView) {
		((LinearLayout) ((ViewGroup) rootView.findViewById(idControlView)
				.getParent())).setBackgroundColor(Color.GRAY);

	}

	private void enableView(int idControlView) {
		((LinearLayout) ((ViewGroup) rootView.findViewById(idControlView)
				.getParent())).setBackgroundColor(Color.TRANSPARENT);

	}

}
