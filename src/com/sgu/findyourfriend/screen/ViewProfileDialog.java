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
