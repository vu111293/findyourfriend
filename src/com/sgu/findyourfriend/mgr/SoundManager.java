/*
 * Copyright (C) 2014 Tubor Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sgu.findyourfriend.mgr;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager extends BaseManager {

	public static int ALERT_SOUND;
	private static SoundManager instance;
	
	public synchronized static SoundManager getInstance() {
		if (instance == null) {
			instance = new SoundManager();
		}
		return instance;
	}
	
	@Override
	public void init(Context context) {
		super.init(context);
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
