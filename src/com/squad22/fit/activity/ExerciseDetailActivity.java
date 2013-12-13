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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.dao.ActivityDetailDao;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.entity.Exercise;
import com.squad22.fit.entity.ExerciseDetail;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.task.SyncDeleteExerciseTask;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.ImagesUtils;

@SuppressLint("SimpleDateFormat")
public class ExerciseDetailActivity extends Activity {

	TextView txtHour;
	TextView txtTitle;
	ImageView ivExercisePhoto;
	TextView txtTimeCount;
	TextView txtKcalCount;
	LinearLayout llActivityDetail;
	ActionBar actionBar;
	String recordId;
	Exercise exercise;
	int timeCount;
	int kcalCount;
	Date date;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat cnFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm a",
			Locale.CHINA);
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_exercise_detail_layout);
		WeightRecordApplication.getInstance().addActivity(this);
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setTitle("");
		actionBar.setDisplayHomeAsUpEnabled(true);

		recordId = getIntent().getStringExtra(Constants.RECORD_ID);

		initView();

		exercise = MyActivityDao.getInstance().getActivityById(this, recordId);

		setValue(exercise);
	}

	private void initView() {
		txtHour = (TextView) findViewById(R.id.txt_current_hour);
		txtTitle = (TextView) findViewById(R.id.txt_title);
		ivExercisePhoto = (ImageView) findViewById(R.id.iv_exercise_photo);
		llActivityDetail = (LinearLayout) findViewById(R.id.ll_activity_detail);
		txtTimeCount = (TextView) findViewById(R.id.txt_time_count);
		txtKcalCount = (TextView) findViewById(R.id.txt_kcal_count);
	}

	private void setValue(Exercise exercise) {
		try {
			date = dateFormat.parse(exercise.createDate);
			String createDate = cnFormat.format(date);
			txtHour.setText(createDate);
			txtTitle.setText(exercise.title);

			if (exercise.image != null && exercise.image.length() > 0) {
				ivExercisePhoto.setVisibility(View.VISIBLE);
				Bitmap bm = ImagesUtils.locDecodeImage(exercise.image);
				
				if(bm != null){
					ivExercisePhoto.setImageBitmap(bm);
				}else{
					ivExercisePhoto.setVisibility(View.GONE);
				}
				
			} else {
				ivExercisePhoto.setVisibility(View.GONE);
			}

			ArrayList<ExerciseDetail> arrayList = ActivityDetailDao
					.getInstance().getActivityDetailById(this, exercise.id);
			for (ExerciseDetail detail : arrayList) {

				View mealDetail = getLayoutInflater().inflate(
						R.layout.exercise_detail_layout, null);
				TextView txtActivity = (TextView) mealDetail
						.findViewById(R.id.txt_activity);
				TextView txtKcal = (TextView) mealDetail
						.findViewById(R.id.txt_kcal);

				txtActivity.setText(detail.name);
				txtKcal.setText(detail.calorie + "卡路里");
				llActivityDetail.addView(mealDetail);

				kcalCount += Integer.valueOf(detail.calorie);
				timeCount += Integer.valueOf(detail.time);
			}

			txtTimeCount.setText(String.valueOf(timeCount));
			txtKcalCount.setText(String.valueOf(kcalCount));
		} catch (Exception e) {

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
			this.finish();
			break;
		case R.id.action_edit:
			Intent intent = new Intent(this, EditExerciseActivity.class);
			intent.putExtra(Constants.ACTIVITY, exercise);
			startActivity(intent);
			break;
		case R.id.action_del:
			int result = MyActivityDao.getInstance()
					.delActivity(this, exercise);
			if (result > 0) {
				MyRecordsDao.getInstance().delRecords(this, exercise.id, "2");
				String currentDate = dateSF.format(date);
				ArrayList<MyRecords> arrayList = MyRecordsDao.getInstance()
						.getAllRecordByDate(this, currentDate, exercise.userId);
				if (arrayList != null && arrayList.size() == 0) {
					MyRecordsDao.getInstance().delSummaryRecords(this);
				}

				if (CommUtils.isAutoSync(this)) {
					new SyncDeleteExerciseTask(ExerciseDetailActivity.this,
							exercise).execute();
				}

				CommUtils.showToast(ExerciseDetailActivity.this, "删除成功");
				intent = new Intent(ExerciseDetailActivity.this,
						MainActivity.class);
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
