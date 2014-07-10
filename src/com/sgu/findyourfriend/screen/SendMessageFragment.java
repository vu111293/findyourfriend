package com.sgu.findyourfriend.screen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.devsmart.android.ui.HorizontalListView;
import com.sgu.findyourfriend.FriendManager;
import com.sgu.findyourfriend.IMessage;
import com.sgu.findyourfriend.MessageManager;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.FriendSwipeAdapter;
import com.sgu.findyourfriend.model.Message;

public class SendMessageFragment extends Fragment implements IMessage {

	private HorizontalListView friendsHList;
	private FriendSwipeAdapter swipeAdapter;

	private TextView txtSendInfo;
	private EditText editMessage;

	private int[] masks;
	private Context context;
	private Activity activity;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getActivity().getWindow().setLayout(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editMessage, InputMethodManager.SHOW_IMPLICIT);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_send_message,
				container, false);

		View view = getActivity().getActionBar().getCustomView();
		view.findViewById(R.id.grpItemControl).setVisibility(View.GONE);
		TextView textSendEvent = (TextView) view.findViewById(R.id.txtSend);
		textSendEvent.setVisibility(View.VISIBLE);

		// init masks
		masks = new int[FriendManager.instance.friends.size()];

		editMessage = (EditText) rootView.findViewById(R.id.editMessage);

		/*
		 * editMessage.setOnFocusChangeListener(new View.OnFocusChangeListener()
		 * {
		 * 
		 * @Override public void onFocusChange(View v, boolean hasFocus) { if
		 * (!hasFocus) { hideKeyboard(getActivity()); } else {
		 * showKeyboard(getActivity()); } } });
		 */

		editMessage.requestFocus();

		textSendEvent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// send to myself
				MessageManager.instance.sendMessage(editMessage.getText()
						.toString(), null);
				editMessage.setText("");

				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editMessage.getWindowToken(), 0);

				// finish fragment
				getActivity().getFragmentManager().popBackStackImmediate();
				((BaseContainerFragment) getParentFragment()).popFragment();
			}
		});

		friendsHList = (HorizontalListView) rootView
				.findViewById(R.id.avatarListView);
		swipeAdapter = new FriendSwipeAdapter(getActivity(),
				FriendManager.instance.friends);
		friendsHList.setAdapter(swipeAdapter);

		friendsHList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				masks[position] = 1 - masks[position];
				changeMask(position, view);
			}

		});

		MessageManager.instance.setMessageListener(this);

		// getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = activity.getApplicationContext();
		this.activity = activity;
	}

	public static void showKeyboard(Activity activity) {
		if (activity != null) {
			activity.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}

	public static void hideKeyboard(Activity activity) {
		if (activity != null) {
			activity.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
	}

	private void changeMask(int pos, View view) {
		StringBuilder builder = new StringBuilder();
		int len = FriendManager.instance.friends.size();
		boolean firstF = false;

		if (pos == 0) {
			if (masks[pos] == 0) {
				view.setBackgroundColor(0x00);
				// them cai da luu chon vao txt
				for (int i = 1; i < len; ++i) {
					if (masks[i] == 1) {
						if (firstF)
							builder.append("; ");
						else
							firstF = true;

						builder.append(FriendManager.instance.friends.get(i)
								.getUserInfo().getName());
					}
				}

			} else {
				view.setBackgroundColor(0xff086EBC);
				// them tat ca vao txt
				for (int i = 1; i < len; ++i) {
					if (firstF)
						builder.append("; ");
					else
						firstF = true;

					builder.append(FriendManager.instance.friends.get(i)
							.getUserInfo().getName());

				}
			}
		} else {
			if (masks[pos] == 0) {
				// huy chon
				view.setBackgroundColor(0x00);
			} else {
				// chon
				view.setBackgroundColor(0xff086EBC);
			}

			if (masks[0] == 0)
				// hieu chinh txt
				for (int i = 1; i < len; ++i) {
					if (masks[i] == 1) {
						if (firstF)
							builder.append("; ");
						else
							firstF = true;

						builder.append(FriendManager.instance.friends.get(i)
								.getUserInfo().getName());
					}

				}
			else
				return;
		}
		txtSendInfo.setText(builder.toString());
	}

	@Override
	public void addNewMessage(Message msg) {
	}

}
