package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.History;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.GpsDirection;
import com.sgu.findyourfriend.utils.Utility;

public class MapController {

	private MapFragment mapFragment;
	private Context context;
	private GoogleMap mMap;
	private GpsDirection gpsDirection;
	private AsyncTask<Void, Void, Void> mLoadLocationTask;
	private List<Marker> hisMarkerList;
	private ProgressDialogCustom progress;

	public MapController(MapFragment parentFragment) {
		this.mapFragment = parentFragment;
		this.context = parentFragment.getParentFragment().getActivity();
		this.mMap = parentFragment.getmMap();

		progress = new ProgressDialogCustom(context);

		// get Gps direction after map is setup
		gpsDirection = new GpsDirection(context, mMap);

		// init variable
		hisMarkerList = new ArrayList<Marker>();
	}

	public void sendMessageTask(int friendId) {
		MessageSendFragment fragment = new MessageSendFragment();

		Bundle bundle = new Bundle();
		bundle.putInt("friendId", friendId);
		fragment.setArguments(bundle);

		((BaseContainerFragment) mapFragment.getParentFragment())
				.replaceFragment(fragment, true);
	}

	public void callTask(int friendId) {

		ArrayList<String> phs = FriendManager.getInstance().hmMemberFriends
				.get(friendId).getNumberLogin();

		if (phs.size() == 0) {
			Toast.makeText(context, "Không có số điện thoại", Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (phs.size() == 1) {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			callIntent.setData(Uri.parse("tel:" + phs.get(0)));
			context.startActivity(callIntent);
			return;
		}
		
		final String[] phonenumbers = new String[phs.size()];

		for (int i = 0; i < phs.size(); ++i) {
			phonenumbers[i] = phs.get(i);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Chọn số cần gọi");
		builder.setItems(phonenumbers, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				callIntent.setData(Uri.parse("tel:" + phonenumbers[which]));
				context.startActivity(callIntent);
			}
		}).setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		builder.show();
	}

	public void drawHistoryTask(final int friendId) {
		// clear route
		gpsDirection.clearRoute();

		// clear marker before add new
		clearnMarkerHistory();

		mLoadLocationTask = new AsyncTask<Void, Void, Void>() {
			Friend f;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progress.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				f = FriendManager.getInstance().hmMemberFriends.get(friendId);
				
				Log.i("TAG debug", f + "");
				
				if (null == f.getSteps())
					f.setSteps(PostData.historyGetUserHistory(context, f
							.getUserInfo().getId()));

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mLoadLocationTask = null;

				if (null == f.getSteps()) {
					
					Utility.showMessage(context, "Không tìm thấy lịch sử");
					
					// hide progress bar
					progress.dismiss();
					return;
				}
				
				
				List<LatLng> latlngs = new ArrayList<LatLng>();

				// draw on map
				int sn = f.getSteps().size();
				while (sn > 0) {
					History hp = f.getSteps().get(sn - 1);
					Log.i("LOC",
							hp.getLocation().latitude + " # "
									+ hp.getLocation().longitude);

					createHistoryPoint(hp, sn);
					latlngs.add(hp.getLocation());
					sn--;
				}

				// hide progress bar
				progress.dismiss();

				// set bound zoom
				zoomBoundPosition(latlngs);
			}
		};

		// execute AsyncTask
		mLoadLocationTask.execute(null, null, null);
	}

	public void updatePositionTask(final int friendId) {
		clearnMarkerHistory();
		gpsDirection.clearRoute();

		(new AsyncTask<Void, Void, Void>() {

			Friend friend;

			@Override
			protected void onPreExecute() {
				friend = FriendManager.getInstance().hmMemberFriends
						.get(friendId);
			}

			@Override
			protected Void doInBackground(Void... params) {

				LatLng lastLocation = PostData.historyGetLastUserLocation(
						context, friendId);
				friend.setLastLocation(lastLocation);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				FriendManager.getInstance().updateChangeMemberFriend(friend);

				// update adapter
				// mapFragment.updateAdapter();

				Intent intentUpdate = new Intent(Config.UPDATE_UI);
				intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.FRIEND);
				intentUpdate.putExtra(Config.UPDATE_ACTION,
						Utility.RESPONSE_YES);
				context.sendBroadcast(intentUpdate);

				if (null != friend.getLastLocation()) {
					zoomToPosition(friend.getLastLocation());
					Log.i("NewPosition", friend.getLastLocation().latitude
							+ " # " + friend.getLastLocation().longitude);
				} else {
					Utility.showMessage(context, "Vị trí hiện tại không có sẵn");
				}

				Toast.makeText(context, "updated!", Toast.LENGTH_SHORT).show();
			}

		}).execute();

	}

	public void routeTask(int friendId) {
		// clear marker history before
		clearnMarkerHistory();

		LatLng dest = FriendManager.getInstance().hmMemberFriends.get(friendId)
				.getLastLocation();
		LatLng myLatLng = MyProfileManager.getInstance().getMyPosition();

		gpsDirection.excuteDirection(myLatLng, dest, true);

		List<LatLng> latlngs = new ArrayList<LatLng>();
		latlngs.add(myLatLng);
		latlngs.add(dest);
		zoomBoundPosition(latlngs);
	}

	public void requestTask(final int friendId) {

		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				PostData.sendShareRequest(context,
						MyProfileManager.getInstance().getMyID(), friendId);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Toast.makeText(context, "sent!", Toast.LENGTH_SHORT).show();
				Friend friend = FriendManager.getInstance().hmMemberFriends
						.get(friendId);
				friend.setAcceptState(Friend.REQUEST_SHARE);
				FriendManager.getInstance().updateChangeMemberFriend(friend);
				// mapFragment.updateAfterAcceptRequest(friendId);

				Intent intentUpdate = new Intent(Config.UPDATE_UI);
				intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.SHARE);
				intentUpdate.putExtra(Config.UPDATE_ACTION, Utility.REQUEST);
				context.sendBroadcast(intentUpdate);

			}

		}).execute();

	}

	public void acceptTask(final int fIDCurrent) {
		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {

			}

			@Override
			protected Void doInBackground(Void... params) {
				mapFragment.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(mapFragment.getActivity(), "Hello",
								Toast.LENGTH_SHORT).show();
					}
				});

				// Toast.makeText(context, "sending accept",
				// Toast.LENGTH_SHORT).show();
				PostData.sendShareAccept(context,
						MyProfileManager.getInstance().getMyID(), fIDCurrent);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				Friend friend = FriendManager.getInstance().hmMemberFriends
						.get(fIDCurrent);
				friend.setAcceptState(Friend.SHARE_RELATIONSHIP);
				FriendManager.getInstance().updateChangeMemberFriend(friend);
				// mapFragment.updateAfterAcceptRequest(fIDCurrent);

				Intent intentUpdate = new Intent(Config.UPDATE_UI);
				intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.SHARE);
				intentUpdate.putExtra(Config.UPDATE_ACTION,
						Utility.RESPONSE_YES);
				context.sendBroadcast(intentUpdate);

				// sound
			}

		}).execute();
	}

	// --------------- utilities methods -------------------- //
	private void createHistoryPoint(History hisP, int prio) {
		MarkerOptions opt = new MarkerOptions();
		opt.position(hisP.getLocation());
		opt.title(getAddress(hisP.getLocation()));
		opt.snippet(null == hisP.getTimest() ? "không xác định thởi gian" :  hisP.getTimest().toGMTString());
		opt.icon(BitmapDescriptorFactory.fromBitmap(Utility.writeTextOnDrawable(
				context, R.drawable.ic_position_history, prio + "")));
		Marker m = mMap.addMarker(opt);
//		opt.title(hisP.getTimest().toGMTString());
		hisMarkerList.add(m);
	}

	public void clearnMarkerHistory() {
		for (Marker m : hisMarkerList) {
			m.remove();
		}
		hisMarkerList.clear();
	}

	// ------------------- zoom control ----------------------- //

	private void zoomBoundPosition(List<LatLng> latlngs) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		for (LatLng latlng : latlngs) {
			builder.include(latlng);
		}

		LatLngBounds bounds = builder.build();

		int padding = 100; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

		mMap.animateCamera(cu);
	}

	public void zoomToPosition(LatLng latLng) {
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 8);
		mMap.animateCamera(cu);
	}

	public void zoomToPosition(Location location) {
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(
				new LatLng(location.getLatitude(), location.getLongitude()), 8);
		mMap.animateCamera(cu);
	}

	// --------------- get address utilities ---------------------- //
	public String getAddress(LatLng point) {
		try {
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(context);
			if (point.latitude != 0 || point.longitude != 0) {
				addresses = geocoder.getFromLocation(point.latitude,
						point.longitude, 1);

				Address address = addresses.get(0);

				String addressText = String.format(
						"%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address
								.getCountryName());

				return addressText;
			} else {
				Toast.makeText(context, "latitude and longitude are null",
						Toast.LENGTH_LONG).show();
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
