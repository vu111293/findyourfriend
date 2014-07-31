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

public class ForgetPasswordFragment extends BaseFragment {

	EditText phone,email;
	Button find;
	private Context ctx;
	
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
				// TODO Auto-generated method stub
				(new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						
					
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						Toast.makeText(ctx, "Sẽ Gởi Email Đến Bạn Trong Giây Lát", Toast.LENGTH_LONG).show();
						
						
						replaceFragment(new LoginFragment(), false);
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
