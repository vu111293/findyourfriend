package com.sgu.findyourfriend.screen;

import com.sgu.findyourfriend.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class ProgressDialogCustom extends ProgressDialog {

	public ProgressDialogCustom(Context context) {
		super(context, R.style.progess_dialog_custom);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pending);
	}

}
