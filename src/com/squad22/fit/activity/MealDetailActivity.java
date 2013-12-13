package com.squad22.fit.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.dao.MealDetailDao;
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.entity.Meal;
import com.squad22.fit.entity.MealDetail;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.task.SyncDeleteMealTask;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.ImagesUtils;

@SuppressLint("SimpleDateFormat")
public class MealDetailActivity extends Activity {

	TextView txtHour;
	TextView txtTitle;
	ImageView ivFood;
	TextView txtMeatCount;
	TextView txtNoMeatCount;
	TextView txtVegetableCount;
	TextView txtStaple;
	LinearLayout llMealDetail;
	ActionBar actionBar;
	Meal meal;
	String recordId;
	double meatCount;
	double noMeatCount;
	double vegetableCount;
	double staple;
	double kcalCount;
	Date date;
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat enFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a",
			Locale.US);
	SimpleDateFormat cnFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm a",
			Locale.CHINA);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.record_meal_detail_layout);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setTitle("");
		actionBar.setDisplayHomeAsUpEnabled(true);

		recordId = getIntent().getStringExtra(Constants.RECORD_ID);
		meal = MyMealDao.getInstance().getMealById(this, recordId);

		initView();

		setValue(meal);
	}

	private void initView() {
		txtHour = (TextView) findViewById(R.id.txt_current_hour);
		txtTitle = (TextView) findViewById(R.id.txt_title);
		ivFood = (ImageView) findViewById(R.id.iv_food_photo);
		llMealDetail = (LinearLayout) findViewById(R.id.ll_meal_detail);
		txtMeatCount = (TextView) findViewById(R.id.txt_meat_count);
		txtNoMeatCount = (TextView) findViewById(R.id.txt_no_meat_count);
		txtVegetableCount = (TextView) findViewById(R.id.txt_vegetable_count);
		txtStaple = (TextView) findViewById(R.id.txt_staple);
	}

	private void setValue(Meal meal) {
		try {
			date = dateFormat.parse(meal.createDate);
			String createDate = cnFormat.format(date);
			txtHour.setText(createDate);
			txtTitle.setText(meal.title);

			if (meal.image != null && meal.image.length() > 0) {
				ivFood.setVisibility(View.VISIBLE);
				
				Bitmap bm = ImagesUtils.locDecodeImage(meal.image);
				if(bm != null){
					ivFood.setImageBitmap(bm);
				}else{
					ivFood.setVisibility(View.GONE);
				}
			} else {
				ivFood.setVisibility(View.GONE);
			}

			ArrayList<MealDetail> arrayList = MealDetailDao.getInstance()
					.getMealDetailById(this, meal.id);
			if (arrayList != null && arrayList.size() > 0) {
				for (MealDetail detail : arrayList) {

					View mealDetail = getLayoutInflater().inflate(
							R.layout.meal_detail_layout, null);
					TextView txtFood = (TextView) mealDetail
							.findViewById(R.id.txt_food);
					TextView txtKcal = (TextView) mealDetail
							.findViewById(R.id.txt_kcal);

					txtFood.setText(detail.Amount + detail.unit + detail.name
							+ "-" + detail.portion);
					if(detail.calorie.equals("")){
						txtKcal.setText("-卡路里");
					}else{
						txtKcal.setText(detail.calorie + "卡路里");
					}
					llMealDetail.addView(mealDetail);

					try {
						kcalCount += Integer.valueOf(detail.calorie);
					} catch (Exception e) {
					}
					String portionStr = detail.portion.substring(0,
							detail.portion.length() - 2);
					double portion = Double.parseDouble(portionStr);

					if (detail.Category.equals("主食-五谷类")) {
						staple += portion;
					} else if (detail.Category.equals("肉类蛋白质")) {
						meatCount += portion;
					} else if (detail.Category.equals("蔬菜水果类")) {
						vegetableCount += portion;
					} else if(detail.Category.equals("非肉类蛋白质")){
						noMeatCount += portion;
					}
				}

				txtStaple.setText(String.valueOf(staple));
				txtMeatCount.setText(String.valueOf(meatCount));
				txtNoMeatCount.setText(String.valueOf(noMeatCount));
				txtVegetableCount.setText(String.valueOf(vegetableCount));
				
				ImageView ivLine = new ImageView(this);
				ivLine.setImageResource(R.drawable.divider_greyline);
				ivLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				llMealDetail.addView(ivLine);

				View kcal = getLayoutInflater().inflate(
						R.layout.meal_detail_layout, null);

				TextView txtFood = (TextView) kcal.findViewById(R.id.txt_food);
				TextView txtKcal = (TextView) kcal.findViewById(R.id.txt_kcal);

				txtFood.setText("总共");
				txtFood.setTextSize(16);
				txtFood.setTextColor(Color.BLACK);

				txtKcal.setText(CommUtils.getString(kcalCount) + "卡路里");
				txtKcal.setTextSize(16);
				txtKcal.setTextColor(Color.BLACK);

				llMealDetail.addView(kcal);
			}
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
			Intent intent = new Intent(this, EditMealActivity.class);
			intent.putExtra(Constants.MEAL, meal);
			startActivity(intent);
			break;
		case R.id.action_del:
			int result = MyMealDao.getInstance().delMeal(this, meal);
			if (result > 0) {
				MyRecordsDao.getInstance().delRecords(this, meal.id, "1");
				String currentDate = dateSF.format(date);
				ArrayList<MyRecords> arrayList = MyRecordsDao.getInstance()
						.getAllRecordByDate(this, currentDate, meal.userId);
				if (arrayList != null && arrayList.size() == 0) {
					MyRecordsDao.getInstance().delSummaryRecords(this);
				}
				SharedPreferences sp = getSharedPreferences(
						Constants.BACKUP_RECORDS, MODE_PRIVATE);
				boolean isRecord = sp.getBoolean(Constants.IS_RECORD, false);
				if (isRecord) {
					if (CommUtils.checkNetwork(this)) {
						String network = sp.getString(Constants.WIFI, "wifi");
						if (network.equals("wifi")) {
							if (CommUtils
									.isWIFIAvailable(MealDetailActivity.this)) {
								new SyncDeleteMealTask(MealDetailActivity.this,meal).execute();
							}
						} else {
							if (CommUtils
									.isWIFIAvailable(MealDetailActivity.this)
									|| CommUtils
											.is3GAvailable(MealDetailActivity.this)) {
								new SyncDeleteMealTask(MealDetailActivity.this, meal).execute();
							}
						}

					}
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
