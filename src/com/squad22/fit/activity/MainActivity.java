package com.squad22.fit.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.squad22.fit.R;
import com.squad22.fit.dialogfragment.SleepDialogFragment;
import com.squad22.fit.dialogfragment.WaterDialogFragment;
import com.squad22.fit.entity.ActionEntity;
import com.squad22.fit.task.SyncTask;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.DensityUtil;

@SuppressLint({ "ResourceAsColor", "SimpleDateFormat" })
public class MainActivity extends FragmentActivity implements TabListener {

	ActionBar actionbar;
	ViewPager pager;
	TabFragmentPagerAdapter mAdapter;
	ActionProvider mActionProvider;
	PopupWindow actionAddPw;
	PopupWindow sleepPw;
	ProgressDialog proDialog;
	ArrayList<ActionEntity> arrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.profile_layout);

		if (CommUtils.isAutoSync(MainActivity.this)) {
			proDialog = CommUtils.createProgressDialog(MainActivity.this,
					proDialog, getString(R.string.syncing));

			new SyncTask(MainActivity.this, mHandler, true).execute();
		}

		arrayList = CommUtils.setArrayList();

		pager = (ViewPager) findViewById(R.id.pager);

		actionbar = getActionBar();
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(mAdapter);

		for (int i = 0; i < 3; i++) {
			Tab tab = actionbar.newTab();
			tab.setText(mAdapter.getPageTitle(i)).setTabListener(this);
			if (i == 0) {
				tab.setIcon(R.drawable.calendar_selected);
			} else if (i == 1) {
				tab.setIcon(R.drawable.male_selected);
			} else {
				tab.setIcon(R.drawable.more_selected);
			}
			actionbar.addTab(tab);
		}
		
//		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				actionbar.setSelectedNavigationItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (proDialog != null && proDialog.isShowing()) {
					proDialog.dismiss();
				}
				break;

			default:
				break;
			}

		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			showActionMenuWindow();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class TabFragmentPagerAdapter extends FragmentPagerAdapter {
		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			Fragment ft = null;
			switch (arg0) {
			case 0:
				ft = new FragmentPage2();
				break;
			case 1:
				ft = new FragmentPage1();
				break;
			case 2:
				ft = new FragmentPage3();
				break;
			}
			return ft;
		}

		@Override
		public int getCount() {
			return 3;
		}

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		pager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@SuppressWarnings("deprecation")
	private void showActionMenuWindow() {
		View view = getLayoutInflater().inflate(R.layout.activity_list_item,
				null);

		actionAddPw = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				DensityUtil.dip2px(this, 75), true);
		actionAddPw.setBackgroundDrawable(new BitmapDrawable());
		actionAddPw.showAsDropDown(findViewById(R.id.action_add), 0, 0);

		ImageView ivSleep = (ImageView) view.findViewById(R.id.iv_sleep);
		ImageView ivMeal = (ImageView) view.findViewById(R.id.iv_food);
		ImageView ivMeasurement = (ImageView) view
				.findViewById(R.id.iv_measurement);
		ImageView ivExercise = (ImageView) view.findViewById(R.id.iv_exercise);
		ImageView ivWater = (ImageView) view.findViewById(R.id.iv_water);

		ivSleep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SleepDialogFragment fragment = new SleepDialogFragment(
						MainActivity.this);
				fragment.show(getFragmentManager(), "sleep");
				if (actionAddPw != null) {
					actionAddPw.dismiss();
				}
			}
		});

		ivMeal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						RecordMealActivity.class);
				startActivity(intent);
				if (actionAddPw != null) {
					actionAddPw.dismiss();
				}
			}
		});
		ivMeasurement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						RecordMeasurementActivity.class);
				startActivity(intent);
				if (actionAddPw != null) {
					actionAddPw.dismiss();
				}
			}
		});
		ivExercise.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						RecordExerciseActivity.class);
				startActivity(intent);
				if (actionAddPw != null) {
					actionAddPw.dismiss();
				}
			}
		});
		ivWater.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WaterDialogFragment newfragment = new WaterDialogFragment(
						MainActivity.this);
				newfragment.show(getFragmentManager(), "water");
				if (actionAddPw != null) {
					actionAddPw.dismiss();
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		WeightRecordApplication.getInstance().exit();
	}

}
