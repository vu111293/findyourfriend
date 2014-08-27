package com.sgu.findyourfriend.screen;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgu.findyourfriend.R;

public class CategoriesFragment extends Fragment {
	private View view = null;

	public CategoriesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}

		try {
			view = inflater.inflate(R.layout.activity_setting, container, false);
		} catch (InflateException e) {

		}
		
		view.setBackgroundColor(Color.WHITE);
		
		return view;
	}
	
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

}
