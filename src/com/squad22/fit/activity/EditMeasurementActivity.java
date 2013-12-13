package com.squad22.fit.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.dao.ProfileDao;
import com.squad22.fit.entity.Measurement;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.entity.Profile;
import com.squad22.fit.task.SyncMeasurementTask;
import com.squad22.fit.task.SyncUpdateMeasurementTask;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

@SuppressLint("SimpleDateFormat")
public class EditMeasurementActivity extends Activity {

	ActionBar actionBar;
	ImageView ivHidePhoto1;
	ImageView ivHidePhoto2;
	ImageView ivHidePhoto3;
	EditText etWeight;
	TextView txtWeight;
	EditText etBodyFat;
	EditText etVisceralFat;
	EditText etLeanMusde;
	EditText etBodyAge;
	TextView txtBMI;
	TextView txtBMR;
	TextView txtKcal;
	EditText etArm;
	EditText etAbdominal;
	EditText etWaist;
	EditText etHips;
	EditText etRightCalf;
	EditText etRightThigh;
	EditText etRemark;
	LinearLayout llContent;
	HorizontalScrollView hsvPhoto;
	int index;
	String imgbm;
	String userName;
	Measurement measurement = new Measurement();
	Profile profile;
	ProgressDialog proDialog;
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setActionBar();

		setContentView(R.layout.record_measurement_layout);

		SharedPreferences sp = getSharedPreferences(Constants.KIIUSER,
				MODE_PRIVATE);
		userName = sp.getString(Constants.USERNAME, "");
		profile = ProfileDao.getInstance().getProfile(this, userName);
		measurement = (Measurement) getIntent().getSerializableExtra(
				Constants.MEASUREMENT);

		initView();
		setValue();
	}

	private void initView() {
		ivHidePhoto1 = (ImageView) findViewById(R.id.iv_hide_photo1);
		ivHidePhoto2 = (ImageView) findViewById(R.id.iv_hide_photo2);
		ivHidePhoto3 = (ImageView) findViewById(R.id.iv_hide_photo3);

		txtWeight = (TextView) findViewById(R.id.txt_weight);
		etWeight = (EditText) findViewById(R.id.et_weight);
		etBodyFat = (EditText) findViewById(R.id.et_body_fat);
		etVisceralFat = (EditText) findViewById(R.id.et_visceral_fat);
		etLeanMusde = (EditText) findViewById(R.id.et_lean_musde);
		etBodyAge = (EditText) findViewById(R.id.et_body_age);
		txtBMI = (TextView) findViewById(R.id.et_bmi);
		txtBMR = (TextView) findViewById(R.id.et_bmr);
		txtKcal = (TextView) findViewById(R.id.et_new_kcal);
		etArm = (EditText) findViewById(R.id.et_arm);
		etAbdominal = (EditText) findViewById(R.id.et_abdominal);
		etWaist = (EditText) findViewById(R.id.et_waistline);
		etHips = (EditText) findViewById(R.id.et_hips);
		etRightThigh = (EditText) findViewById(R.id.et_right_thigh);
		etRightCalf = (EditText) findViewById(R.id.et_right_calf);

		etRemark = (EditText) findViewById(R.id.et_remark);
		llContent = (LinearLayout) findViewById(R.id.ll_content);
		hsvPhoto = (HorizontalScrollView) findViewById(R.id.hsv);

		ivHidePhoto1.setVisibility(View.VISIBLE);
		ivHidePhoto2.setVisibility(View.VISIBLE);
		ivHidePhoto3.setVisibility(View.VISIBLE);

		hsvPhoto.setVerticalScrollBarEnabled(false);
		hsvPhoto.setHorizontalScrollBarEnabled(false);
		hsvPhoto.setOnTouchListener(new onTouch());

		String unit = "";
		if (profile.unit != null && profile.unit.equals(Constants.IMPERIAL)) {
			unit = "磅";
		} else {
			unit = "公斤";
		}
		txtWeight.setText(getString(R.string.weight) + "(" + unit + ")");

		etWeight.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String text = s.toString();
				if (!text.equals("") && text != null) {
					llContent.setVisibility(View.VISIBLE);
					String bmi = "";
					String bmr = "";
					double weight = Double.parseDouble(text);
					int age = CommUtils.getAge(profile.birthday);
					if (profile.unit != null
							&& profile.unit.equals(Constants.IMPERIAL)) {
						profile.height = profile.height * 12;
						bmi = CommUtils.getLBBMI(weight, profile.height);
						bmr = CommUtils.getLBBMR(profile.sex, weight,
								profile.height, age);
					} else {
						bmi = CommUtils.getKGBMI(weight, profile.height);
						bmr = CommUtils.getKGBMR(profile.sex, weight,
								profile.height, age);
					}

					txtBMI.setText(bmi);
					txtBMR.setText(bmr);

					double kcal = Integer.valueOf(bmr) * 1.2;
					txtKcal.setText(CommUtils.getString(kcal));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void setValue() {

		etWeight.setText(String.valueOf(measurement.weight));
		etRemark.setText(measurement.remark);

		etBodyFat.setText(String.valueOf(measurement.bodyFat));
		etLeanMusde.setText(String.valueOf(measurement.leanMusde));
		etVisceralFat.setText(CommUtils.getString(measurement.visceralFat));
		etBodyAge.setText(CommUtils.getString(measurement.bodyAge));
		txtBMI.setText(String.valueOf(measurement.BMI));
		txtBMR.setText(String.valueOf(measurement.BMR));

		etArm.setText(String.valueOf(measurement.arm));
		etWaist.setText(String.valueOf(measurement.waist));
		etHips.setText(String.valueOf(measurement.hip));
		etAbdominal.setText(String.valueOf(measurement.abd));
		etRightThigh.setText(String.valueOf(measurement.thigh));
		etRightCalf.setText(String.valueOf(measurement.calf));
	}

	private void getValue() {
		String weight = etWeight.getText().toString().trim();
		String bodyFat = etBodyFat.getText().toString().trim();
		String visceralFat = etVisceralFat.getText().toString().trim();
		String leanMusde = etLeanMusde.getText().toString().trim();
		String bodyAge = etBodyAge.getText().toString().trim();
		String arm = etArm.getText().toString().trim();
		String waist = etWaist.getText().toString().trim();
		String hip = etHips.getText().toString().trim();
		String calf = etRightCalf.getText().toString().trim();
		String thigh = etRightThigh.getText().toString().trim();
		String abdominal = etAbdominal.getText().toString().trim();
		String bmi = txtBMI.getText().toString().trim();
		String bmr = txtBMR.getText().toString().trim();
		measurement.remark = etRemark.getText().toString().trim();

		if (bodyFat != null && !bodyFat.equals("")) {
			measurement.bodyFat = Double.parseDouble(bodyFat);
		}
		if (visceralFat != null && !visceralFat.equals("")) {
			measurement.visceralFat = Double.parseDouble(visceralFat);
		}

		if (leanMusde != null && !leanMusde.equals("")) {
			measurement.leanMusde = Double.parseDouble(leanMusde);
		}

		if (bodyAge != null && !bodyAge.equals("")) {
			measurement.bodyAge = Double.parseDouble(bodyAge);
		}

		if (arm != null && !arm.equals("")) {
			measurement.arm = Double.parseDouble(arm);
		}

		if (waist != null && !waist.equals("")) {
			measurement.waist = Double.parseDouble(waist);
		}

		if (hip != null && !hip.equals("")) {
			measurement.hip = Double.parseDouble(hip);
		}

		if (calf != null && !calf.equals("")) {
			measurement.calf = Double.parseDouble(calf);
		}

		if (thigh != null && !thigh.equals("")) {
			measurement.thigh = Double.parseDouble(thigh);
		}

		if (abdominal != null && !abdominal.equals("")) {
			measurement.abd = Double.parseDouble(abdominal);
		}

		if (bmi != null && !bmi.equals("")) {
			measurement.BMI = Double.parseDouble(bmi);
		}

		if (bmr != null && !bmr.equals("")) {
			measurement.BMR = Double.parseDouble(bmr);
		}

		if (weight.equals("") || weight == null) {
			etWeight.setError(getString(R.string.weight_error));
			etWeight.requestFocus();
		} else {
			measurement.weight = Double.parseDouble(weight);

			if (measurement.syncId == 1) {
				measurement.syncId = 2;
			}

			int result = MeasurementDao.getInstance().updateMeasurement(this,
					measurement);
			if (result > 0) {

				// 判断是否自动同步
				SharedPreferences sp = getSharedPreferences(
						Constants.BACKUP_RECORDS, MODE_PRIVATE);
				boolean isRecord = sp.getBoolean(Constants.IS_RECORD, false);
				if (isRecord) {
					if (CommUtils.checkNetwork(this)) {
						String network = sp.getString(Constants.WIFI, "wifi");
						if (network.equals("wifi")) {
							if (CommUtils.isWIFIAvailable(this)) {

								if (measurement.syncId == 0) {
									new SyncMeasurementTask(
											EditMeasurementActivity.this,
											measurement).execute();
								} else {
									new SyncUpdateMeasurementTask(
											EditMeasurementActivity.this,
											measurement).execute();
								}

							}
						} else {
							if (CommUtils.isWIFIAvailable(this)
									|| CommUtils.is3GAvailable(this)) {
								if (measurement.syncId == 0) {
									new SyncMeasurementTask(
											EditMeasurementActivity.this,
											measurement).execute();
								} else {
									new SyncUpdateMeasurementTask(
											EditMeasurementActivity.this,
											measurement).execute();
								}

							}
						}
					}
				}

				try {
					Date currentDate = sf.parse(measurement.createDate);
					String createDate = dateSF.format(currentDate);
					if (MyRecordsDao.getInstance().getRecord(this, createDate,
							measurement.userId)) {
						MyRecords records = new MyRecords();
						records.createDate = createDate;
						records.recordId = "";
						records.type = 7;
						MyRecordsDao.getInstance().insert(this, records);
					}
				} catch (Exception e) {
				}

				CommUtils.showToast(this, "新测量记录成功");
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				this.finish();
			} else {
				CommUtils.showToast(this, "测量记录修改失败");
			}
		}
	}

	class onTouch implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return true;
		}

	}

	private void setActionBar() {
		final LayoutInflater inflater = (LayoutInflater) getActionBar()
				.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = inflater.inflate(
				R.layout.actionbar_custom_view_done_cancel, null);
		customActionBarView.findViewById(R.id.actionbar_done)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getValue();
					}
				});
		customActionBarView.findViewById(R.id.actionbar_cancel)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CommUtils.showAlertDialog(EditMeasurementActivity.this);
					}
				});

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView,
				new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
	}

}
