package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.CustomAdapterCategories;
import com.sgu.findyourfriend.model.Category;

public class CategoriesFragment extends Fragment {
	List<Category> Data = new ArrayList<Category>();
	CustomAdapterCategories adapter;
	ListView lv;

	public CategoriesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_categories,
				container, false);

		Data.add(new Category("My Favorites")); // adding a header to the list
		Data.add(new Category("Message", "drawable://" + "drawable://"
				+ R.drawable.ic_action_email, 10));
		Data.add(new Category("Likes", "drawable://"
				+ R.drawable.ic_action_good));
		Data.add(new Category("Games", "drawable://"
				+ R.drawable.ic_action_gamepad));
		Data.add(new Category("Lables", "drawable://"
				+ R.drawable.ic_action_labels));

		Data.add(new Category("Main Options"));// adding a header to the list
		Data.add(new Category("Search", "drawable://"
				+ R.drawable.ic_action_search));
		Data.add(new Category("Cloud", "drawable://"
				+ R.drawable.ic_action_cloud));
		Data.add(new Category("Camara", "drawable://"
				+ R.drawable.ic_action_camera));
		Data.add(new Category("Video", "drawable://"
				+ R.drawable.ic_action_video));
		Data.add(new Category("Groups", "drawable://"
				+ R.drawable.ic_action_group, 5));
		Data.add(new Category("Import & Export", "drawable://"
				+ R.drawable.ic_action_import_export));

		Data.add(new Category("Other Option")); // adding a header to the list
		Data.add(new Category("About", "drawable://"
				+ R.drawable.ic_action_about));
		Data.add(new Category("Settings", "drawable://"
				+ R.drawable.ic_action_settings));
		Data.add(new Category("Help", "drawable://" + R.drawable.ic_action_help));
		adapter = new CustomAdapterCategories(rootView.getContext(),
				R.layout.custom_categories, Data);
		lv = (ListView) rootView.findViewById(R.id.ListView_Categories);
		lv.setAdapter(adapter);
		return rootView;
	}

}
