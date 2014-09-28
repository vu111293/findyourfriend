/*
 * Copyright (C) 2014 Tubor Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

public class MessageAdapter extends BaseAdapter {

	public static int MINE_TYPE = 0;
	public static int FRIEND_TYPE = 1;
	public static int COUNT_TYPE = 2;

	private List<Message> mMessages;
	private LayoutInflater mInflater;

	private SimpleDateFormat sdfDate;

	@SuppressLint("SimpleDateFormat")
	public MessageAdapter(Context context, List<Message> messages) {
		super();
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

			holder.expendMsg = (TextView) view.findViewById(R.id.expend_text);
			holder.message = (TextView) view.findViewById(R.id.message_text);
			holder.imgSender = (ImageView) view
					.findViewById(R.id.imgIconMessage);
			holder.txtName = (TextView) view.findViewById(R.id.txtName);
			holder.txtTime = (TextView) view.findViewById(R.id.txtTime);

			view.setTag(holder);
		} else {
			view = convertView;
		}

		ViewHolder holder = (ViewHolder) view.getTag();

		holder.expendMsg.setText(message.getExpendMsg());
		holder.message.setText(message.getMessage());

		if (message.isMine()) {
			holder.txtName.setText(message.getReceiverName());
		} else {
			holder.txtName.setText(message.getSenderName());
		}

		if (null != FriendManager.getInstance().hmImageP.get(message
				.getIdSender())) {
			holder.imgSender
					.setImageDrawable(FriendManager.getInstance().hmImageP
							.get(message.getIdSender()));
		} else if (message.getIdSender() == 0) {
			// admin
			holder.imgSender.setImageResource(R.drawable.ic_launcher);
		} else {
			// no avatar
			holder.imgSender.setImageResource(R.drawable.ic_no_imgprofile);
		}

		holder.txtTime.setText(sdfDate.format(message.getSmsTime()));

		// set image view
		return view;
	}

	private static class ViewHolder {
		TextView message;
		TextView expendMsg;
		TextView txtName;
		ImageView imgSender;
		TextView txtTime;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
