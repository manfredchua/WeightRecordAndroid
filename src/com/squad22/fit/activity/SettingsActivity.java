package com.squad22.fit.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.squad22.fit.R;

public class SettingsActivity extends Activity implements OnClickListener {

	ActionBar actionBar;
	TextView txtRemind;
//	ImageView ivMetric;
//	ImageView ivImperial;
//	RelativeLayout rlMetric;
//	RelativeLayout rlImperial;
//	TextView txtSina;
//	TextView txtWeChat;
//	TextView txtRenren;
//	Button btnSave;
//	ProfileDao dao = ProfileDao.getInstance();
//	Profile profile = new Profile();
//	boolean isCheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.settings_layout);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");

		initView();

//		profile = dao.getProfile(this);
//
//		if (profile.unit != null && profile.unit.equals(Constants.IMPERIAL)) {
//			isCheck = true;
//			ivImperial.setVisibility(View.VISIBLE);
//			ivImperial.setImageResource(R.drawable.navigation_accept);
//			ivMetric.setVisibility(View.INVISIBLE);
//		} else {
//			isCheck = false;
//			ivMetric.setVisibility(View.VISIBLE);
//			ivMetric.setImageResource(R.drawable.navigation_accept);
//			ivImperial.setVisibility(View.INVISIBLE);
//		}
	}

	private void initView() {
		txtRemind = (TextView) findViewById(R.id.txt_remind);
//		ivMetric = (ImageView) findViewById(R.id.iv_metric);
//		ivImperial = (ImageView) findViewById(R.id.iv_imperial);
//		rlMetric = (RelativeLayout) findViewById(R.id.rl_metric);
//		rlImperial = (RelativeLayout) findViewById(R.id.rl_imperial);
//
//		txtSina = (TextView) findViewById(R.id.txt_sina);
//		txtWeChat = (TextView) findViewById(R.id.txt_wechat);
//		txtRenren = (TextView) findViewById(R.id.txt_renren);
//		btnSave = (Button) findViewById(R.id.btn_save);
//
//		btnSave.setOnClickListener(this);
//		rlMetric.setOnClickListener(this);
//		rlImperial.setOnClickListener(this);
		txtRemind.setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.rl_imperial:
//			isCheck = true;
//			ivImperial.setVisibility(View.VISIBLE);
//			ivImperial.setImageResource(R.drawable.navigation_accept);
//			ivMetric.setVisibility(View.INVISIBLE);
//
//			break;
//		case R.id.rl_metric:
//			isCheck = false;
//			ivMetric.setVisibility(View.VISIBLE);
//			ivMetric.setImageResource(R.drawable.navigation_accept);
//			ivImperial.setVisibility(View.INVISIBLE);
//
//			break;
//		case R.id.btn_save:
//			if (isCheck) {
//				formatLB();
//			} else {
//				formatKG();
//			}
//			break;
		case R.id.txt_remind:
			
			break;
		default:
			break;
		}
	}

//	private void formatLB() {
//		if (!profile.unit.equals(Constants.IMPERIAL)) {
//			format(profile, Constants.ft, Constants.lb, Constants.IMPERIAL);
//		}
//	}
//
//	private void formatKG() {
//		if (!profile.unit.equals(Constants.METRIC)) {
//			format(profile, Constants.m, Constants.kg, Constants.METRIC);
//		}
//	}

//	private void format(Profile profile, double convertHeight,
//			double convertWeight, String unit) {
//		profile.height = profile.height * convertHeight;
//		profile.weight = profile.weight * convertWeight;
//		profile.unit = unit;
//
//		int rowId = dao.updateProfile(this, profile);
//		if (rowId > 0) {
//			Intent intent = new Intent(this, MainActivity.class);
//			startActivity(intent);
//			WeightRecordApplication.getInstance().exit();
//			Log.i("FormatKG", "单位转换成kg/m");
//		}
//	}
}
