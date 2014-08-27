package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;
import java.util.List;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapterFriendSearch extends ArrayAdapter<User> {

	private Context context;
	private ArrayList<User> data = new ArrayList<User>();
	private int layoutResID;

	public CustomAdapterFriendSearch(Context context, int resource,
			ArrayList<User> data) {
		super(context, resource, data);
		this.context = context;
		this.layoutResID = resource;
		this.data = data;
	}

	public void setData(ArrayList<User> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		final View convertView = inflater.inflate(layoutResID, null);
		final User ur = data.get(position);

		if (!ur.getAvatar().equals(""))
			((ImageView) convertView.findViewById(R.id.imgProfile))
					.setImageDrawable(Drawable.createFromPath(ur.getAvatar()));

		((TextView) convertView.findViewById(R.id.txtName)).setText(ur
				.getName());

		if (ur.getAddress().lastIndexOf(",") > -1)
			((TextView) convertView.findViewById(R.id.txtProvince)).setText(ur.getAddress().substring(
					ur.getAddress().lastIndexOf(",") + 1).trim());
		else
			((TextView) convertView.findViewById(R.id.txtProvince)).setText(ur
					.getAddress());

		((Button) convertView.findViewById(R.id.btnInvite))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// request to server

						(new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... params) {
								PostData.sendFriendRequest(context,
										MyProfileManager.getInstance().getMyID(), ur.getId());

								// ************ here add now
								
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								data.remove(ur);
								notifyDataSetChanged();
								Toast.makeText(context, "đã gửi yêu cầu",
										Toast.LENGTH_SHORT).show();
							}

						}).execute();

					}
				});

		return convertView;

	}

}
