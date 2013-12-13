package com.squad22.fit.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squad22.fit.R;
import com.squad22.fit.adapter.RecordsAdapter;
import com.squad22.fit.dao.BowelCountDao;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.dao.SleepDao;
import com.squad22.fit.dao.WaterCountDao;
import com.squad22.fit.entity.BowelCount;
import com.squad22.fit.entity.Meal;
import com.squad22.fit.entity.Measurement;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.entity.Records;
import com.squad22.fit.entity.Sleep;
import com.squad22.fit.entity.WaterCount;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

@SuppressLint("SimpleDateFormat")
public class FeedRecordCalendar extends Activity {

	ActionBar actionBar;
	ListView lvRecords;
	Calendar mCalendar;
	String currentDate;
	SimpleDateFormat sf = new SimpleDateFormat("MM月dd日");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat createSf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat dailycountSf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	ArrayList<MyRecords> arrayList;
	ArrayList<Records> records;
	Date currentDay = new Date();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_record_calendar_layout);
		WeightRecordApplication.getInstance().addActivity(this);
		Date date = (Date) getIntent().getSerializableExtra(
				Constants.CURRENT_DATE);

		mCalendar = Calendar.getInstance();
		mCalendar.setTime(date);
		currentDate = sf.format(date);
		String week = CommUtils.getWeek(currentDate);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(week + ", " + currentDate);

		initView();
		recordList(date);
	}

	private void initView() {
		lvRecords = (ListView) findViewById(R.id.lv_records);

		lvRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startActivity(records.get(position));
			}

		});
	}

	private void startActivity(Records myRecord) {
		switch (myRecord.type) {
		case 1:
			Intent intent = new Intent(this, MealDetailActivity.class);
			intent.putExtra(Constants.RECORD_ID, myRecord.recordId);
			startActivity(intent);
			break;
		case 2:
			intent = new Intent(this, ExerciseDetailActivity.class);
			intent.putExtra(Constants.RECORD_ID, myRecord.recordId);
			startActivity(intent);
			break;
		case 3:
			intent = new Intent(this, MeasurementDetailActivity.class);
			intent.putExtra(Constants.RECORD_ID, myRecord.recordId);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			this.finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void recordList(Date currentDate) {
		String createDate = dateFormat.format(currentDate);
		String userId = CommUtils.getUserId(this);
		arrayList = MyRecordsDao.getInstance().getAllRecordByDate(this,
				createDate, userId);
		
		records = setRecords(arrayList, createDate);
		RecordsAdapter adapter = new RecordsAdapter(this, records);
		lvRecords.setAdapter(adapter);
	}

	@SuppressWarnings("deprecation")
	private ArrayList<Records> setRecords(ArrayList<MyRecords> arrayList,
			String currentDate) {
		ArrayList<Records> recordList = new ArrayList<Records>();
		if (arrayList != null && arrayList.size() > 0) {
			for (MyRecords myRecord : arrayList) {
				Records records = new Records();
				records.currentDate = currentDate;
				records.recordId = myRecord.recordId;
				records.height = CommUtils.adjust(getWindowManager());
				records.type = myRecord.type;
				try {
					switch (myRecord.type) {
					case 1:
						Meal meal = MyMealDao.getInstance().getMealById(this,
								myRecord.recordId);
						Date date = createSf.parse(meal.createDate);
						records.hourStr = date.getHours();
						records.minuteStr = date.getMinutes();
						records.title = meal.title;
						if(meal.image != null && meal.image.length() > 0){
							records.isPhoto = true;
						}

						break;
					case 2:
						com.squad22.fit.entity.Exercise activity = MyActivityDao
								.getInstance().getActivityById(this,
										myRecord.recordId);
						date = createSf.parse(activity.createDate);
						records.hourStr = date.getHours();
						records.minuteStr = date.getMinutes();
						records.title = activity.title;
						if(activity.image != null && activity.image.length() > 0){
							records.isPhoto = true;
						}

						break;
					case 3:
						Measurement measurement = MeasurementDao.getInstance()
								.getMeasurementById(this, myRecord.recordId);
						date = createSf.parse(measurement.createDate);
						records.hourStr = date.getHours();
						records.minuteStr = date.getMinutes();
						records.title = "记录体重或其他测量";
						if((measurement.image1 != null && measurement.image1.length() > 0)
								|| (measurement.image2 != null && measurement.image2.length() > 0)
								||(measurement.image3 != null && measurement.image3.length() > 0)){
							records.isPhoto = true;
						}

						break;
					case 4:
						Sleep sleep = SleepDao.getInstance().getSleepById(this,
								myRecord.recordId);
						date = createSf.parse(sleep.createDate);
						records.hourStr = date.getHours();
						records.minuteStr = date.getMinutes();
						Date startDate = createSf.parse(sleep.sleepStart);
						Date endDate = createSf.parse(sleep.sleepEnd);
						long time = endDate.getTime() - startDate.getTime();
						long timeHour = time / 1000 / 60 / 60;
						
						String timeStr = "";
						if (timeHour == 0) {
							long timeMinute = time / 1000 / 60;
							timeStr = timeMinute + "分钟";
						} else {
							timeStr = timeHour + "小时";
						}
						records.title = "你已睡眠" + timeStr;
						break;
					case 5:
						WaterCount water = WaterCountDao.getInstance()
								.getWaterCountById(this, myRecord.recordId);
						date = dailycountSf.parse(water.waterDate);
						records.hourStr = date.getHours();
						records.minuteStr = date.getMinutes();
						records.title = "喝了一杯水";

						break;
					case 6:
						BowelCount bowel = BowelCountDao.getInstance()
								.getBowelCountById(this, myRecord.recordId);
						date = dailycountSf.parse(bowel.bowelDate);
						records.hourStr = date.getHours();
						records.minuteStr = date.getMinutes();
						records.title = "排泄一次";
						break;

					}
				} catch (Exception e) {

				}
				recordList.add(records);
			}
		}
		return recordList;
	}
}
