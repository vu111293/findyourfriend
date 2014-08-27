package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.model.Friend;

public class MessageContactAdapter extends ArrayAdapter<Friend> {
	private Context ctx;
	private ArrayList<Friend> data;
	private int rowLayout;
	private Map<Integer, View> myViews = new HashMap<Integer, View>();

	public MessageContactAdapter(Context ctx, int rowLayout,
			ArrayList<Friend> list) {
		super(ctx, rowLayout, list);
		this.ctx = ctx;
		this.rowLayout = rowLayout;
		this.data = list;
	}

	@Override
	public Friend getItem(int position) {
		return data.get(position);
	}

	public ArrayList<Friend> getData() {
		return data;
	}

	public void checkAll() {
		for (int i = 0; i < data.size(); ++i) {
			data.get(i).setCheck(true);
		}
		notifyDataSetChanged();
	}
	
	public void unCheckAll() {
		for (int i = 0; i < data.size(); ++i) {
			data.get(i).setCheck(false);
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = myViews.get(position);
		ViewHolder holder;
		Friend objBean;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(rowLayout, null);

			holder = new ViewHolder();

			holder.tvname = (TextView) view.findViewById(R.id.txtName);
			holder.tvAddress = (TextView) view
					.findViewById(R.id.txtPhoneNumber);
			holder.chkSelect = (CheckBox) view.findViewById(R.id.chkSelect);

			holder.chkSelect
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							getItem(position).setCheck(isChecked);
						}
					});
			view.setTag(holder);
			myViews.put(position, view);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		objBean = getItem(position);
		holder.tvname.setText(Html.fromHtml(objBean.getUserInfo().getName()));
		holder.tvAddress.setText(Html.fromHtml(objBean.getUserInfo().getAddress()));
		holder.chkSelect.setChecked(objBean.isCheck());
		return view;
	}

	class ViewHolder {
		public TextView tvname, tvAddress;
		public CheckBox chkSelect;
	}
}
