package com.example.frindyourfriend;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class HistoryActivity extends Fragment {

	
	public  HistoryActivity() {
		// TODO Auto-generated constructor stub
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.activity_history, container, false);
          
        return rootView;
    }
}
