package com.sgu.findyourfriend.screen;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.utils.Utility;

public class HelpInfoDialog extends Dialog {

	public HelpInfoDialog(Context context) {
		super(context, R.style.full_screen_dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help_dialog_custom);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		
		
		Utility.showMessage(getContext(), "Đang hoàn thiện");
		
	}

}
