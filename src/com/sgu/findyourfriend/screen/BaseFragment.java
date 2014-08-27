package com.sgu.findyourfriend.screen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.sgu.findyourfriend.R;

public class BaseFragment extends Fragment {
	public void replaceFragment(Fragment fragment, boolean addToBackStack) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.replace(R.id.container_framelayout, fragment);
		transaction.commit();
		// getChildFragmentManager().executePendingTransactions();
	}

	public boolean popFragment() {
		Log.e("test", "pop fragment: "
				+ getFragmentManager().getBackStackEntryCount());
		boolean isPop = false;
		if (getFragmentManager().getBackStackEntryCount() > 0) {
			isPop = true;
			getFragmentManager().popBackStack();
		}
		return isPop;
		// return true;
	}
}
