package com.squad22.fit.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.dao.ProfileDao;
import com.squad22.fit.entity.Profile;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

public class TargetMeasurementActivity extends Activity {

	ActionBar actionBar;
	TextView txtStandardWeight;
	TextView txtBodyFat;
	TextView txtLeanMusde;
	TextView txtBodyAge;
	TextView txtVisceralFat;
	TextView txtBMI;
	TextView txtBMR;
	TextView txtKcal;
	TextView txtUnit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.measure_layout);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.traget_measurement));
		initView();
		SharedPreferences sp = getSharedPreferences(Constants.KIIUSER, MODE_PRIVATE);
		String userName = sp.getString(Constants.USERNAME, "");
		Profile profile = ProfileDao.getInstance().getProfile(this, userName);
		if (profile.name != null) {
			setValue(profile);
		}

	}

	private void setValue(Profile profile) {
		String target = CommUtils.getTargetWeight(profile.height, profile.sex);
		txtStandardWeight.setText(target);

		int age = CommUtils.getAge(profile.birthday);
		String bodyFat = CommUtils.getBodyFat(profile.sex, age);
		txtBodyFat.setText(bodyFat);

		String musde = CommUtils.getLeanMusde(profile.sex, age);
		txtLeanMusde.setText(musde);

		txtBodyAge.setText(String.valueOf(age));

		String visceralFat = CommUtils.getVisceralFat(age);
		txtVisceralFat.setText(visceralFat);
		String bmi = "";
		String bmr = "";
		if (profile.unit != null && profile.unit.equals(Constants.IMPERIAL)) {
			txtUnit.setText(getString(R.string.unit_standard_weight));
			profile.height = profile.height * 12;
			bmi = CommUtils.getLBBMI(profile.weight, profile.height);
			bmr = CommUtils.getLBBMR(profile.sex, profile.weight,
					profile.height, age);
		} else {
			txtUnit.setText(getString(R.string.standard_weight));
			bmi = CommUtils.getKGBMI(profile.weight, profile.height);
			bmr = CommUtils.getKGBMR(profile.sex, profile.weight,
					profile.height, age);
		}

		txtBMI.setText(bmi);
		txtBMR.setText(bmr);

		double kcal = Integer.valueOf(bmr) * 1.2;

		txtKcal.setText(CommUtils.getString(kcal));

	}

	private void initView() {
		txtStandardWeight = (TextView) findViewById(R.id.txt_standard_weight);
		txtBodyFat = (TextView) findViewById(R.id.txt_body_fat);
		txtLeanMusde = (TextView) findViewById(R.id.txt_lean_musde);
		txtBodyAge = (TextView) findViewById(R.id.txt_body_age);
		txtVisceralFat = (TextView) findViewById(R.id.txt_visceral_fat);
		txtBMI = (TextView) findViewById(R.id.txt_bmi);
		txtBMR = (TextView) findViewById(R.id.txt_bmr);
		txtKcal = (TextView) findViewById(R.id.txt_kcal);
		txtUnit = (TextView) findViewById(R.id.txt_unit);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
