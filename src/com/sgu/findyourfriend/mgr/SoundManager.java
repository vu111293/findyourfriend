package com.sgu.findyourfriend.mgr;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.sgu.findyourfriend.R;

public class SoundManager {
	
	private static SoundManager instance;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	private Context context;
	private float volume = 1.0f;
	
	public static final int tickSound = R.raw.tick;
	
	private SoundManager() {
		// TODO Auto-generated constructor stub
	}
	
	public synchronized static SoundManager getInstance() {
		if (instance == null) {
			instance = new SoundManager();
		}
		return instance;
	}
	
	public void init(Context context) {
		this.context = context;
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(tickSound, soundPool.load(context, tickSound, 1));
		
	}
	
	public void playSound(Context context, int soundID) {
		// soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
		MediaPlayer mp = MediaPlayer.create(context, soundID);
	   	mp.start();
	} 
		
}
