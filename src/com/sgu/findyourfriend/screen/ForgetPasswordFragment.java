package com.sgu.findyourfriend.screen;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class ForgetPasswordFragment extends BaseFragment {

	private EditText phone,email;
	private Button find;
	private Context ctx;
	private ProgressDialogCustom progress;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_forget_password, container,
				false);
		
		phone=(EditText)rootView.findViewById(R.id.EditText_ForgetPassword_Phone);
		email=(EditText)rootView.findViewById(R.id.EditText_ForgetPassword_Email);
		find=(Button)rootView.findViewById(R.id.Button_ForgetPassword_FindYourPass);
		find.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				boolean isEmpty = false;
				
				if (phone.getText().toString().trim().length() > 0) {
					phone.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edit_text));
				} else {
					phone.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edit_text_wrong));
					isEmpty = true;
				}

				if (Utility.isValidEmailAddress(email.getText().toString()
						.trim())) {
					email.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text));
				} else {
					email.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text_wrong));
					isEmpty = true;
				}

				if (isEmpty) {
					Utility.showAlertDialog(ctx, "Cảnh báo",
							"Nhập các thông tin yêu cầu", false);
					return;
				}
				
				// TODO Auto-generated method stub
				(new AsyncTask<Void, Void, Boolean>() {

					@Override
					protected void onPreExecute() {
						progress = new ProgressDialogCustom(ctx);
						progress.show();
					}
					
					@Override
					protected Boolean doInBackground(Void... params) {
						return PostData.userForgetPassword(getActivity(), phone.getText().toString().trim(),
								email.getText().toString().trim());
					}
					
					@Override
					protected void onPostExecute(Boolean result) {
						progress.dismiss();
						if (result) { 
							Utility.showAlertDialog(getActivity(), "Thông báo", "Sẽ gửi mail đến bạn trong giây lát", false);
							replaceFragment(new LoginFragment(), false);
						} else {
							Utility.showAlertDialog(getActivity(), "Cảnh báo", "Email hoặc số điện thoại không đúng", false);
							
						}
					}
					
				}).execute();
			}
		});
		
		
		
		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.ctx = activity;
	}
	
}
