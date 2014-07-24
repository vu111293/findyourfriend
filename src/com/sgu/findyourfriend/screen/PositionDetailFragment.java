package com.sgu.findyourfriend.screen;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devsmart.android.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Friend;
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

	// include layout for horizontal friend list
	private View inc;

	// control button
	private Button btnMessage;
	private Button btnCall;
	private Button btnHistory;
	private Button btnUpdate;
	private Button btnRoute;
	private Button btnRequest;

	private int friendId;

	private GpsDirection gpsDirection;

	private Context context;

	private MapFragment parentFragment;

	public PositionDetailFragment() {
		// TODO Auto-generated constructor stub
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
		txtUpdateTime
				.setText(Utility.convertMicTimeToString(System
						.currentTimeMillis()
						- friend.getUserInfo().getLastestlogin().getNanos())
						+ " trước");
		txtAccuracy.setText(friend.getAccurency() + " km");

		txtPhoneNumber.setText(friend.getNumberLogin().get(0));

		// add here
		// txtUpdateTime.setText();

		// get information to transport and accuracy at here

		setupControlView(rootView);

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
				if (friendId != 0) {
					parentFragment.tickVibrator();
					SendMessageFragment fragment = new SendMessageFragment();

					Bundle bundle = new Bundle();
					bundle.putInt("friendId", friendId);
					fragment.setArguments(bundle);

					((BaseContainerFragment) getParentFragment())
							.replaceFragment(fragment, true);
				}
			}
		});

		btnCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (friendId != MyProfileManager.getInstance().mine.getId()) {
					ArrayList<String> phs = FriendManager.getInstance().hmMemberFriends
							.get(friendId).getNumberLogin();

					if (phs.size() == 0) {
						Toast.makeText(context, "Not phone number",
								Toast.LENGTH_LONG).show();
						return;
					}

					final String[] phonenumbers = new String[phs.size()];

					for (int i = 0; i < phs.size(); ++i) {
						phonenumbers[i] = phs.get(i);
					}

					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Chọn số cần gọi:");
					builder.setItems(phonenumbers,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent callIntent = new Intent(
											Intent.ACTION_CALL);
									callIntent
											.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									callIntent.setData(Uri.parse("tel:"
											+ phonenumbers[which]));
									context.startActivity(callIntent);
								}
							}).setNegativeButton("Quay lại",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});

					builder.show();
				}
			}
		});

		btnHistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utility.showMessage(getActivity(), "xin đợi...");
				parentFragment.tickVibrator();
				MapFragment fragment = new MapFragment();

				Bundle bundle = new Bundle();
				bundle.putInt("friendId", friendId);
				bundle.putString("task", "history");

				fragment.setArguments(bundle);
				((BaseContainerFragment) getParentFragment()).replaceFragment(
						fragment, true);
			}
		});

		btnUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utility.showMessage(getActivity(), "xin đợi...");
				parentFragment.tickVibrator();
			}
		});

		btnRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (friendId != MyProfileManager.getInstance().mine.getId()) {

					Utility.showMessage(getActivity(), "xin đợi...");
					parentFragment.tickVibrator();
					MapFragment fragment = new MapFragment();

					Bundle bundle = new Bundle();
					bundle.putInt("friendId", friendId);
					bundle.putString("task", "route");

					fragment.setArguments(bundle);
					((BaseContainerFragment) getParentFragment())
							.replaceFragment(fragment, true);
				}

			}
		});

		btnRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (friendId != MyProfileManager.getInstance().mine.getId()
						&& friend.isShare()) {
					Utility.showMessage(getActivity(), "Yêu cầu đã được gửi.");
					parentFragment.tickVibrator();

				}
			}
		});

		// check view visiable
		if (friendId == MyProfileManager.getInstance().mine.getId()) {
			parentFragment.disableView(view, R.id.btnMessage);
			parentFragment.disableView(view, R.id.btnCall);
			parentFragment.disableView(view, R.id.btnRoute);
		} else {
			parentFragment.enableView(view, R.id.btnMessage);
			parentFragment.enableView(view, R.id.btnCall);
			parentFragment.enableView(view, R.id.btnRoute);
			if (friend.isShare()) {
				parentFragment.disableView(view, R.id.btnRequest);
			} else {
				parentFragment.enableView(view, R.id.btnRequest);
			}
		}

	}
}
