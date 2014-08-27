package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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

//	private HorizontalListView friendsHList;
//	private FriendSwipeAdapter swipeAdapter;

	private EditText editMessage;

	private int[] masks;
	private Context context;
	private Activity activity;
	private Button btnSend;
	private LinearLayout friendLayout;

	private ArrayList<Friend> contactData;
	private MessageContactAdapter contactAdapter;
	private ListView listView;
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
//		InputMethodManager imm = (InputMethodManager) getActivity()
//				.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.showSoftInput(editMessage, InputMethodManager.SHOW_IMPLICIT);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_send_message,
				container, false);

//		friendLayout = (LinearLayout) rootView.findViewById(R.id.layoutH);

		btnSend = (Button) rootView.findViewById(R.id.btnSend);


		// init masks
		masks = new int[FriendManager.getInstance().hmMemberFriends.size()];

		editMessage = (EditText) rootView.findViewById(R.id.editMessage);

		editMessage.requestFocus();

		btnSelectAll = (Button) rootView.findViewById(R.id.btnSelectAll);
		btnClose = (Button) rootView.findViewById(R.id.btnClose);
		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editMessage.getWindowToken(), 0);

				if (isSelectedReceiver()) {
					// send to myself
					MessageManager.getInstance().sendMessage(
							editMessage.getText().toString(), getToAddress());
					editMessage.setText("");

					// finish fragment
					getActivity().getFragmentManager().popBackStackImmediate();
					((BaseContainerFragment) getParentFragment()).popFragment();

				} else {
					Utility.showAlertDialog(getParentFragment().getActivity(),
							"", "Chọn người nhận", true);
//					friendLayout.setBackgroundColor(getActivity()
//							.getResources().getColor(R.color.pupors));

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
		
		
		contactData = new ArrayList<Friend>(FriendManager.getInstance().pureFriends);
		
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
		
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = activity.getApplicationContext();
		this.activity = activity;
	}

//	public static void showKeyboard(Activity activity) {
//		if (activity != null) {
//			activity.getWindow().setSoftInputMode(
//					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//		}
//	}
//
//	public static void hideKeyboard(Activity activity) {
//		if (activity != null) {
//			activity.getWindow().setSoftInputMode(
//					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		}
//	}

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
			if (f.isCheck()) return true;
		}
		return false;
	}

	@Override
	public void addNewMessage(Message msg) {
		if (mainFragment != null)
			mainFragment.addNewMessage(msg);
	}

}
