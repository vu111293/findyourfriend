package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.model.Friend;

public class CustomAdapterFriendStatus extends ArrayAdapter<Friend> {
	private int LayoutResID;
	private Context ctx;
	private List<Friend> DataList = new ArrayList<Friend>();

	public CustomAdapterFriendStatus(Context context, int resource,
			List<Friend> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		ctx = context;
		LayoutResID = resource;
		DataList = objects;
	}

	@Override
	public Friend getItem(int position) {
		return DataList.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
		convertView = inflater.inflate(LayoutResID, null);
		TextView name = (TextView) convertView
				.findViewById(R.id.txtName);
		TextView phoneNumber = (TextView) convertView
				.findViewById(R.id.txtPhoneNumber);
		ImageView imgProfile = (ImageView) convertView
				.findViewById(R.id.imgProfile);
		ImageView imgStatusBar = (ImageView) convertView
				.findViewById(R.id.imgStatusBar);
		TextView txtStatusText = (TextView) convertView.findViewById(R.id.txtStatusText);
		ImageView imgNoneAccept = (ImageView) convertView.findViewById(R.id.imgNoneAccept);
		
		
		Friend fr = DataList.get(position);
		
		
		name.setText(fr.getUserInfo().getName());
		phoneNumber.setText(fr.getNumberLogin().get(0));
		
		imgProfile.setImageDrawable(FriendManager.getInstance().hmImageP
				.get(fr.getUserInfo().getId()));
		
		// set img
		if (fr.getAcceptState() == Friend.ACCEPT_STATE) {
			imgNoneAccept.setVisibility(View.GONE);
			if (fr.isAvailable()) {
				imgStatusBar.setBackgroundColor(ctx.getResources().getColor(R.color.online));
				if (fr.isShare()) {
					txtStatusText.setText("chia sẻ");
				} else {
					txtStatusText.setText("trực tuyến");
				}
			} else {
				imgStatusBar.setBackgroundColor(ctx.getResources().getColor(R.color.offine));
				txtStatusText.setText("đang ẩn");
			}
			
		} else if(fr.getAcceptState() == Friend.WAIT_STATE){
			imgNoneAccept.setVisibility(View.VISIBLE);
			imgStatusBar.setBackgroundColor(ctx.getResources().getColor(R.color.pending));
			txtStatusText.setText("chờ");
		}
		return convertView;
	}

}
