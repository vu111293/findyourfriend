package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class CustomAdapterFriendRequests extends ArrayAdapter<Friend> {

	private Context context;
	private List<Friend> Data = new ArrayList<Friend>();
	private int LayoutResID;

	public CustomAdapterFriendRequests(Context context, int resource,
			List<Friend> objects) {
		super(context, resource, objects);
		this.context = context;
		this.LayoutResID = resource;
		this.Data = objects;
	}

	public int getItemCount() {
		return Data.size();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		final View convertView = inflater.inflate(LayoutResID, null);
		final Friend fr = Data.get(position);

		((ImageView) convertView.findViewById(R.id.imgProfile))
				.setImageDrawable(FriendManager.getInstance().hmImageP.get(fr
						.getUserInfo().getId()));

		((TextView) convertView.findViewById(R.id.txtName)).setText(fr
				.getUserInfo().getName());

		((ImageView) convertView.findViewById(R.id.imgNotNow))
				.setVisibility(View.GONE);
		((Button) convertView.findViewById(R.id.btnNotNow))
				.setVisibility(View.VISIBLE);

		((Button) convertView.findViewById(R.id.btnConfirm))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// request to server

						(new AsyncTask<Void, Void, Boolean>() {

							@Override
							protected Boolean doInBackground(Void... params) {
								return PostData.sendFriendAccept(context,
										MyProfileManager.getInstance().getMyID(), fr.getUserInfo()
												.getId());
							}

							@Override
							protected void onPostExecute(Boolean isOk) {
								if (isOk) {
									Data.remove(fr);
									notifyDataSetChanged();
									
									// warning
									fr.setAcceptState(Friend.FRIEND_RELATIONSHIP);
									FriendManager.getInstance().updateChangeRequestToMember(fr);
									
									// update with broadcast intent
									Intent intentUpdate = new Intent(Config.UPDATE_UI);
									intentUpdate.putExtra(Config.UPDATE_TYPE,
											Utility.FRIEND);
									intentUpdate.putExtra(Config.UPDATE_ACTION,
											Utility.RESPONSE_YES);
									context.sendBroadcast(intentUpdate);
									
									Toast.makeText(context, "đã chấp nhận",
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(context, "thử lại sau",
											Toast.LENGTH_SHORT).show();
								}
							}

						}).execute();

					}
				});

		((Button) convertView.findViewById(R.id.btnNotNow))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// request to server
						(new AsyncTask<Void, Void, Boolean>() {

							@Override
							protected Boolean doInBackground(Void... params) {
								return PostData.sendFriendNotAccept(context,
										MyProfileManager.getInstance().getMyID(), fr.getUserInfo()
												.getId());
							}

							@Override
							protected void onPostExecute(Boolean isOk) {
								if (isOk) {
									Data.remove(fr);
									notifyDataSetChanged();
									
									FriendManager.getInstance().removeFriendRequest(fr);
								} else {
									Toast.makeText(context, "thử lại sau",
											Toast.LENGTH_SHORT).show();
								}
							}

						}).execute();

					}
				});

		return convertView;

	}

}
