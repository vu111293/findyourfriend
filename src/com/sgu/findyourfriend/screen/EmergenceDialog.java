package com.sgu.findyourfriend.screen;

import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.mgr.SoundManager;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class EmergenceDialog extends Dialog {

	private TextView txtTicker;
	private int counter;
	private Vibrator vibrator;
	private boolean isStop;

	private boolean isVibrate;
	private boolean isSound;

	public EmergenceDialog(Context context) {
		super(context, R.style.full_screen_dialog);
		isStop = false;
		isVibrate = SettingManager.getInstance().isVibrate();
		isSound = SettingManager.getInstance().isAlertRingtone();

		if (isVibrate)
			vibrator = (Vibrator) context
					.getSystemService(Context.VIBRATOR_SERVICE);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alert_dialog_custom);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

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
						SoundManager.getInstance().playSound(getContext());

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
						Utility.showMessage(getContext(), "đã gửi cầu cứu");
					}
					dismiss();
				}

			}
		};

		handler.postDelayed(r, 0);
	}
	
	private void sendWarning() {
		Set<String> friendIds = SettingManager.getInstance().getFriendsWarning();
		
		String defaultMsg = SettingManager.getInstance().getDefaultMsg(); 
		
		
		if (SettingManager.getInstance().isEmailWarning()) {
			for (String fID : friendIds) {
				// DataPost.sendEmail(me, Integer.parseInt(fID));
			}
		}
		
		if (SettingManager.getInstance().isMessageWarning()) {
			for (String fID : friendIds) {
				PostData.sendMessage(getContext(), MyProfileManager.getInstance().getMyID(),
						Integer.parseInt(fID), defaultMsg);
			}
		}
	}
}
