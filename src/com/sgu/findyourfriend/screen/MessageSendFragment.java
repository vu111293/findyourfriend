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
package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.MessageContactAdapter;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.IMessage;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.utils.Utility;

@SuppressLint("ValidFragment")
public class MessageSendFragment extends Fragment implements IMessage {

	private EditText editMessage;
	private Button btnSend;

	private ArrayList<Friend> contactData;
	private MessageContactAdapter contactAdapter;
	private ListView listView;
	private TextView txtEmpty;
	private Button btnClose;
	private Button btnSelectAll;
	private boolean isSelectMode = true;

	private int friendId;

	private MessageFragment mainFragment;

	public MessageSendFragment() {
	}

	public MessageSendFragment(MessageFragment mainFragment) {
		this.mainFragment = mainFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_send_message,
				container, false);

		btnSend = (Button) rootView.findViewById(R.id.btnSend);
		editMessage = (EditText) rootView.findViewById(R.id.editMessage);
		editMessage.requestFocus();

		btnSelectAll = (Button) rootView.findViewById(R.id.btnSelectAll);
		btnClose = (Button) rootView.findViewById(R.id.btnClose);
		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!Utility.checkConnectToNetworkContinue(getActivity()))
					return;

				final Dialog dialog = new Dialog(getActivity());

				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editMessage.getWindowToken(), 0);

				if (isSelectedReceiver()) {

					// mainFragment.setBottomSMS();

					// send to myself
					MessageManager.getInstance().sendMessage(
							editMessage.getText().toString(), getToAddress());
					editMessage.setText("");

					// finish fragment
					getActivity().getFragmentManager().popBackStackImmediate();
					((BaseContainerFragment) getParentFragment()).popFragment();
				} else {

					Utility.showDialog(Utility.WARNING, dialog, "Cảnh báo",
							"Bạn chưa chọn người nhận tin nhắn.");
				}
			}
		});

		// setup contact
		listView = (ListView) rootView.findViewById(R.id.list);

		btnSelectAll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSelectMode) {
					contactAdapter.checkAll();
					btnSelectAll.setText("Bỏ chọn");
				} else {
					contactAdapter.unCheckAll();
					btnSelectAll.setText("Chọn tất cả");
				}

				isSelectMode = !isSelectMode;
			}
		});

		btnClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().getFragmentManager().popBackStackImmediate();
				((BaseContainerFragment) getParentFragment()).popFragment();
			}
		});

		contactData = new ArrayList<Friend>(
				FriendManager.getInstance().pureFriends);

		for (Friend f : contactData) {
			f.setCheck(false);
		}

		// get arguments
		Bundle bundle = getArguments();
		friendId = bundle.getInt("friendId", -1);

		if (friendId >= 0) {
			for (Friend f : contactData) {
				if (f.getUserInfo().getId() == friendId) {
					f.setCheck(true);
					break;
				}
			}
		}

		contactAdapter = new MessageContactAdapter(getActivity(),
				R.layout.custom_contact_addfriend, contactData);
		listView.setAdapter(contactAdapter);

		txtEmpty = (TextView) rootView.findViewById(R.id.txtListEmpty);
		if (contactAdapter.getCount() > 0)
			txtEmpty.setVisibility(View.GONE);

		Log.i("TAG", "" + contactAdapter.getCount());

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private List<Integer> getToAddress() {
		List<Integer> addrs = new ArrayList<Integer>();

		for (Friend f : contactData) {
			if (f.isCheck()) {
				addrs.add(f.getUserInfo().getId());
			}
		}
		return addrs;
	}

	private boolean isSelectedReceiver() {
		for (Friend f : contactData) {
			if (f.isCheck())
				return true;
		}
		return false;
	}

	@Override
	public void addNewMessage(Message msg) {
		if (mainFragment != null)
			mainFragment.addNewMessage(msg);
	}

}
