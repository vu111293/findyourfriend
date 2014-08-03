package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.drive.internal.ac;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.MessageAdapter;
import com.sgu.findyourfriend.ctr.ControlOptions;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.utils.Utility;

public class MessageFragment extends Fragment {

	private static String TAG = "MESSAGE FRAGMENT";
	private static boolean isRegister = false;
	private List<Message> messages;
	private MessageAdapter adapter;
	private EditText text;

	private Context context;
	private Activity activity;

	private ListView smsListView;

	private MessageFragment mThis = this;

	public MessageFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// check and notify
		if (SettingManager.getInstance().getNoNewMesssage() > 0) {
			SettingManager.getInstance().setNoNewMessage(0);
			MessageManager.getInstance().sendUpdateMessageWidget();
		}

		View rootView = inflater.inflate(R.layout.fragment_message, container,
				false);

		// set actionbar
		View bar = getActivity().getActionBar().getCustomView();
		bar.findViewById(R.id.grpItemControl).setVisibility(View.VISIBLE);
		bar.findViewById(R.id.imgSend).setVisibility(View.GONE);

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

		// List<Message> ls = new ArrayList<Message>();
		// ls.add(new Message("message", true));
		// ls.add(new Message("message", false));
		// ls.add(new Message("message", true));
		// ls.add(new Message("message", false));

		adapter = new MessageAdapter(context, messages);

		// setup listview
		smsListView = (ListView) view.findViewById(R.id.listview);
		smsListView.setAdapter(adapter);
		smsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Log.i("CLICK", position + "");

				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Warning!");
				builder.setMessage("Do you want to delete this message?");
				builder.setPositiveButton("Ok", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						MessageManager.getInstance().deleteMessage(
								messages.get(position));
						messages.remove(messages.get(position));
						adapter.notifyDataSetChanged();
						smsListView.setSelection(messages.size() - 1);
					}
				});
				builder.setNegativeButton("Cancel", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				builder.show();
			}
		});
		smsListView.setSelection(messages.size() - 1);

		// check require from sliding friend event
		if (ControlOptions.getInstance().isRequire()) {
			Log.i("REQUIRE", "####################################");
			int fId = ControlOptions.getInstance().getHashMap("friendId");
			openEditMessageScene(fId);
			ControlOptions.getInstance().finish();
		}
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
		SendMessageFragment fragment = new SendMessageFragment(mThis);

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
			if (intent.getStringExtra(
					(com.sgu.findyourfriend.mgr.Config.UPDATE_TYPE)).equals(
					Utility.MESSAGE)) {

				adapter.notifyDataSetChanged();
				smsListView.setSelection(messages.size() - 1);
			}
		}
	};

}
