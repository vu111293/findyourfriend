package com.sgu.findyourfriend.screen;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.sgu.findyourfriend.MessageManager;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.MessageAdapter;
import com.sgu.findyourfriend.ctr.ControlOptions;
import com.sgu.findyourfriend.model.Message;

public class MessageFragment extends Fragment {

	private static String TAG = "MESSAGE FRAGMENT";

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

		View rootView = inflater.inflate(R.layout.fragment_message, container,
				false);

		// set actionbar
		View bar = getActivity().getActionBar().getCustomView();
		bar.findViewById(R.id.grpItemControl).setVisibility(View.VISIBLE);
		bar.findViewById(R.id.txtSend).setVisibility(View.GONE);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
		this.activity = activity;
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

		messages = MessageManager.instance.getAllMessage();
		Log.i("MESSAGE", "numbwe sms db: " + messages.size());

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
						MessageManager.instance.deleteMessage(messages
								.get(position));
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
			int fId = Integer.parseInt(ControlOptions.getInstance().getHashMap("friendId"));
			openEditMessageScene(fId);
			ControlOptions.getInstance().finish();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		smsListView.setSelection(messages.size() - 1);
	}

	public void onDestroyView() {
		super.onDestroyView();
	}

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

}
