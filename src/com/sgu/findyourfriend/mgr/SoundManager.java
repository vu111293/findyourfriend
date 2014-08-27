package com.sgu.findyourfriend.mgr;

import java.util.HashMap;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundManager {

	private static SoundManager instance;
//	private SoundPool soundPool;
//	private HashMap<Integer, Integer> soundPoolMap;
//	private Context context;
//	private float volume = 1.0f;
//	private boolean loaded = false;

	public static int ALERT_SOUND;

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
//		this.context = context;

		ALERT_SOUND = context.getResources().getIdentifier("correct", "raw",
				context.getPackageName());
	}

	public void playSound(Context context) {
		MediaPlayer mpintro = MediaPlayer.create(context, ALERT_SOUND);
		mpintro.setLooping(false);
		mpintro.start();
		mpintro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer player) {
				player.release();
			}
		});
	}

}
