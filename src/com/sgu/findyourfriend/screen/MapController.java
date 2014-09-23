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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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

		final ArrayList<String> phs = FriendManager.getInstance().hmMemberFriends
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

		// more one phone number
		final Dialog dialog = new Dialog(context);
		Utility.showListDialog(Utility.CONFIRM, dialog, "Chọn số cần gọi", phs,
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						callIntent.setData(Uri.parse("tel:" + phs.get(position)));
						context.startActivity(callIntent);

						dialog.dismiss();
					}
				});
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

				// if (null == f.getSteps())
				f.setSteps(PostData.historyGetUserHistory(context, f
						.getUserInfo().getId()));

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mLoadLocationTask = null;

				if (null == f.getSteps() || f.getSteps().size() == 0) {

					Utility.showMessage(context, "Không tìm thấy lịch sử");

					// hide progress bar
					progress.dismiss();
					mapFragment.showMarker();
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

				if (latlngs.size() > 1)
					// set bound zoom
					Utility.zoomBoundPosition(latlngs, mMap);
				else
					Utility.zoomToPosition(latlngs.get(0), mMap);
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

				progress.show();
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
					Utility.zoomToPosition(friend.getLastLocation(), mMap);
					Log.i("NewPosition", friend.getLastLocation().latitude
							+ " # " + friend.getLastLocation().longitude);
				} else {
					Utility.showMessage(context, "Vị trí hiện tại không có sẵn");
				}

				Toast.makeText(context, "đã cập nhật", Toast.LENGTH_SHORT)
						.show();

				progress.dismiss();
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
		Utility.zoomBoundPosition(latlngs, mMap);
	}

	public void requestTask(final int friendId) {

		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				progress.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				PostData.sendShareRequest(context, MyProfileManager
						.getInstance().getMyID(), friendId);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				Friend friend = FriendManager.getInstance().hmMemberFriends
						.get(friendId);
				friend.setAcceptState(Friend.REQUEST_SHARE);
				FriendManager.getInstance().updateChangeMemberFriend(friend);
				// mapFragment.updateAfterAcceptRequest(friendId);

				Intent intentUpdate = new Intent(Config.UPDATE_UI);
				intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.SHARE);
				intentUpdate.putExtra(Config.UPDATE_ACTION, Utility.REQUEST);
				context.sendBroadcast(intentUpdate);
				Toast.makeText(context, "đã gửi yêu cầu chia sẻ",
						Toast.LENGTH_SHORT).show();
				progress.dismiss();
			}

		}).execute();

	}

	public void acceptTask(final int fIDCurrent) {
		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				progress.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				// mapFragment.getActivity().runOnUiThread(new Runnable() {
				// public void run() {
				// Toast.makeText(mapFragment.getActivity(), "Hello",
				// Toast.LENGTH_SHORT).show();
				// }
				// });

				// Toast.makeText(context, "sending accept",
				// Toast.LENGTH_SHORT).show();
				PostData.sendShareAccept(context, MyProfileManager
						.getInstance().getMyID(), fIDCurrent);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				Friend friend = FriendManager.getInstance().hmMemberFriends
						.get(fIDCurrent);
				friend.setAcceptState(Friend.SHARE_RELATIONSHIP);
				FriendManager.getInstance().updateChangeMemberFriend(friend);

				Intent intentUpdate = new Intent(Config.UPDATE_UI);
				intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.SHARE);
				intentUpdate.putExtra(Config.UPDATE_ACTION,
						Utility.RESPONSE_YES);
				context.sendBroadcast(intentUpdate);

				// msg
				Toast.makeText(mapFragment.getActivity(),
						"đã chấp nhận chia sẻ", Toast.LENGTH_SHORT).show();
				progress.dismiss();
			}

		}).execute();
	}

	private static SimpleDateFormat format = new SimpleDateFormat(
			"HH:mm:ss dd/MM/yyyy");

	// --------------- utilities methods -------------------- //
	private void createHistoryPoint(History hisP, int prio) {
		MarkerOptions opt = new MarkerOptions();
		opt.position(hisP.getLocation());
		opt.title(Utility.getAddress(context, hisP.getLocation()));
		opt.snippet(null == hisP.getTimest() ? "thời gian không xác định"
				: format.format(new Date(hisP.getTimest().getTime())));
		opt.icon(BitmapDescriptorFactory.fromBitmap(Utility
				.writeTextOnDrawable(context, R.drawable.ic_position_history,
						prio + "")));
		Marker m = mMap.addMarker(opt);
		// opt.title(hisP.getTimest().toGMTString());
		hisMarkerList.add(m);

		Log.i("Time", hisP.getTimest().toLocaleString());
		// Log.i("Time", "format: " + format.format(new
		// Date(hisP.getTimest().getTime())));
	}

	public void clearnMarkerHistory() {
		for (Marker m : hisMarkerList) {
			m.remove();
		}
		hisMarkerList.clear();
	}

	public void unFriendTask(final int fIDCurrent) {
		(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				progress.show();
			}

			@Override
			protected Boolean doInBackground(Void... params) {

				return PostData.friendRemove(context, MyProfileManager
						.getInstance().getMyID(), fIDCurrent);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					FriendManager.getInstance().removeFriend(fIDCurrent);

					Intent intentUpdate = new Intent(Config.UPDATE_UI);
					intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.FRIEND);
					intentUpdate.putExtra(Config.UPDATE_ACTION, Utility.REMOVE);
					intentUpdate.putExtra(Utility.FRIEND_ID, fIDCurrent);

					context.sendBroadcast(intentUpdate);

					Toast.makeText(context, "xóa bạn thành công",
							Toast.LENGTH_SHORT).show();
				} else {
					Utility.showDialog(Utility.ERROR, new Dialog(context),
							"Lỗi", "xóa thất bại. Xin thử lại sau");
				}

				progress.dismiss();
			}

		}).execute();

	}

	public void stopShareTask(final int fIDCurrent) {
		(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				progress.show();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				return PostData.friendStopShare(context, MyProfileManager
						.getInstance().getMyID(), fIDCurrent);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					// update ui
					FriendManager.getInstance().stopShare(fIDCurrent);

					Intent intentUpdate = new Intent(Config.UPDATE_UI);
					intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.FRIEND);
					intentUpdate.putExtra(Config.UPDATE_ACTION, Utility.REMOVE);
					intentUpdate.putExtra(Utility.FRIEND_ID, fIDCurrent);

					context.sendBroadcast(intentUpdate);

					Toast.makeText(context, "đã dừng chia sẻ",
							Toast.LENGTH_SHORT).show();
				} else {
					Utility.showDialog(Utility.ERROR, new Dialog(context),
							"Lỗi",
							"Không thể dừng chia sẻ lúc này. Xin thử lại sau");
				}

				progress.dismiss();

			}

		}).execute();

	}

	public void stopShareAllTask(final int fIDCurrent) {
		(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				progress.show();
			}

			@Override
			protected Boolean doInBackground(Void... params) {

				boolean success = true;
				for (Friend f : FriendManager.getInstance().pureFriends) {
					success &= PostData.friendStopShare(context,
							MyProfileManager.getInstance().getMyID(), f
									.getUserInfo().getId());

				}

				return success;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					// update ui
					for (Friend f : FriendManager.getInstance().pureFriends) {
						FriendManager.getInstance().stopShare(
								f.getUserInfo().getId());
					}

					// FriendManager.getInstance().stopShare(fIDCurrent);

					Intent intentUpdate = new Intent(Config.UPDATE_UI);
					intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.FRIEND);
					intentUpdate.putExtra(Config.UPDATE_ACTION, Utility.REMOVE);
					intentUpdate.putExtra(Utility.RESPONSE_NO, fIDCurrent);

					context.sendBroadcast(intentUpdate);

					Toast.makeText(context,
							"đã dừng chia sẻ với tất cả bạn bè",
							Toast.LENGTH_SHORT).show();
				} else {
					Utility.showDialog(Utility.ERROR, new Dialog(context),
							"Lỗi",
							"Không thể dừng chia sẻ lúc này. Xin thử lại sau");
				}
				progress.dismiss();
			}

		}).execute();

	}

	public void removeHistoriesTask(final int fIDCurrent) {
		(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				progress.show();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				return PostData.historyRemove(context, MyProfileManager
						.getInstance().getMyID());
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					Toast.makeText(context, "lịch sử đi chuyển đã được xóa",
							Toast.LENGTH_SHORT).show();
				} else {
					Utility.showDialog(Utility.ERROR, new Dialog(context),
							"Lỗi",
							"Không thể xóa lịch sử di chuyển lúc này. Xin thử lại sau");
				}

				progress.dismiss();
			}

		}).execute();
	}

	public void inviteTask(final int id) {
		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				progress.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				PostData.sendFriendRequest(context, MyProfileManager
						.getInstance().getMyID(), id);

				// ************ here add now
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Toast.makeText(context, "đã gửi yêu cầu", Toast.LENGTH_SHORT)
						.show();

				progress.dismiss();
			}

		}).execute();

	}

}
