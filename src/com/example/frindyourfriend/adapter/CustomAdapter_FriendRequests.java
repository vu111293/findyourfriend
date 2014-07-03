package com.example.frindyourfriend.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.example.frindyourfriend.R;
import com.example.frindyourfriend.R.id;
import com.example.frindyourfriend.model.FriendRequest;
import com.google.android.gms.internal.in;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter_FriendRequests extends ArrayAdapter<FriendRequest>{

	Context context;
	List<FriendRequest> Data=new ArrayList<FriendRequest>();
	int LayoutResID;
	public CustomAdapter_FriendRequests(Context context, int resource, ArrayList<FriendRequest> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.LayoutResID=resource;
		this.Data=objects;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent ){
		
		LayoutInflater inflater=((Activity)context).getLayoutInflater();
		convertView=inflater.inflate(LayoutResID, null);
		FriendRequest fr=Data.get(position);
		TextView Name=(TextView)convertView.findViewById(R.id.textView_friendrequests_name);
		TextView Mutual=(TextView)convertView.findViewById(R.id.textView_friendrequests_mutualfriends);
		
		Name.setText(fr.getName());
		Mutual.setText(fr.getMutualFriends()+" bạn chung");
		
		return convertView;
		
	}

}
