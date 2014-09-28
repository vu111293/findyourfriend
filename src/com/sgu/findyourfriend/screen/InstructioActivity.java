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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.screen.tips.Ins_Add_Friend;
import com.sgu.findyourfriend.screen.tips.Ins_Add_Friend_Main;
import com.sgu.findyourfriend.screen.tips.Ins_End;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_0;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_1;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_Main;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_Shared_0;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_Shared_1;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_Shared_2;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_Shared_3;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_Shared_4;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_Shared_5;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_Shared_6;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_Shared_7;
import com.sgu.findyourfriend.screen.tips.Ins_Friend_Shared_Main;
import com.sgu.findyourfriend.screen.tips.Ins_Main;
import com.sgu.findyourfriend.screen.tips.Ins_Map_Icon;
import com.sgu.findyourfriend.screen.tips.Ins_Me_0;
import com.sgu.findyourfriend.screen.tips.Ins_Me_Main;
import com.sgu.findyourfriend.screen.tips.Ins_Tsk_Alert;
import com.sgu.findyourfriend.screen.tips.Ins_Tsk_Friend;
import com.sgu.findyourfriend.screen.tips.Ins_Tsk_Friend_List;
import com.sgu.findyourfriend.screen.tips.Ins_Tsk_Main;
import com.sgu.findyourfriend.screen.tips.Ins_Tsk_Map;
import com.sgu.findyourfriend.screen.tips.Ins_Tsk_Msg;
import com.sgu.findyourfriend.screen.tips.Ins_Tsk_Setting;

public class InstructioActivity extends FragmentActivity {

	SparseArray<Fragment> ins;
	int idx = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_emergency);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		
		setContentView(R.layout.instruction);

		final ImageButton btNext = (ImageButton) findViewById(R.id.button_ins_next);
		final ImageButton btBack = (ImageButton) findViewById(R.id.button_ins_back);
		final ImageButton btSkip = (ImageButton) findViewById(R.id.button_ins_skip);

		ins = new SparseArray<Fragment>();
		ins.append(0, new Ins_Main());
		ins.append(1, new Ins_Tsk_Main());
		ins.append(2, new Ins_Tsk_Map());
		ins.append(3, new Ins_Tsk_Msg());
		ins.append(4, new Ins_Tsk_Friend());
		ins.append(5, new Ins_Tsk_Setting());
		ins.append(6, new Ins_Tsk_Alert());
		ins.append(7, new Ins_Tsk_Friend_List());

		ins.append(8, new Ins_Add_Friend_Main());
		ins.append(9, new Ins_Add_Friend());

		ins.append(10, new Ins_Map_Icon());

		ins.append(11, new Ins_Friend_Shared_Main());
		ins.append(12, new Ins_Friend_Shared_0());
		ins.append(13, new Ins_Friend_Shared_1());
		ins.append(14, new Ins_Friend_Shared_2());
		ins.append(15, new Ins_Friend_Shared_3());
		ins.append(16, new Ins_Friend_Shared_4());
		ins.append(17, new Ins_Friend_Shared_5());
		ins.append(18, new Ins_Friend_Shared_6());
		ins.append(19, new Ins_Friend_Shared_7());

		ins.append(20, new Ins_Friend_Main());
		ins.append(21, new Ins_Friend_0());
		ins.append(22, new Ins_Friend_1());

		ins.append(23, new Ins_Me_Main());
		ins.append(24, new Ins_Me_0());
		ins.append(25, new Ins_End());

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		// transaction.setCustomAnimations(R.anim.enter, R.anim.exit,
		// R.anim.pop_enter, R.anim.pop_exit);
		transaction.addToBackStack(null);
		transaction.replace(R.id.container_framelayout, ins.get(0));
		transaction.commit();
		btBack.setEnabled(false);
		btBack.setAlpha(0.3f);

		OnClickListener btNextListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				if (idx < ins.size() - 1) {
					idx++;
					transaction.replace(R.id.container_framelayout,
							ins.get(idx));
					transaction.commit();
				}
				if (idx == ins.size() - 1) {
					btNext.setEnabled(false);
					btNext.setAlpha(0.3f);
					btSkip.setEnabled(false);
					btSkip.setAlpha(0.3f);
				}

				btBack.setEnabled(true);
				btBack.setAlpha(1.0f);
			}
		};

		OnClickListener btBackListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				if (idx > 0) {
					idx--;
					transaction.replace(R.id.container_framelayout,
							ins.get(idx));
					transaction.commit();
				}
				if (idx == 0) {
					btBack.setEnabled(false);
					btBack.setAlpha(0.3f);
				}

				btNext.setEnabled(true);
				btNext.setAlpha(1.0f);
				btSkip.setEnabled(true);
				btSkip.setAlpha(1.0f);

			}
		};

		OnClickListener btSkipListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				idx = ins.size() - 1;
				transaction.replace(R.id.container_framelayout, ins.get(idx));
				transaction.commit();

				btNext.setEnabled(false);
				btNext.setAlpha(0.3f);
				btSkip.setEnabled(false);
				btSkip.setAlpha(0.3f);
				btBack.setEnabled(true);
				btBack.setAlpha(1.0f);

			}
		};
		btNext.setOnClickListener(btNextListener);
		btBack.setOnClickListener(btBackListener);
		btSkip.setOnClickListener(btSkipListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
