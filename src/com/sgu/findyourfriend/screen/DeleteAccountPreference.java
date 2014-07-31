package com.sgu.findyourfriend.screen;



import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.sgu.findyourfriend.R;

public final class DeleteAccountPreference extends DialogPreference {

	public DeleteAccountPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setPositiveButtonText("Delete");
	}

	@Override
	protected View onCreateDialogView() {
		
		// Inflate layout
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.layout_deleteaccount, null);
		return view;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		// Return if change was cancelled
		if (!positiveResult) {
			return;
		}

		// Persist current value if needed
		if (shouldPersist()) {

		}

		// Notify activity about changes (to update preference summary line)
		notifyChanged();
	}
}
