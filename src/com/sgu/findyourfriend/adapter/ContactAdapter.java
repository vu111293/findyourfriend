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
import com.sgu.findyourfriend.model.ContactBean;

public class ContactAdapter extends ArrayAdapter<ContactBean> {

	private Context ctx;
	private ArrayList<ContactBean> data;
	private int rowLayout;
	private Map<Integer, View> myViews = new HashMap<Integer, View>();

	public ContactAdapter(Context ctx, int rowLayout,
			ArrayList<ContactBean> list) {
		super(ctx, rowLayout, list);
		this.ctx = ctx;
		this.rowLayout = rowLayout;
		this.data = list;
	}

	@Override
	public ContactBean getItem(int position) {
		return data.get(position);
	}

	public ArrayList<ContactBean> getData() {
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
		ContactBean objBean;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(rowLayout, null);

			holder = new ViewHolder();

			holder.tvname = (TextView) view.findViewById(R.id.txtName);
			holder.tvPhoneNo = (TextView) view
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
		holder.tvname.setText(Html.fromHtml(objBean.getName()));
		holder.tvPhoneNo.setText(Html.fromHtml(objBean.getPhoneNo()));
		holder.chkSelect.setChecked(objBean.isCheck());
		return view;
	}

	class ViewHolder {
		public TextView tvname, tvPhoneNo;
		public CheckBox chkSelect;
	}
}
