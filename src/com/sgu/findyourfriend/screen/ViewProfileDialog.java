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

import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;

public class ViewProfileDialog extends Dialog {

	private User mem;
	private String phonePublic;
	
	
	public ViewProfileDialog(Context context, int friendId) {
		super(context);
		
		mem = FriendManager.getInstance().hmRequestFriends.get(friendId).getUserInfo();
		phonePublic = FriendManager.getInstance().hmRequestFriends.get(friendId).getNumberLogin().get(0);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_friend_detail);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		
		((TextView)findViewById(R.id.txtName)).setText(mem.getName());
		((TextView)findViewById(R.id.txtPhoneNumber)).setText(phonePublic);
		((TextView)findViewById(R.id.txtEmail)).setText(mem.getEmail());
		
		if (mem.isPublic()) {
			((TextView)findViewById(R.id.txtAddress)).setText(mem.getAddress());
			((TextView)findViewById(R.id.txtAge)).setText("" + ((new Date(System.currentTimeMillis())).getYear() - mem.getBirthday().getYear()));
			((TextView)findViewById(R.id.txtSchool)).setText(mem.getSchool());
			((TextView)findViewById(R.id.txtWorkplace)).setText(mem.getWorkplace());
			((TextView)findViewById(R.id.txtFacebook)).setText(mem.getFblink());
			
		}
		
		
	}
}
