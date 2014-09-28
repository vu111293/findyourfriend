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
package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.mgr.SoundManager;
import com.sgu.findyourfriend.utils.Utility;

public class EmergenceActivity extends Activity {

	private static String patternLocation = "\nVị trí hiện tại: ";

	private TextView txtTicker;
	private int counter;
	private Vibrator vibrator;
	private boolean isStop;

	private boolean isVibrate;
	private boolean isSound;

	private String latlng;

	public void init() {
		isStop = false;

		SettingManager.getInstance().init(getApplicationContext());
		SoundManager.getInstance().init(getApplicationContext());

		isVibrate = SettingManager.getInstance().isVibrate();
		isSound = SettingManager.getInstance().isAlertRingtone();

		if (isVibrate)
			vibrator = (Vibrator) getApplicationContext().getSystemService(
					Context.VIBRATOR_SERVICE);

		latlng = getIntent().getStringExtra("latlng");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_emergency);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		init();

		txtTicker = (TextView) findViewById(R.id.txtTicker);
		findViewById(R.id.btnCancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						isStop = true;
					}
				});

		counter = 10;
		final Handler handler = new Handler();
		final Runnable r = new Runnable() {
			public void run() {
				if (counter >= 0 && !isStop) {
					txtTicker.setText(counter + "");

					// play sound
					if (isSound)
						SoundManager.getInstance().playSound(
								getApplicationContext());

					if (isVibrate) {
						if (counter > 0)
							vibrator.vibrate(300);
						else
							vibrator.vibrate(800);
					}
					counter--;
					handler.postDelayed(this, 1000);
				} else {
					if (!isStop) {
						// excute
						sendWarning();
						Utility.showMessage(getApplicationContext(),
								"đã gửi yêu cầu trợ giúp");
					}
					finish();
				}

			}
		};

		handler.postDelayed(r, 0);
	}

	private void sendWarning() {
		Set<String> friendIds = SettingManager.getInstance()
				.getDefaultWarning();
		StringBuilder defaultMsg = new StringBuilder();
		defaultMsg.append(SettingManager.getInstance().getDefaultMsg());

		defaultMsg.append(patternLocation + latlng);

		List<Integer> addrs = new ArrayList<Integer>();

		for (String fID : friendIds) {
			addrs.add(Integer.parseInt(fID));
		}

		MessageManager.getInstance().init(EmergenceActivity.this);
		MessageManager.getInstance().sendMessage(defaultMsg.toString(), addrs);
	}
}
