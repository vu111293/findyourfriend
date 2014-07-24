package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.model.Friend;

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

		// if (fr.isNotNow()) {
		// ((ImageView) convertView.findViewById(R.id.imgNotNow))
		// .setVisibility(View.VISIBLE);
		// ((Button) convertView.findViewById(R.id.btnNotNow))
		// .setVisibility(View.GONE);
		// } else {
		((ImageView) convertView.findViewById(R.id.imgNotNow))
				.setVisibility(View.GONE);
		((Button) convertView.findViewById(R.id.btnNotNow))
				.setVisibility(View.VISIBLE);
		// }

		((Button) convertView.findViewById(R.id.btnNotNow))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// request to server

						Data.remove(fr);
						notifyDataSetChanged();

						Toast.makeText(context, "remove item ",
								Toast.LENGTH_SHORT).show();
					}
				});

		((Button) convertView.findViewById(R.id.btnConfirm))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// request to server
						Data.remove(fr);
						notifyDataSetChanged();

						Toast.makeText(context, "remove item ",
								Toast.LENGTH_SHORT).show();
					}
				});

		return convertView;

	}

}
