package com.sgu.findyourfriend.screen;

import java.util.HashSet;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.utils.PreferenceKeys;

public class SettingFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private final String TAG = SettingFragment.class.getName();

	private MultiSelectListPreference contactOfFiends;
	
	public SettingFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getPreferenceManager().setSharedPreferencesName(PreferenceKeys.SHARE_PREFERENCES_KEY);
		
		addPreferencesFromResource(R.xml.setting);
		//getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
//		setupPrefs();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		setupPrefs();
		
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	
	private void setupPrefs() {
		
		int numFriend = FriendManager.getInstance().memberFriends.size();
		String friendNames[] = new String[numFriend];
		HashSet<String> idOfFriends = new HashSet<String>();
		
		
		
		for (int i = 0; i < numFriend; ++i) {
			Friend f = FriendManager.getInstance().memberFriends.get(i);
			friendNames[i] = f.getUserInfo().getName();
			idOfFriends.add(f.getUserInfo().getId() + "");
		}
		
		// setup check all
		friendNames[0] = "Tất cả";
		
		 contactOfFiends = (MultiSelectListPreference)
	            getPreferenceScreen().findPreference("com.sgu.findyourfriend.defaultwarning");
		 contactOfFiends.setEntries(friendNames);
		 contactOfFiends.setValues(idOfFriends);
	}
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
//		if (key.equals(PreferenceKeys.mapType)) {
//			SettingManager.getInstance().getMapType();
//		}
		
		Log.i(TAG, "Setting: " + key);
	}
	
	
	
	
	
}
