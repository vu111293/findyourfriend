package com.sgu.findyourfriend.screen;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.MessageAdapter;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.IMessage;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.utils.Utility;

public class MessageFragment extends Fragment implements IMessage {

	private static String TAG = "MESSAGE FRAGMENT";
	private static boolean isRegister; // = false;
	private List<Message> messages;
	private MessageAdapter adapter;
	private EditText text;

	private Context context;
	private Activity activity;

	private ListView smsListView;

	private MessageFragment mThis = this;

	public MessageFragment() {
		isRegister = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// check and notify
		if (SettingManager.getInstance().getNoNewMesssage() > 0) {

			// broadcast update widget
			SettingManager.getInstance().setNoNewMessage(0);

			Intent intent = new Intent(Config.UPDATE_MESSAGE_WIDGET_ACTION);
			// Send Broadcast to Broadcast receiver with message
			context.sendBroadcast(intent);

			// send broadcast hide notify
			Intent intent2 = new Intent(Config.NOTIFY_UI);
			intent2.putExtra(Config.MESSAGE_NOTIFY, Config.HIDE);
			context.sendBroadcast(intent2);

		}

		View rootView = inflater.inflate(R.layout.fragment_message, container,
				false);

		MessageManager.getInstance().setMessageListener(this);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
		this.activity = activity;
		if (!isRegister) {
			activity.registerReceiver(mHandleMessageReceiver, new IntentFilter(
					com.sgu.findyourfriend.mgr.Config.UPDATE_UI));
			activity.registerReceiver(mHandleMessageReceiver, new IntentFilter(
					com.sgu.findyourfriend.mgr.Config.MAIN_ACTION));
			isRegister = true;
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		text = (EditText) view.findViewById(R.id.text);
		text.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// load send message fragment

				openEditMessageScene(-1);
			}
		});

		messages = MessageManager.getInstance().getAllMessage();
		Log.i("MESSAGE", "numbwe sms db: " + messages.size());
		adapter = new MessageAdapter(context, messages);

		// setup listview
		smsListView = (ListView) view.findViewById(R.id.listview);
		smsListView.setAdapter(adapter);

		smsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				return false;
			}

		});

		smsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builderSingle = new AlertDialog.Builder(
						activity);
				String[] items = { "Gửi mới", "Xóa" };
				builderSingle.setItems(items,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									// reply
									int id;
									if (adapter.getItem(position).isMine()) {
										id = adapter.getItem(position)
												.getIdReceiver();
									} else {
										id = adapter.getItem(position)
												.getIdSender();
									}

									if (id == 0) {
										Utility.showMessage(activity,
												"Không thể gửi cho admin");
									} else {
										openEditMessageScene(id);
									}

								} else if (which == 1) {
									// delete message
									MessageManager.getInstance().deleteMessage(
											messages.get(position));
									messages.remove(messages.get(position));
									adapter.notifyDataSetChanged();
									smsListView.setSelection(messages.size() - 1);
								}
							}
						});

				builderSingle.setNegativeButton("Hủy",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				builderSingle.show();
			}
		});
		smsListView.setSelection(messages.size() - 1);
	}

	@Override
	public void onResume() {
		super.onResume();
		smsListView.setSelection(messages.size() - 1);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (isRegister)
			getActivity().unregisterReceiver(mHandleMessageReceiver);
	}

	// public void onDestroyView() {
	// super.onDestroyView();
	// }

	public void addNewMessage(Message msg) {
		messages.add(msg);
		adapter.notifyDataSetChanged();
		smsListView.setSelection(messages.size() - 1);
	}

	private void openEditMessageScene(int friendId) {
		MessageSendFragment fragment = new MessageSendFragment(mThis);

		Bundle bundle = new Bundle();
		bundle.putInt("friendId", friendId);
		fragment.setArguments(bundle);

		((BaseContainerFragment) mThis.getParentFragment()).replaceFragment(
				fragment, true);
	}

	// --------------handle message ----------------------------------
	// handle message
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctx, Intent intent) {
			String action = intent.getAction();

			if (action.equals(Config.MAIN_ACTION)) {
				if (intent.hasExtra(Config.EDIT_MESSAGE_ACTION)) {
					int fID = intent
							.getIntExtra(Config.EDIT_MESSAGE_ACTION, -1);
					Log.i("MESSAGE", fID + "");
					if (fID >= 0) {

						openEditMessageScene(fID);
					}
				}

			} else if (action.equals(Config.UPDATE_UI)) {
				if (intent.getStringExtra(
						(com.sgu.findyourfriend.mgr.Config.UPDATE_TYPE))
						.equals(Utility.MESSAGE)) {

					adapter.notifyDataSetChanged();
					smsListView.setSelection(messages.size() - 1);
				}

			}

		}
	};

}
