package com.sgu.findyourfriend.screen;



import android.content.Context;
import android.preference.DialogPreference;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.sgu.findyourfriend.R;

public final class ChangePasswordPreference extends DialogPreference {

	private EditText mCurrentPassword;
	private EditText mNewPassword;
	private EditText mRetypePassword;
	private CheckBox mShowPassword;
	private TextView mForgetPassword;

	public ChangePasswordPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setPositiveButtonText("Save");
	}

	@Override
	protected View onCreateDialogView() {
		
		// Inflate layout
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.layout_changepassword, null);

		mCurrentPassword=(EditText) view.findViewById(R.id.pass_current);
		mNewPassword=(EditText) view.findViewById(R.id.pass_newpass);
		mRetypePassword=(EditText) view.findViewById(R.id.pass_retype);
		mShowPassword=(CheckBox) view.findViewById(R.id.chbx_showpass);
		mForgetPassword=(TextView) view.findViewById(R.id.txt_forgetpass);
		mForgetPassword.setText(Html.fromHtml("<a href='#'>Quên mật khẩu ?</a>"));
		mForgetPassword.setMovementMethod(LinkMovementMethod.getInstance());
		mShowPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    mNewPassword.setInputType(129);
                }
            }
        });
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
