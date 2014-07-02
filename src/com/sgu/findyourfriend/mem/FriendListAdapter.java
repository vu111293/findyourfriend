package com.sgu.findyourfriend.mem;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.model.Friend;

public class FriendListAdapter extends BaseAdapter {

	private Context context;
	private List<Friend> mData;

	public FriendListAdapter(Context context, List<Friend> friendList) {
		this.context = context;
		this.mData = friendList;
	}

	public FriendListAdapter(Context context) {
		this.context = context;
		this.mData = new ArrayList<Friend>();
	}

	public void addItem(Friend fr) {
		this.mData.add(fr);
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Friend getItem(int pos) {
		return mData.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_friend, null);
			holder = new ViewHolder();

			holder.imgProfile = (ImageView) convertView
					.findViewById(R.id.imgProfile);
			holder.txtMemberName = (TextView) convertView
					.findViewById(R.id.txtMemberName);
			holder.txtPhoneNumber = (TextView) convertView
					.findViewById(R.id.txtPhoneNumber);
			holder.imgStatus = (ImageView) convertView
					.findViewById(R.id.imgStatus);

			Friend frd = mData.get(position);
			// holder.imgProfile.setImageResource(resId);
			holder.txtMemberName.setText(frd.getName());
			holder.txtPhoneNumber.setText(frd.getPhoneNumber());
			if (frd.isAvailable())
				holder.imgStatus.setImageResource(R.drawable.avaiable);
			else
				holder.imgStatus.setImageResource(R.drawable.notavaiable);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	private class ViewHolder {
		ImageView imgProfile;
		TextView txtMemberName;
		TextView txtPhoneNumber;
		ImageView imgStatus;
	}

}
