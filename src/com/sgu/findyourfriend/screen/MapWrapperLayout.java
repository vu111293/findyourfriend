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
