package com.sgu.findyourfriend.ctr;

import java.util.HashMap;


public class ControlOptions {
	private static ControlOptions instance;
	
	public static enum SCENE_NAME {
		MAP, MESSAGE, OPTIONS, NONE
	};
	
	
	private boolean commit;
	
	private HashMap<String, String> mhashmap;
	
	
	
	private ControlOptions() {
		commit = false;
		mhashmap = new HashMap<String, String>();
	}
	
	public synchronized static ControlOptions getInstance() {
		if (instance == null) {
			instance = new ControlOptions();
		}
		return instance;
	}
	
	public boolean isRequire() {
		return commit;
	}
	
	public void edit() {
		mhashmap.clear();
		commit = true;
	}
	
	public void putHashMap(String key, String value) {
		mhashmap.put(key, value);
	}
	
	public String getHashMap(String key) {
		return mhashmap.get(key);
	}
	
	public void finish() {
		commit = false;
	}
	
	
	

}
