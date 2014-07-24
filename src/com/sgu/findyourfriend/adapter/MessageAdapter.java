package com.sgu.findyourfriend.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.model.Message;

/**
 * AwesomeAdapter is a Custom class to implement custom row in ListView
 * 
 * @author Adil Soomro
 * 
 */
public class MessageAdapter extends BaseAdapter {

	public static int MINE_TYPE = 0;
	public static int FRIEND_TYPE = 1;
	public static int COUNT_TYPE = 2;

	private Context mContext;
	private List<Message> mMessages;
	private LayoutInflater mInflater;
	
    private SimpleDateFormat sdfDate; 

	public MessageAdapter(Context context, List<Message> messages) {
		super();
		this.mContext = context;
		this.mMessages = messages;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 sdfDate = new SimpleDateFormat("dd/MM");
	}

	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Message getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public int getViewTypeCount() {
		return COUNT_TYPE;
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position).isMine())
			return MINE_TYPE;
		return FRIEND_TYPE;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message message = (Message) this.getItem(position);
		View view = null;

		if (convertView == null) {
			final ViewHolder holder = new ViewHolder();
			if (getItemViewType(position) == MINE_TYPE) {
				view = mInflater.inflate(R.layout.right_row_message, null);
			} else {
				view = mInflater.inflate(R.layout.left_row_message, null);
			}

			holder.message = (TextView) view.findViewById(R.id.message_text);
			holder.imgSender = (ImageView) view
					.findViewById(R.id.imgIconMessage);
			holder.txtSenderName = (TextView) view
					.findViewById(R.id.txtSenderName);
			holder.txtReceiverName = (TextView) view
					.findViewById(R.id.txtReceiverName);
			holder.txtTime = (TextView) view
					.findViewById(R.id.txtTime);
			
			// convertView.setTag(holder);
			view.setTag(holder);
		} else {
			view = convertView;
			// holder = (ViewHolder) convertView.getTag();
		}

		ViewHolder holder = (ViewHolder) view.getTag();

		holder.message.setText(message.getMessage());

		holder.txtSenderName
				.setText(FriendManager.getInstance().hmMemberFriends
						.get(message.getIdSender()).getUserInfo().getName());
		
		
		String receiverName = FriendManager.getInstance().hmMemberFriends
				.get(message.getIdReceiver()).getUserInfo().getName().trim();
		
		holder.txtReceiverName
		.setText(" gửi đến " + receiverName.substring(
				receiverName.lastIndexOf(' ') == -1 ? 0 : receiverName.lastIndexOf(' ')));
		
		holder.imgSender.setImageDrawable(FriendManager.getInstance().hmImageP
				.get(message.getIdSender()));

		holder.txtTime.setText(sdfDate.format(message.getSmsTime()));
		
//		if (getItemViewType(position) == MINE_TYPE) {
//			holder.txtSenderName.setText(MyProfileManager.getInstance().mine
//					.getName());
//			holder.imgSender
//					.setImageDrawable(FriendManager.getInstance().hmImageP
//							.get(7));
//
//		} else {
//			holder.txtSenderName
//					.setText(FriendManager.getInstance().hmMemberFriends.get(2)
//							.getUserInfo().getName());
//			holder.imgSender
//					.setImageDrawable(FriendManager.getInstance().hmImageP
//							.get(2));
//		}

		// set image view

		return view;
	}

	private static class ViewHolder {
		TextView message;
		TextView txtSenderName;
		TextView txtReceiverName;
		ImageView imgSender;
		TextView txtTime;
	}

	@Override
	public long getItemId(int position) {
		// Unimplemented, because we aren't using Sqlite.
		return position;
	}

}
