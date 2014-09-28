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

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MapWrapperLayout extends FrameLayout {
	public interface OnDragListener {
		public void onDrag(MotionEvent motionEvent);
	}

	private OnDragListener mOnDragListener;

	public MapWrapperLayout(Context context) {
		super(context);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mOnDragListener != null) {
			mOnDragListener.onDrag(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	public void setOnDragListener(OnDragListener mOnDragListener) {
		this.mOnDragListener = mOnDragListener;
	}
}
