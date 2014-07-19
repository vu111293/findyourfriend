package com.sgu.findyourfriend.screen;

import java.util.ArrayList;

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
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.utils.GpsDirection;
import com.sgu.findyourfriend.utils.Utility;

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

	private View rootView;

	// control button
	private Button btnMessage;
	private Button btnCall;
	private Button btnHistory;
	private Button btnUpdate;
	private Button btnRoute;
	private Button btnRequest;

	private int friendId;

	private GpsDirection gpsDirection;

	// image loader
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_position_info,
				container, false);

		setupImageLoader();
		gpsDirection = new GpsDirection(getActivity());

		Bundle bundle = this.getArguments();
		friendId = bundle.getInt("friendId");
		friend = FriendManager.getInstance().friends.get(friendId);

		imgProfile = (ImageView) rootView.findViewById(R.id.imgAvatar);
		imageLoader.displayImage(friend.getUserInfo().getAvatar(), imgProfile,
				options);

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
				- friend.getUserInfo().getLastestlogin().getNanos()) + " trước");
		txtAccuracy.setText(friend.getAccurency() + " km");

		txtPhoneNumber.setText(friend.getNumberLogin().get(0));

		// add here
		// txtUpdateTime.setText();

		// get information to transport and accuracy at here

		this.rootView = rootView;
		setupControlView(rootView);

		gpsDirection
				.loadViewDirectionInfo(txtDistance, txtWalkTime,
						txtMotobikeTime, new LatLng(bundle.getDouble("myLat"),
								bundle.getDouble("myLng")), friend
								.getLastLocation());

		// if ()
		// ((LinearLayout)
		// ((ViewGroup)view.findViewById(R.id.btnMessage).getParent())).setBackgroundColor(Color.RED);

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
				ArrayList<String> phs = FriendManager.getInstance().friends
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
							public void onClick(DialogInterface dialog, int id) {
							}
						});

				builder.show();
			}
		});

		btnHistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
			if (friend.isShare()) {
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

	@SuppressWarnings("deprecation")
	private void setupImageLoader() {
		// setup image loader
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader = ImageLoader.getInstance();
		// File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.memoryCacheExtraOptions(480, 800)
				// default = device screen dimensions

				.taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
				.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
				.threadPoolSize(3)
				// default
				.threadPriority(Thread.NORM_PRIORITY - 1)
				// default
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				// default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// default
				.memoryCacheSize(2 * 1024 * 1024)
				.imageDownloader(new BaseImageDownloader(context)) // default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.build();
		imageLoader.init(config);
	}

}
