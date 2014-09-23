/*
 * 	 This file is part of Find Your Friend.
 *
 *   Find Your Friend is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Find Your Friend is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Find Your Friend.  If not, see <http://www.gnu.org/licenses/>.
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
