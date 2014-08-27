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
		sdfDate = new SimpleDateFormat("hh:mm dd/MM");
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
			holder.txtName = (TextView) view
					.findViewById(R.id.txtName);
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

		if(message.isMine()) {
			holder.txtName
			.setText(message.getReceiverName());
		} else {
			holder.txtName
			.setText(message.getSenderName());
		}
		
		
		if (null != FriendManager.getInstance().hmImageP
				.get(message.getIdSender()))
			holder.imgSender.setImageDrawable(FriendManager.getInstance().hmImageP
				.get(message.getIdSender()));
		else 
			holder.imgSender.setImageResource(R.drawable.ic_no_imgprofile);

		holder.txtTime.setText(sdfDate.format(message.getSmsTime()));
		
		// set image view

		return view;
	}

	private static class ViewHolder {
		TextView message;
		TextView txtName;
		ImageView imgSender;
		TextView txtTime;
	}

	@Override
	public long getItemId(int position) {
		// Unimplemented, because we aren't using Sqlite.
		return position;
	}

}
