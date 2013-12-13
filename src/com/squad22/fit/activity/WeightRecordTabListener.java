package com.squad22.fit.activity;

import com.squad22.fit.R;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;

public class WeightRecordTabListener implements TabListener {
	android.app.Fragment fragment;
	
	public WeightRecordTabListener(android.app.Fragment fragment){
		this.fragment = fragment;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.add(R.id.pager, fragment, null);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
	}

}
