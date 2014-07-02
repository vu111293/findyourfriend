package com.sgu.findyourfriend.screen;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgu.findyourfriend.R;

public class HelpFragment extends Fragment {
	
	public HelpFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
         
        return rootView;
    }
}
