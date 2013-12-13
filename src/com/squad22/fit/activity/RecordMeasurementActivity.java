package com.squad22.fit.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.HeightPickerDialog;
import com.squad22.fit.utils.ImagesUtils;

@SuppressLint("SimpleDateFormat")
public class RecordMeasurementActivity extends Activity implements
		OnClickListener {

	private static final String TAG = "RecordMeasurementActivity";
	ActionBar actionBar;
	ImageView ivMeasurement1;
	ImageView ivMeasurement2;
	ImageView ivMeasurement3;
	ImageView ivCamera1;
	ImageView ivCamera2;
	ImageView ivCamera3;
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
	int index;
	String imgbm;
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
		String userName = sp.getString(Constants.USERNAME, "");
		profile = ProfileDao.getInstance().getProfile(this, userName);
		initView();
	}

	private void initView() {
		ivMeasurement1 = (ImageView) findViewById(R.id.iv_measurement1);
		ivMeasurement2 = (ImageView) findViewById(R.id.iv_measurement2);
		ivMeasurement3 = (ImageView) findViewById(R.id.iv_measurement3);
		ivCamera1 = (ImageView) findViewById(R.id.iv_camera1);
		ivCamera2 = (ImageView) findViewById(R.id.iv_camera2);
		ivCamera3 = (ImageView) findViewById(R.id.iv_camera3);

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

		ivCamera1.setOnClickListener(this);
		ivCamera2.setOnClickListener(this);
		ivCamera3.setOnClickListener(this);

		ivMeasurement1.setOnClickListener(this);
		ivMeasurement2.setOnClickListener(this);
		ivMeasurement3.setOnClickListener(this);

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

		measurement.userId = CommUtils.getUserId(this);

		Date date = new Date();
		measurement.createDate = sf.format(date);
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
			int random = (int) (Math.random() * 10000);
			measurement.measurementId = "w" + random + "s";

			if (MeasurementDao.getInstance().insert(this, measurement)) {
				measurement.id = MeasurementDao.getInstance().getMeasurementId(
						this, measurement.measurementId, measurement.userId);
				MyRecords myRecords = new MyRecords();
				myRecords.createDate = measurement.createDate;
				myRecords.recordId = measurement.id;
				myRecords.type = 3;
				myRecords.userId = measurement.userId;

				MyRecordsDao.getInstance().insert(this, myRecords);

				// 判断是否自动同步
				if (CommUtils.isAutoSync(this)) {
					new SyncMeasurementTask(RecordMeasurementActivity.this,
							measurement).execute();
				}

				String createDate = dateSF.format(date);
				if (MyRecordsDao.getInstance().getRecord(this, createDate,
						measurement.userId)) {
					MyRecords records = new MyRecords();
					records.createDate = createDate;
					records.recordId = "";
					records.type = 7;
					records.userId = measurement.userId;
					MyRecordsDao.getInstance().insert(this, records);
				}

				CommUtils.showToast(RecordMeasurementActivity.this, "测量记录保存成功");

				Intent intent = new Intent(RecordMeasurementActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				CommUtils.showToast(this, "新测量记录失败");
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_measurement1:
		case R.id.iv_camera1:
			index = 1;
			CommUtils.showPhoto(this);
			break;
		case R.id.iv_measurement2:
		case R.id.iv_camera2:
			index = 2;
			CommUtils.showPhoto(this);
			break;
		case R.id.iv_measurement3:
		case R.id.iv_camera3:
			index = 3;
			CommUtils.showPhoto(this);
			break;
		default:
			break;
		}
	}

	public void showWeightDialog() {
		String nowNumber = "0.0";
		String number = etWeight.getText().toString();

		if (!number.equals("")) {
			number = number.substring(0, number.length() - 2);
			nowNumber = number;
		}
		String unit = "";
		if (profile.unit != null && profile.unit.equals(Constants.IMPERIAL)) {
			unit = "磅";
		} else {
			unit = "公斤";
		}
		DialogFragment newFragment = new HeightPickerDialog(this,
				new HeightPickerDialog.OnMyNumberSetListener() {
					@Override
					public void onNumberSet(String number, String mode) {
						etWeight.setText("");
						etWeight.setText(number + mode);

					}
				}, nowNumber, unit);
		newFragment.show(getFragmentManager(), "dialog");

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.GALLERY:
			if (data != null) {
				Uri uri = data.getData();
				imgbm = uri.toString();

				Log.d(TAG, "uri: " + uri.toString() + ",Media.uri: "
						+ Media.EXTERNAL_CONTENT_URI.toString());
				String columns[] = new String[] { Media.DATA };
				Cursor cursor = getContentResolver().query(uri, columns, null,
						null, null);
				while (cursor.moveToNext()) {
					// 图库中图片路径
					imgbm = cursor.getString(cursor.getColumnIndex(Media.DATA));
				}

				if (cursor != null) {
					cursor.close();
				}

				if (imgbm != null) {
					Bitmap photo = ImagesUtils.decodeImage(imgbm);
					if (photo != null) {
						byte[] photos = ImagesUtils.convertImageToByte(photo);
						String path = imgbm
								.substring(imgbm.lastIndexOf("/") + 1);
						File localFile = ImagesUtils.getFileFromBytes(photos,
								Constants.DOWNLOAD_PHOTO_PATH + "" + path);

						switch (index) {
						case 1:
							measurement.image1 = localFile.getAbsolutePath();

							ivCamera1.setVisibility(View.GONE);

							ivMeasurement1.setImageBitmap(photo);
							break;

						case 2:
							measurement.image2 = localFile.getAbsolutePath();

							ivCamera2.setVisibility(View.GONE);

							ivMeasurement2.setImageBitmap(photo);
							break;
						case 3:
							measurement.image3 = localFile.getAbsolutePath();

							ivCamera3.setVisibility(View.GONE);

							ivMeasurement3.setImageBitmap(photo);
							break;
						default:
							break;
						}
						
					}
				}
			}
			break;

		case Constants.TAKE_A_PICTURE:
			// 图库中图片路径
			imgbm = CommUtils.mCurrentPhotoFile.getAbsolutePath();
			if (imgbm != null) {
				Bitmap photo = ImagesUtils.decodeImage(imgbm);
				if (photo != null) {
					byte[] photos = ImagesUtils.convertImageToByte(photo);
					String path = imgbm.substring(imgbm.lastIndexOf("/") + 1);
					File localFile = ImagesUtils.getFileFromBytes(photos,
							Constants.DOWNLOAD_PHOTO_PATH + "" + path);

					switch (index) {
					case 1:
						measurement.image1 = localFile.getAbsolutePath();

						ivCamera1.setVisibility(View.GONE);

						ivMeasurement1.setImageBitmap(photo);
						break;

					case 2:
						measurement.image2 = localFile.getAbsolutePath();

						ivCamera2.setVisibility(View.GONE);

						ivMeasurement2.setImageBitmap(photo);
						break;
					case 3:
						measurement.image3 = localFile.getAbsolutePath();

						ivCamera3.setVisibility(View.GONE);

						ivMeasurement3.setImageBitmap(photo);
						break;
					default:
						break;
					}
				}
			}
			break;

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
						CommUtils
								.showAlertDialog(RecordMeasurementActivity.this);
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
