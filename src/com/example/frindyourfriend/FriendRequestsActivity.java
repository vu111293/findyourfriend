package com.example.frindyourfriend;

import java.util.ArrayList;

import com.example.frindyourfriend.adapter.CustomAdapter_FriendRequests;
import com.example.frindyourfriend.model.FriendRequest;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FriendRequestsActivity extends Fragment {
	ArrayList<FriendRequest> Data=new ArrayList<FriendRequest>();
	CustomAdapter_FriendRequests Adapter=null;
	ListView lv=null;
	Context ctx;
	 View rootView;
	
	public FriendRequestsActivity(){}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
         rootView = inflater.inflate(R.layout.activity_friendrequests, container, false);
         ctx=rootView.getContext();
         lv=(ListView)rootView.findViewById(R.id.listView_FriendRequests);
		
		Data.add(new FriendRequest("Nguyễn Chí Hùng", 100, ""));
		Data.add(new FriendRequest("Quốc Vũ", 1, ""));
		Data.add(new FriendRequest("Trương Toàn", 20, ""));
		Adapter=new CustomAdapter_FriendRequests(ctx, R.layout.custom_friendrequests, Data);

	
		 
		 
		lv.setAdapter(Adapter);
		Data.add(new FriendRequest("Nguyễn Chí Hùng", 100, ""));
		Data.add(new FriendRequest("Quốc Vũ", 1, ""));
		Data.add(new FriendRequest("Trương Toàn", 20, ""));
		Adapter.notifyDataSetChanged();
        return rootView;
    }
	
	

	

}
