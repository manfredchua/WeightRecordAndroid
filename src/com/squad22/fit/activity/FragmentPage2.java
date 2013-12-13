package com.squad22.fit.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
public class FragmentPage2 extends Fragment implements OnClickListener {
	ListView lvRecords;
	TextView txtCurrentDay;
	TextView txtNotData;
	ImageView ivNextDay;
	ImageView ivPrevious;
	LinearLayout llPrevious;
	LinearLayout llNext;
	LinearLayout llTodaySummary;
	Calendar mCalendar;
	String currentDate;
	SimpleDateFormat sf = new SimpleDateFormat("MM月dd日");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat createSf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	MyRecordsDao recordsDao = MyRecordsDao.getInstance();
	ArrayList<MyRecords> arrayList;
	ArrayList<Records> records;
	Date currentDay = new Date();
	Date date;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("FragmentPage2.onCreateView()");
		View view = inflater.inflate(R.layout.feed_record_calendar_layout, null);

		date = new Date();

		mCalendar = Calendar.getInstance();
		mCalendar.setTime(date);
		currentDate = sf.format(date);
		String week = CommUtils.getWeek(currentDate);

		initView(view);
		
		txtCurrentDay.setText(week + ", " + currentDate);
		
		new RecordListTask(date).execute();
		

		return view;
	}

	private void initView(View view) {
		lvRecords = (ListView) view.findViewById(R.id.lv_records);
		txtCurrentDay = (TextView) view.findViewById(R.id.txt_current_day);
		ivNextDay = (ImageView) view.findViewById(R.id.iv_next_day);
		ivPrevious = (ImageView) view.findViewById(R.id.iv_previous_day);
		llPrevious = (LinearLayout) view.findViewById(R.id.ll_previous);
		llNext = (LinearLayout) view.findViewById(R.id.ll_next);
		llTodaySummary = (LinearLayout) view.findViewById(R.id.ll_summary);
		txtNotData = (TextView) view.findViewById(R.id.txt_not_data);
		
		CommUtils.setFontFamily(getActivity(), txtCurrentDay);
		txtCurrentDay.setTextColor(Color.RED);
		
		llTodaySummary.setOnClickListener(this);
		ivNextDay.setOnClickListener(this);
		ivPrevious.setOnClickListener(this);
		llPrevious.setOnClickListener(this);
		llNext.setOnClickListener(this);

		txtNotData.setTextColor(Color.argb(20, 0, 0, 0));  //文字透明度  
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
			Intent intent = new Intent(getActivity(), MealDetailActivity.class);
			intent.putExtra(Constants.RECORD_ID, myRecord.recordId);
			startActivity(intent);
			break;
		case 2:
			intent = new Intent(getActivity(), ExerciseDetailActivity.class);
			intent.putExtra(Constants.RECORD_ID, myRecord.recordId);
			startActivity(intent);
			break;
		case 3:
			intent = new Intent(getActivity(), MeasurementDetailActivity.class);
			intent.putExtra(Constants.RECORD_ID, myRecord.recordId);
			startActivity(intent);
			break;
		default:
			break;
		}

	}
	
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_next:
		case R.id.iv_next_day:
			nextDay();
			break;
		case R.id.ll_previous:
		case R.id.iv_previous_day:
			previousDay();
			break;
		case R.id.ll_summary:
			Intent intent = new Intent(getActivity(), SummaryDetailActivity.class);
			intent.putExtra(Constants.CURRENT_DATE, mCalendar.getTime());
			getActivity().startActivity(intent);
			break;
		}
	}
	
	
	/**
	 * 当前日期增加一天
	 */
	private void nextDay() {
		if (mCalendar.getTime().getTime() < date.getTime()) {
			ivNextDay.setImageResource(R.drawable.arrow_right);
			mCalendar.add(Calendar.DAY_OF_MONTH, 1);
			currentDate = sf.format(mCalendar.getTime());
			String weekDate = dateFormat.format(mCalendar.getTime());
			String week = CommUtils.getWeek(weekDate);
			txtCurrentDay.setText(week + ", " + currentDate);

			new RecordListTask(mCalendar.getTime()).execute();
		} else {
			ivNextDay.setImageResource(R.drawable.arrow_right_unselected);
		}
	}

	/**
	 * 当前日期减去一天
	 */
	private void previousDay() {

		mCalendar.add(Calendar.DAY_OF_MONTH, -1);
		currentDate = sf.format(mCalendar.getTime());
		String weekDate = dateFormat.format(mCalendar.getTime());
		String week = CommUtils.getWeek(weekDate);
		txtCurrentDay.setText(week + ", " + currentDate);
		new RecordListTask(mCalendar.getTime()).execute();
		ivNextDay.setImageResource(R.drawable.arrow_right);

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
				records.height = CommUtils.adjust(getActivity().getWindowManager());
				records.type = myRecord.type;
				try {
					switch (myRecord.type) {
					case 1:
						Meal meal = MyMealDao.getInstance().getMealById(getActivity(),
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
								.getInstance().getActivityById(getActivity(),
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
								.getMeasurementById(getActivity(), myRecord.recordId);
						date = createSf.parse(measurement.createDate);
						records.hourStr = date.getHours();
						records.minuteStr = date.getMinutes();
						records.title = getString(R.string.measure_title);
						if((measurement.image1 != null && measurement.image1.length() > 0)
								|| (measurement.image2 != null && measurement.image2.length() > 0)
								||(measurement.image3 != null && measurement.image3.length() > 0)){
							records.isPhoto = true;
						}

						break;
					case 4:
						Sleep sleep = SleepDao.getInstance().getSleepById(getActivity(),
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
							timeStr = timeMinute + getString(R.string.minute);
						} else {
							timeStr = timeHour + getString(R.string.hour);
						}
						records.title = getString(R.string.sleep_desc) + timeStr;
						break;
					case 5:
						WaterCount water = WaterCountDao.getInstance()
								.getWaterCountById(getActivity(), myRecord.recordId);
						date = createSf.parse(water.waterDate);
						records.hourStr = date.getHours();
						records.minuteStr = date.getMinutes();
						records.title = getString(R.string.drink_water);

						break;
					case 6:
						BowelCount bowel = BowelCountDao.getInstance()
								.getBowelCountById(getActivity(), myRecord.recordId);
						date = createSf.parse(bowel.bowelDate);
						records.hourStr = date.getHours();
						records.minuteStr = date.getMinutes();
						records.title = getString(R.string.excrete);
						break;

					}
				} catch (Exception e) {

				}
				recordList.add(records);
			}
		}
		return recordList;
	}
	
	private class RecordListTask extends AsyncTask<Void, Void, Void>{
		Date date;
		public RecordListTask(Date date){
			this.date = date;
		}
		@Override
		protected Void doInBackground(Void... params) {
			String createDate = dateFormat.format(date);
			String userId = CommUtils.getUserId(getActivity());
			arrayList = recordsDao.getAllRecordByDate(getActivity(),
					createDate, userId);
			
			records = setRecords(arrayList, createDate);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			if(records != null && records.size() > 0){
				lvRecords.setVisibility(View.VISIBLE);
				txtNotData.setVisibility(View.GONE);
				RecordsAdapter adapter = new RecordsAdapter(getActivity(), records);
				lvRecords.setAdapter(adapter);
			}else{
				lvRecords.setVisibility(View.GONE);
				txtNotData.setVisibility(View.VISIBLE);
				txtNotData.setText(getActivity().getResources().getString(R.string.not_record));
			}
		}
		
	}
}
