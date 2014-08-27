package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Friend;

public class FriendSwipeAdapter extends ArrayAdapter<Friend> {
	
	private static int SELECT_MODE = 0;
	private static int UNSELECT_MODE = 1;
	
	private int LayoutResID;
	private Context ctx;
	private List<Friend> DataList = new ArrayList<Friend>();
	private Map<Integer, View> myViews = new HashMap<Integer, View>();

	private int itemIdOldHightLight = -1;
	private int itemIdHightLight = -1;
	private boolean isShowStatus;
	private int itemSelected = -1;
	
	private int mode = SELECT_MODE;

	public FriendSwipeAdapter(Context context, int resource,
			List<Friend> objects) {
		super(context, resource, objects);
		ctx = context;

		isShowStatus = true;

		int myID = MyProfileManager.getInstance().getMyID();

		// change my position item to first
		for (int i = 0; i < objects.size(); ++i) {
			if (objects.get(i).getUserInfo().getId() == myID) {
				Friend f = objects.remove(i);
				objects.add(0, f);
				break;
			}
		}

		objects.add(new Friend(-1));

		LayoutResID = resource;
		DataList = objects;

	}

	@Override
	public void notifyDataSetChanged() {

		if (DataList.get(DataList.size() - 1).getUserInfo().getId() != -1 && isShowStatus) {
			DataList.add(new Friend(-1));
		}

		super.notifyDataSetChanged();
	}

	public void hightLightItem(int pos) {
		itemIdOldHightLight = itemIdHightLight;
		itemIdHightLight = pos;
		notifyDataSetChanged();
	}

	public void unHightLightItem() {
		itemIdHightLight = -1;
		notifyDataSetChanged();
	}

	public void selectItem(int pos) {
		mode = SELECT_MODE;
		itemSelected = pos;
		notifyDataSetChanged();
	}
	
	public void unSelectItem(int pos) {
		mode = UNSELECT_MODE;
		itemSelected = pos;
		notifyDataSetChanged();
	}
	
	public void setShowStatus(boolean isShow) {
		this.isShowStatus = isShow;
		
		if (!isShow) {
			DataList.remove(DataList.size() - 1);
			notifyDataSetChanged();
		}
	}

	@Override
	public Friend getItem(int position) {
		return DataList.get(position);
	}

	public List<Friend> getData() {
		return DataList;
	}

	// return -1 if not found
	public int getPositionByFriendID(int friendID) {
		for (int i = 0; i < DataList.size(); ++i) {
			if (DataList.get(i).getUserInfo().getId() == friendID)
				return i;
		}
		return -1;
	}
	
	public int getDistaceToItem(int friendId) {
		int idx = getPositionByFriendID(friendId);
		
		if (idx == -1) return 0;
		
		// dis = 60 // padding = 5
		return idx * 55 + 30;
	}
	
	
	public int getWidth() {
		return DataList.size() * 55 - 5;
	}
	
	public View getViewByFriendId(int friendId) {
		
		for (int i = 0; i < DataList.size(); ++i) {
			if (DataList.get(i).getUserInfo().getId() == friendId) {
				
				return myViews.get(i);
			}
		}
		
		return null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = myViews.get(position);
		ViewHolder holder;
		Friend friend = this.getItem(position);

		if (null == view) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(LayoutResID, null);

			holder = new ViewHolder();

			holder.imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
			holder.imgWait = (ImageView) view.findViewById(R.id.imgWaitAlert);
			holder.imgRequest = (ImageView) view
					.findViewById(R.id.imgRequestAlert);
			holder.imgResponse = (ImageView) view
					.findViewById(R.id.imgResponseAlert);
			holder.imgSelect = (ImageView) view.findViewById(R.id.imgSelect);
			
			if (friend.getUserInfo().getId() == -1) {
				holder.imgAvatar.setBackgroundDrawable(null);
				holder.imgAvatar.setImageDrawable(getContext().getResources()
						.getDrawable(R.drawable.ic_addfriend));
			}

			view.setTag(holder);
			myViews.put(position, view);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if (friend.getUserInfo().getId() == -1)
			return view;

		holder.imgAvatar.setImageDrawable(FriendManager.getInstance().hmImageP
				.get(friend.getUserInfo().getId()));
		holder.imgWait.setVisibility(View.GONE);
		holder.imgRequest.setVisibility(View.GONE);
		holder.imgResponse.setVisibility(View.GONE);

		if (isShowStatus) {
			if (friend.getAcceptState() == Friend.REQUEST_SHARE) {
				holder.imgWait.setVisibility(View.VISIBLE);
			} else if (friend.getAcceptState() == Friend.REQUESTED_SHARE) {
				holder.imgRequest.setVisibility(View.VISIBLE);
			} else if (friend.getAcceptState() == Friend.FRIEND_RELATIONSHIP) {
				holder.imgResponse.setVisibility(View.VISIBLE);
			}

		}

		if (itemIdOldHightLight >= 0 && itemIdOldHightLight == position)
			holder.imgAvatar.setBackgroundDrawable(getContext().getResources()
					.getDrawable(R.drawable.border_bkg_noneselect));

		// holder.imgCheck.setVisibility(View.GONE);
		// view.setBackgroundColor(0x000000);

		
		if (itemIdHightLight == position)
			holder.imgAvatar.setBackgroundDrawable(getContext().getResources()
					.getDrawable(R.drawable.border_bkg_selected));
		// holder.imgCheck.setVisibility(View.VISIBLE);
		// view.setBackgroundColor(0xff2F497A);

		
		if (!isShowStatus) {
			if (mode == SELECT_MODE) {
			if (itemSelected == position)
				holder.imgSelect.setVisibility(View.VISIBLE);
//				holder.imgAvatar.setBackgroundDrawable(getContext().getResources()
//						.getDrawable(R.drawable.border_bkg_selected));
			} else {
				// unselect mode
				holder.imgSelect.setVisibility(View.GONE);
//				holder.imgAvatar.setBackgroundDrawable(getContext().getResources()
//						.getDrawable(R.drawable.border_bkg_noneselect));
			}
			
			
		}
		
		return view;
	}

	class ViewHolder {
		public ImageView imgAvatar;
		public ImageView imgWait;
		public ImageView imgResponse;
		public ImageView imgRequest;
		public ImageView imgSelect;
	}

}
