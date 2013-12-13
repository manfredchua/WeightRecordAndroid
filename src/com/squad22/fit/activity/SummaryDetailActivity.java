package com.squad22.fit.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.dao.ActivityDetailDao;
import com.squad22.fit.dao.BowelCountDao;
import com.squad22.fit.dao.MealDetailDao;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.dao.SleepDao;
import com.squad22.fit.dao.SummaryDao;
import com.squad22.fit.dao.WaterCountDao;
import com.squad22.fit.entity.BowelCount;
import com.squad22.fit.entity.Exercise;
import com.squad22.fit.entity.ExerciseDetail;
import com.squad22.fit.entity.Meal;
import com.squad22.fit.entity.MealDetail;
import com.squad22.fit.entity.Sleep;
import com.squad22.fit.entity.WaterCount;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.XmlPullParserMessage;

@SuppressLint("SimpleDateFormat")
public class SummaryDetailActivity extends Activity {

	ActionBar actionBar;
	TextView txtMonth;
	TextView txtDay;
	TextView txtTitle;
	TextView txtFoodCount;
	TextView txtFoodKcalCount;
	TextView txtActivityCount;
	TextView txtActivityKcalCount;
	TextView txtWaterCount;
	TextView txtBowelCount;
	TextView txtSleepCount;
	String userId;
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat createSf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.summary_detail_layout);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("今日总结");

		Date date = (Date) getIntent().getSerializableExtra(
				Constants.CURRENT_DATE);

		initView();
		userId = CommUtils.getUserId(this);

		setValue(date);
		
		
		
	}

	private void initView() {
		txtMonth = (TextView) findViewById(R.id.txt_month);
		txtDay = (TextView) findViewById(R.id.txt_day);
		txtTitle = (TextView) findViewById(R.id.txt_title);
		txtFoodCount = (TextView) findViewById(R.id.txt_food_count);
		txtFoodKcalCount = (TextView) findViewById(R.id.txt_food_kcal_count);
		txtActivityCount = (TextView) findViewById(R.id.txt_activity_count);
		txtActivityKcalCount = (TextView) findViewById(R.id.txt_activity_kcal_count);
		txtWaterCount = (TextView) findViewById(R.id.txt_water_count);
		txtBowelCount = (TextView) findViewById(R.id.txt_bowel_count);
		txtSleepCount = (TextView) findViewById(R.id.txt_sleep_count);
	}

	@SuppressWarnings("deprecation")
	private void setValue(Date date) {
		String str = Locale.getDefault().getLanguage();
		String currentDate = "";
		if (str.equals("cn") || str.equals("zh")) {
			currentDate = (date.getMonth() + 1) + "月";
		} else {
			currentDate = showMonth((date.getMonth() + 1));
		}

		txtMonth.setText(currentDate);
		txtDay.setText(String.valueOf(date.getDate()));

		int random = (int) (Math.random() * 209);

		String quote = SummaryDao.getInstance().getQuote(this,
				String.valueOf(random));
		txtTitle.setText(quote);

		currentDate = dateSF.format(date);
		ArrayList<Meal> mealList = MyMealDao.getInstance().getAllMealByDate(
				SummaryDetailActivity.this, currentDate, userId);
		txtFoodCount.setText(String.valueOf(mealList.size()));

		int kcalCount = 0;
		for (Meal meal : mealList) {
			ArrayList<MealDetail> arrayList = MealDetailDao.getInstance()
					.getMealDetailById(this, meal.id);
			for (MealDetail detail : arrayList) {
				if (detail.calorie != null && !detail.calorie.equals("")) {
					kcalCount += Integer.valueOf(detail.calorie);
				}
			}
		}

		txtFoodKcalCount.setText(String.valueOf(kcalCount));

		ArrayList<Exercise> exerciseList = MyActivityDao.getInstance()
				.getAllActivityByDate(SummaryDetailActivity.this, currentDate, userId);

		int activityKcalCount = 0;
		int time = 0;

		for (Exercise exercise : exerciseList) {
			ArrayList<ExerciseDetail> arrayList = ActivityDetailDao
					.getInstance().getActivityDetailById(this, exercise.id);
			for (ExerciseDetail detail : arrayList) {
				time += Integer.valueOf(detail.time);
				activityKcalCount += Integer.valueOf(detail.calorie);
			}
		}
		txtActivityCount.setText(String.valueOf(time));
		txtActivityKcalCount.setText(String.valueOf(activityKcalCount));

		ArrayList<WaterCount> waterList = WaterCountDao.getInstance()
				.getWaterCountByDate(SummaryDetailActivity.this, currentDate, userId);
		txtWaterCount.setText(String.valueOf(waterList.size()));

		ArrayList<BowelCount> bowelList = BowelCountDao.getInstance()
				.getBowelCountByDate(SummaryDetailActivity.this, currentDate, userId);
		txtBowelCount.setText(String.valueOf(bowelList.size()));

		ArrayList<Sleep> sleepList = SleepDao.getInstance().getSleepByDate(
				SummaryDetailActivity.this, currentDate, userId);
		long sleepTime = 0;

		for (Sleep sleep : sleepList) {
			try {
				Date startDate = createSf.parse(sleep.sleepStart);
				Date endDate = createSf.parse(sleep.sleepEnd);
				sleepTime += endDate.getTime() - startDate.getTime();

			} catch (Exception e) {
			}
		}
		long timeHour = sleepTime / 1000 / 60 / 60;

		String timeStr = "";

		if (timeHour == 0) {
			long timeMinute = sleepTime / 1000 / 60;
			if (timeMinute < 10) {
				timeStr = "00:0" + timeMinute;
			} else {
				timeStr = "00:" + timeMinute;
			}
		} else {
			if (timeHour < 10) {
				timeStr = "0" + timeHour + ":00";
			} else {
				timeStr = timeHour + ":00";
			}

		}

		txtSleepCount.setText(timeStr);
	}

	private String showMonth(int curr_month) {
		String month = null;
		ArrayList<HashMap<String, String>> array = XmlPullParserMessage
				.getYearsXmlResource(this);
		for (HashMap<String, String> hashMap : array) {
			Set<String> key = hashMap.keySet();
			for (String str : key) {
				if (str.equals(String.valueOf(curr_month))) {
					return hashMap.get(str);
				}
			}
		}
		return month;
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
}
