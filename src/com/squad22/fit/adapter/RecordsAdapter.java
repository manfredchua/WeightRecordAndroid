package com.squad22.fit.adapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.activity.EditExerciseActivity;
import com.squad22.fit.activity.EditMealActivity;
import com.squad22.fit.activity.EditMeasurementActivity;
import com.squad22.fit.dao.BowelCountDao;
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.dao.SleepDao;
import com.squad22.fit.dao.WaterCountDao;
import com.squad22.fit.entity.BowelCount;
import com.squad22.fit.entity.Exercise;
import com.squad22.fit.entity.Meal;
import com.squad22.fit.entity.Measurement;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.entity.Records;
import com.squad22.fit.entity.Sleep;
import com.squad22.fit.entity.WaterCount;
import com.squad22.fit.task.SyncDeleteBowelTask;
import com.squad22.fit.task.SyncDeleteExerciseTask;
import com.squad22.fit.task.SyncDeleteMealTask;
import com.squad22.fit.task.SyncDeleteMeasurementTask;
import com.squad22.fit.task.SyncDeleteSleepTask;
import com.squad22.fit.task.SyncDeleteWaterTask;
import com.squad22.fit.utils.ClockView;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.DensityUtil;

public class RecordsAdapter extends BaseAdapter {

	Context mContext;
	ArrayList<Records> records;

	public RecordsAdapter(Context mContext, ArrayList<Records> records) {
		this.mContext = mContext;
		this.records = records;
	}

	@Override
	public int getCount() {
		return records.size();
	}

	@Override
	public Object getItem(int position) {
		return records.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View view;

		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.record_calendar_item_layout, null);
		} else {
			view = convertView;
		}
		LinearLayout llClock = (LinearLayout) view.findViewById(R.id.ll_clock);
		TextView txtTitle = (TextView) view.findViewById(R.id.txt_title);
		final TextView txtDel = (TextView) view.findViewById(R.id.txt_del);
		ImageView ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
		ImageView ivType = (ImageView) view.findViewById(R.id.iv_type);

		final Records entity = records.get(position);

		if (entity.type == 4 || entity.type == 5 || entity.type == 6) {
			txtTitle.setTextColor(Color.GRAY);
			ivPhoto.setVisibility(View.GONE);
			if (entity.type == 4) {
				ivType.setImageResource(R.drawable.moon_grey);
			} else if (entity.type == 5 ) {
				ivType.setImageResource(R.drawable.mug_grey);
			} else if (entity.type == 6){
				ivType.setImageResource(R.drawable.toilet_roll_grey);
			}
		}else{
			txtTitle.setTextColor(Color.BLACK);
		}

		if (entity.type == 1) {
			if (entity.isPhoto) {
				ivPhoto.setVisibility(View.VISIBLE);
				ivPhoto.setImageResource(R.drawable.photo);
				ivType.setImageResource(R.drawable.bowl_grey);
			} else {
				ivPhoto.setVisibility(View.GONE);
				ivType.setImageResource(R.drawable.bowl_grey);
			}
		} else if (entity.type == 2) {
			if (entity.isPhoto) {
				ivPhoto.setVisibility(View.VISIBLE);
				ivPhoto.setImageResource(R.drawable.photo);
				ivType.setImageResource(R.drawable.dumbbell_grey);
			} else {
				ivPhoto.setVisibility(View.GONE);
				ivType.setImageResource(R.drawable.dumbbell_grey);
			}
		} else if (entity.type == 3) {
			if (entity.isPhoto) {
				ivPhoto.setVisibility(View.VISIBLE);
				ivPhoto.setImageResource(R.drawable.photo);
				ivType.setImageResource(R.drawable.scale_grey);
			} else {
				ivPhoto.setVisibility(View.GONE);
				ivType.setImageResource(R.drawable.scale_grey);
			}
		}

		ClockView clockView = new ClockView(mContext, entity.hourStr,
				entity.minuteStr, entity.height);
		llClock.addView(clockView);

		txtTitle.setText(entity.title);

		txtDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPupoWindow(entity, position, txtDel);
			}
		});

		return view;
	}

	@SuppressWarnings("deprecation")
	private void showPupoWindow(final Records entity, int position,
			TextView txtDel) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.action_provider, null);
		ListView listView = (ListView) view.findViewById(R.id.lvAction);

		listView.setCacheColorHint(Color.TRANSPARENT);
		PhotoAdapter adapter;
		if (entity.type == 1 || entity.type == 2 || entity.type == 3) {
			adapter = new PhotoAdapter(mContext, new String[] { "删除", "编辑" });
		} else {
			adapter = new PhotoAdapter(mContext, new String[] { "删除" });
		}

		listView.setAdapter(adapter);

		final PopupWindow actionAddPw = new PopupWindow(view,
				DensityUtil.dip2px(mContext, 120), DensityUtil.dip2px(mContext,
						100), true);
		actionAddPw.setBackgroundDrawable(new BitmapDrawable());
		actionAddPw.showAsDropDown(txtDel, 0, 0);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					showDelete(entity, position);
					break;
				case 1:
					// 1代表饮食,2代表运动,3代表测量
					switch (entity.type) {
					case 1:
						Meal meal = MyMealDao.getInstance().getMealById(mContext,
								entity.recordId);
						Intent intent = new Intent(mContext,
								EditMealActivity.class);
						intent.putExtra(Constants.MEAL, meal);
						mContext.startActivity(intent);
						break;
					case 2:
						Exercise exercise = MyActivityDao.getInstance()
								.getActivityById(mContext, entity.recordId);
						intent = new Intent(mContext,
								EditExerciseActivity.class);
						intent.putExtra(Constants.ACTIVITY, exercise);
						mContext.startActivity(intent);
						break;
					case 3:
						Measurement measurement = MeasurementDao.getInstance()
								.getMeasurementById(mContext, entity.recordId);
						intent = new Intent(mContext,
								EditMeasurementActivity.class);
						intent.putExtra(Constants.MEASUREMENT, measurement);
						mContext.startActivity(intent);
						break;

					default:
						break;
					}
					break;
				default:
					break;
				}
				if (actionAddPw != null)
					actionAddPw.dismiss();
			}
		});
	}

	private void showDelete(final Records entity, final int position) {
		new AlertDialog.Builder(mContext)
				.setMessage("确定删除此记录")
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setPositiveButton(R.string.comfirm,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								int result = MyRecordsDao.getInstance()
										.delRecords(mContext, entity.recordId,
												String.valueOf(entity.type));
								if (result > 0) {
									// 1代表饮食,2代表运动,3代表测量,4代表睡眠,5代表喝水,6代表排泄
									switch (entity.type) {
									case 1:
										Meal meal = MyMealDao.getInstance()
												.getMealById(mContext,
														entity.recordId);
										MyMealDao.getInstance().delMeal(mContext,
												meal);
										if(CommUtils.isAutoSync(mContext)){
											new SyncDeleteMealTask(mContext, meal).execute();
										}
										break;
									case 2:
										Exercise exercise = MyActivityDao
												.getInstance().getActivityById(
														mContext,
														entity.recordId);
										MyActivityDao
												.getInstance()
												.delActivity(mContext, exercise);
										if(CommUtils.isAutoSync(mContext)){
											new SyncDeleteExerciseTask(mContext, exercise).execute();
										}
										break;
									case 3:
										Measurement measurement = MeasurementDao
												.getInstance()
												.getMeasurementById(mContext,
														entity.recordId);
										MeasurementDao.getInstance()
												.delMeasurement(mContext,
														measurement);
										if(CommUtils.isAutoSync(mContext)){
											new SyncDeleteMeasurementTask(mContext, measurement).execute();
										}
										break;
									case 4:
										Sleep sleep = SleepDao.getInstance()
												.getSleepById(mContext,
														entity.recordId);
										SleepDao.getInstance().delSleep(
												mContext, sleep);
										
										if(CommUtils.isAutoSync(mContext)){
											new SyncDeleteSleepTask(mContext, sleep).execute();
										}
										break;
									case 5:
										WaterCount water = WaterCountDao
												.getInstance()
												.getWaterCountById(mContext,
														entity.recordId);
										WaterCountDao.getInstance().delWater(
												mContext, water);
										
										if(CommUtils.isAutoSync(mContext)){
											new SyncDeleteWaterTask(mContext, water).execute();
										}
										break;
									case 6:
										BowelCount bowel = BowelCountDao.getInstance().getBowelCountById(mContext, entity.recordId);
										BowelCountDao.getInstance().delBowel(
												mContext, bowel);
										if(CommUtils.isAutoSync(mContext)){
											new SyncDeleteBowelTask(mContext, bowel).execute();
										}
										break;

									default:
										break;
									}
									String userId = CommUtils.getUserId(mContext);
									ArrayList<MyRecords> arrayList = MyRecordsDao
											.getInstance().getAllRecordByDate(
													mContext,
													entity.currentDate, userId);
									if (arrayList != null
											&& arrayList.size() == 0) {
										MyRecordsDao.getInstance()
												.delSummaryRecords(mContext);
									}
									records.remove(entity);
									notifyDataSetChanged();
								}
							}
						}).show();
	}

}
