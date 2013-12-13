package com.squad22.fit.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.entity.Measurement;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.task.SyncDeleteMeasurementTask;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.ImagesUtils;

@SuppressLint("SimpleDateFormat")
public class MeasurementDetailActivity extends Activity {

	ActionBar actionBar;
	TextView txtHour;
	ImageView ivMeasurement1;
	ImageView ivMeasurement2;
	ImageView ivMeasurement3;
	HorizontalScrollView hsvPhoto;
	TextView etWeight;
	TextView txtWeight;
	TextView etBodyFat;
	TextView etVisceralFat;
	TextView etLeanMusde;
	TextView etBodyAge;
	TextView txtBMI;
	TextView txtBMR;
	TextView txtKcal;
	TextView etArm;
	TextView etAbdominal;
	TextView etWaist;
	TextView etHips;
	TextView etRightCalf;
	TextView etRightThigh;
	TextView etRemark;
	Measurement measurement;
	Date date;
	MeasurementDao dao = MeasurementDao.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat cnFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm a",
			Locale.CHINA);
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.record_measurement_detail_layout);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");

		initView();

		String recordId = getIntent().getStringExtra(Constants.RECORD_ID);
		measurement = dao.getMeasurementById(this, recordId);
		Measurement oldEntity = dao.getMeasurement(this,
				measurement.createDate, measurement.userId);
		setValue(measurement, oldEntity);

	}

	private void initView() {
		txtHour = (TextView) findViewById(R.id.txt_current_hour);
		etRemark = (TextView) findViewById(R.id.txt_remark);

		ivMeasurement1 = (ImageView) findViewById(R.id.iv_measurement1);
		ivMeasurement2 = (ImageView) findViewById(R.id.iv_measurement2);
		ivMeasurement3 = (ImageView) findViewById(R.id.iv_measurement3);
		hsvPhoto = (HorizontalScrollView) findViewById(R.id.hsv);

		txtWeight = (TextView) findViewById(R.id.txt_weight);
		etWeight = (TextView) findViewById(R.id.et_weight);
		etBodyFat = (TextView) findViewById(R.id.et_body_fat);
		etVisceralFat = (TextView) findViewById(R.id.et_visceral_fat);
		etLeanMusde = (TextView) findViewById(R.id.et_lean_musde);
		etBodyAge = (TextView) findViewById(R.id.et_body_age);
		txtBMI = (TextView) findViewById(R.id.et_bmi);
		txtBMR = (TextView) findViewById(R.id.et_bmr);
		txtKcal = (TextView) findViewById(R.id.et_new_kcal);
		etArm = (TextView) findViewById(R.id.et_arm);
		etAbdominal = (TextView) findViewById(R.id.et_abdominal);
		etWaist = (TextView) findViewById(R.id.et_waistline);
		etHips = (TextView) findViewById(R.id.et_hips);
		etRightThigh = (TextView) findViewById(R.id.et_right_thigh);
		etRightCalf = (TextView) findViewById(R.id.et_right_calf);
	}

	private void setValue(Measurement entity, Measurement oldEntity) {
		try {
			date = dateFormat.parse(entity.createDate);
			String createDate = cnFormat.format(date);
			txtHour.setText(createDate);
			String weightStr = "";
			if (oldEntity.weight != 0.0) {
				double weight = entity.weight - oldEntity.weight;
				weightStr = CommUtils.getString(weight);
			} else {
				weightStr = "0.0";
			}
			etWeight.setText(entity.weight + "(" + weightStr + ")");

			if (entity.image1 != null && entity.image1.length() > 0) {
				ivMeasurement1.setVisibility(View.VISIBLE);

				Bitmap bm = ImagesUtils.locDecodeImage(entity.image1);
				if (bm != null) {
					ivMeasurement1.setImageBitmap(bm);
				} else {
					ivMeasurement1.setVisibility(View.GONE);
				}
			} else {
				ivMeasurement1.setVisibility(View.GONE);
			}

			if (entity.image2 != null && entity.image2.length() > 0) {
				ivMeasurement2.setVisibility(View.VISIBLE);
				Bitmap bm = ImagesUtils.locDecodeImage(entity.image2);
				if (bm != null) {
					ivMeasurement2.setImageBitmap(bm);
				} else {
					ivMeasurement2.setVisibility(View.GONE);
				}
			} else {
				ivMeasurement2.setVisibility(View.GONE);
			}

			if (entity.image3 != null && entity.image3.length() > 0) {
				ivMeasurement3.setVisibility(View.VISIBLE);
				Bitmap bm = ImagesUtils.locDecodeImage(entity.image3);
				if (bm != null) {
					ivMeasurement3.setImageBitmap(bm);
				} else {
					ivMeasurement3.setVisibility(View.GONE);
				}
			} else {
				ivMeasurement3.setVisibility(View.GONE);
			}

			if (ivMeasurement1.getVisibility() == View.GONE
					&& ivMeasurement2.getVisibility() == View.GONE
					&& ivMeasurement3.getVisibility() == View.GONE) {
				hsvPhoto.setVisibility(View.GONE);
			} else {
				hsvPhoto.setVisibility(View.VISIBLE);
			}
			if (entity.remark != null && !entity.remark.equals("")) {
				etRemark.setText(entity.remark);
			} else {
				etRemark.setText("-");
			}

			if (entity.bodyFat == 0.0) {
				etBodyFat.setText("-");
			} else {
				String bodyFatStr = "";
				if (oldEntity.bodyFat != 0.0) {
					double bodyFat = entity.bodyFat - oldEntity.bodyFat;
					bodyFatStr = CommUtils.getString(bodyFat);
				} else {
					bodyFatStr = "0.0";
				}
				etBodyFat.setText(String.valueOf(entity.bodyFat) + "("
						+ bodyFatStr + ")");
			}

			if (entity.leanMusde == 0.0) {
				etLeanMusde.setText("-");
			} else {
				String leanMusdeStr = "";
				if (oldEntity.leanMusde != 0.0) {
					double leanMusde = entity.leanMusde - oldEntity.leanMusde;
					leanMusdeStr = CommUtils.getString(leanMusde);
				} else {
					leanMusdeStr = "0.0";
				}
				etLeanMusde.setText(String.valueOf(entity.leanMusde) + "("
						+ leanMusdeStr + ")");
			}

			if (entity.visceralFat == 0.0) {
				etVisceralFat.setText("-");
			} else {

				String visceralFatStr = "";
				if (oldEntity.visceralFat != 0.0) {
					double visceralFat = entity.visceralFat
							- oldEntity.visceralFat;
					visceralFatStr = CommUtils.getString(visceralFat);
				} else {
					visceralFatStr = "0.0";
				}

				etVisceralFat.setText(String.valueOf(entity.visceralFat) + "("
						+ visceralFatStr + ")");
			}

			if (entity.bodyAge == 0) {
				etBodyAge.setText("-");
			} else {
				String bodyAgeStr = "";
				if (oldEntity.bodyAge != 0.0) {
					double bodyAge = entity.bodyAge - oldEntity.bodyAge;
					bodyAgeStr = CommUtils.getString(bodyAge);
				} else {
					bodyAgeStr = "0.0";
				}

				String age = CommUtils.getString(entity.bodyAge);
				etBodyAge.setText(age + "(" + bodyAgeStr + ")");
			}

			if (entity.BMI == 0.0) {
				txtBMI.setText("-");
			} else {

				String BMIStr = "";
				if (oldEntity.BMI != 0.0) {
					double BMI = entity.BMI - oldEntity.BMI;
					BMIStr = CommUtils.getString(BMI);
				} else {
					BMIStr = "0.0";
				}

				txtBMI.setText(CommUtils.getDouble(entity.BMI) + "(" + BMIStr + ")");
			}

			if (entity.BMR == 0.0) {
				txtBMR.setText("-");
			} else {
				String BMRStr = "";
				if (oldEntity.BMR != 0.0) {
					double BMR = entity.BMR - oldEntity.BMR;
					BMRStr = CommUtils.getString(BMR);
				} else {
					BMRStr = "0.0";
				}

				txtBMR.setText(CommUtils.getDouble(entity.BMR) + "(" + BMRStr + ")");
			}

			if (entity.arm == 0.0) {
				etArm.setText("-");
			} else {
				String armStr = "";
				if (oldEntity.arm != 0.0) {
					double arm = entity.arm - oldEntity.arm;
					armStr = CommUtils.getString(arm);
				} else {
					armStr = "0.0";
				}

				etArm.setText(String.valueOf(entity.arm) + "(" + armStr + ")");
			}

			if (entity.waist == 0.0) {
				etWaist.setText("-");
			} else {
				String waistStr = "";
				if (oldEntity.waist != 0.0) {
					double waist = entity.waist - oldEntity.waist;
					waistStr = CommUtils.getString(waist);
				} else {
					waistStr = "0.0";
				}

				etWaist.setText(String.valueOf(entity.waist) + "(" + waistStr
						+ ")");
			}

			if (entity.hip == 0.0) {
				etHips.setText("-");
			} else {
				String hipStr = "";
				if (oldEntity.hip != 0.0) {
					double hip = entity.hip - oldEntity.hip;
					hipStr = CommUtils.getString(hip);
				} else {
					hipStr = "0.0";
				}

				etHips.setText(String.valueOf(entity.hip) + "(" + hipStr + ")");
			}

			if (entity.abd == 0.0) {
				etAbdominal.setText("-");
			} else {
				String abdStr = "";
				if (oldEntity.abd != 0.0) {
					double abd = entity.abd - oldEntity.abd;
					abdStr = CommUtils.getString(abd);
				} else {
					abdStr = "0.0";
				}

				etAbdominal.setText(String.valueOf(entity.abd) + "(" + abdStr
						+ ")");
			}

			if (entity.thigh == 0.0) {
				etRightThigh.setText("-");
			} else {
				String thighStr = "";
				if (oldEntity.thigh != 0.0) {
					double thigh = entity.thigh - oldEntity.thigh;
					thighStr = CommUtils.getString(thigh);
				} else {
					thighStr = "0.0";
				}

				etRightThigh.setText(String.valueOf(entity.thigh) + "("
						+ thighStr + ")");
			}

			if (entity.calf == 0.0) {
				etRightCalf.setText("-");
			} else {
				String calfStr = "";
				if (oldEntity.calf != 0.0) {
					double calf = entity.calf - oldEntity.calf;
					calfStr = CommUtils.getString(calf);
				} else {
					calfStr = "0.0";
				}

				etRightCalf.setText(String.valueOf(entity.calf) + "(" + calfStr
						+ ")");
			}

		} catch (Exception e) {
			Log.e("Exception", "--" + e.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_edit:
			Intent intent = new Intent(this, EditMeasurementActivity.class);
			intent.putExtra(Constants.MEASUREMENT, measurement);
			startActivity(intent);
			break;
		case R.id.action_del:
			int result = MeasurementDao.getInstance().delMeasurement(this,
					measurement);
			if (result > 0) {
				MyRecordsDao.getInstance()
						.delRecords(this, measurement.id, "3");
				String currentDate = dateSF.format(date);
				ArrayList<MyRecords> arrayList = MyRecordsDao.getInstance()
						.getAllRecordByDate(this, currentDate,
								measurement.userId);
				if (arrayList != null && arrayList.size() == 0) {
					MyRecordsDao.getInstance().delSummaryRecords(this);
				}

				// 判断是否自动同步
				if (CommUtils.isAutoSync(this)) {
					new SyncDeleteMeasurementTask(
							MeasurementDetailActivity.this, measurement)
							.execute();
				}
				CommUtils.showToast(this, "删除成功");
				intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				this.finish();
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
