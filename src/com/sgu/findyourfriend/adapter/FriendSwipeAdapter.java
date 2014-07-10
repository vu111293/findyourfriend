package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.model.Friend;

public class FriendSwipeAdapter extends BaseAdapter {

	// private List<String> mData = new ArrayList<String>();

	public static int USER_ACCEPTED = 0;
	public static int USER_WAITACCCEPT = 1;
	public static int NUMBER_USER_TYPE = 2;

	private List<Friend> mData;
	private Context mContext;

	public FriendSwipeAdapter(Context context) {
		this.mContext = context;
		mData = new ArrayList<Friend>();
	}

	public FriendSwipeAdapter(Context context, List<Friend> friends) {
		this.mContext = context;
		this.mData = friends;
	}

	public void addItem(Friend friend) {
		mData.add(friend);
	}

	@Override
	public Friend getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public int getViewTypeCount() {
		return NUMBER_USER_TYPE;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return mData.get(position).getUserInfo().isAccepted() ? USER_ACCEPTED
				: USER_WAITACCCEPT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Friend friend = this.getItem(position);
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			if (friend.getUserInfo().isAccepted()) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_friend_accepted, parent, false);
				holder.imgAvatar = (ImageView) convertView
						.findViewById(R.id.imgAvatar);
			} else {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_friend_waitaccept, parent, false);
				holder.imgAvatar = (ImageView) convertView
						.findViewById(R.id.imgAvatar);
				holder.txtName = (TextView) convertView
						.findViewById(R.id.txtName);

			}
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (friend.getUserInfo().isAccepted()) {
			// edit
			holder.imgAvatar.setImageResource(R.drawable.avatar2);
		} else {
			// edit
			holder.imgAvatar.setImageResource(R.drawable.ic_notavailable);
			holder.txtName.setText(friend.getUserInfo().getName());			
		}



		return convertView;
	}

	private class ViewHolder {
		public ImageView imgAvatar;
		public TextView txtName;
	}

}
