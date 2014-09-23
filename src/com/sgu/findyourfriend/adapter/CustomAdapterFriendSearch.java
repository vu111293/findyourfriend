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
package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.screen.ProgressDialogCustom;

public class CustomAdapterFriendSearch extends ArrayAdapter<User> {

	private Context context;
	private ArrayList<User> data = new ArrayList<User>();
	private int layoutResID;
	private ProgressDialogCustom progress;

	public CustomAdapterFriendSearch(Context context, int resource,
			ArrayList<User> data) {
		super(context, resource, data);
		this.context = context;
		this.layoutResID = resource;
		this.data = data;

		progress = new ProgressDialogCustom(context);
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
			((TextView) convertView.findViewById(R.id.txtProvince)).setText(ur
					.getAddress()
					.substring(ur.getAddress().lastIndexOf(",") + 1).trim());
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
							protected void onPreExecute() {
								progress.show();
							}

							@Override
							protected Void doInBackground(Void... params) {
								PostData.sendFriendRequest(context,
										MyProfileManager.getInstance()
												.getMyID(), ur.getId());

								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								data.remove(ur);
								notifyDataSetChanged();
								Toast.makeText(context, "đã gửi yêu cầu",
										Toast.LENGTH_SHORT).show();

								progress.dismiss();
							}

						}).execute();

					}
				});

		return convertView;

	}

}
