package com.sgu.findyourfriend.gps;

import java.util.ArrayList;
import java.util.List;

import com.sgu.findyourfriend.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SwipeAdapter extends BaseAdapter {

	private List<String> mData = new ArrayList<String>();

	public SwipeAdapter() {

	}

	public void addItem(String title) {
		mData.add(title);
	}

	@Override
	public String getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View retval = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.list_ava_item, null);

		TextView title = (TextView) retval.findViewById(R.id.title);
		title.setText(mData.get(position));
		return retval;
	}

}
