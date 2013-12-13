package com.squad22.fit.task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiACL;
import com.kii.cloud.storage.KiiACL.FileAction;
import com.kii.cloud.storage.KiiACLEntry;
import com.kii.cloud.storage.KiiAnonymousUser;
import com.kii.cloud.storage.KiiFile;
import com.kii.cloud.storage.KiiFileBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.query.KiiClause;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squad22.fit.dao.ActivityDao;
import com.squad22.fit.dao.ActivityDetailDao;
import com.squad22.fit.dao.BowelCountDao;
import com.squad22.fit.dao.FoodDao;
import com.squad22.fit.dao.MealDetailDao;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.dao.ProfileDao;
import com.squad22.fit.dao.SleepDao;
import com.squad22.fit.dao.WaterCountDao;
import com.squad22.fit.entity.ActivityEntity;
import com.squad22.fit.entity.BowelCount;
import com.squad22.fit.entity.Exercise;
import com.squad22.fit.entity.ExerciseDetail;
import com.squad22.fit.entity.Food;
import com.squad22.fit.entity.Meal;
import com.squad22.fit.entity.MealDetail;
import com.squad22.fit.entity.Measurement;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.entity.Profile;
import com.squad22.fit.entity.Sleep;
import com.squad22.fit.entity.WaterCount;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.FileCache;
import com.squad22.fit.utils.ImagesUtils;

@SuppressLint("SimpleDateFormat")
public class SyncTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = null;
	Context context;
	Handler mHandler;
	boolean isAutoSync;
	ProfileDao dao = ProfileDao.getInstance();
	MyActivityDao activityDao = MyActivityDao.getInstance();
	MyMealDao mealDao = MyMealDao.getInstance();
	ActivityDetailDao activityDetailDao = ActivityDetailDao.getInstance();
	MealDetailDao mealDetailDao = MealDetailDao.getInstance();
	MeasurementDao measurementDao = MeasurementDao.getInstance();
	SleepDao sleepDao = SleepDao.getInstance();
	WaterCountDao waterDao = WaterCountDao.getInstance();
	BowelCountDao bowelDao = BowelCountDao.getInstance();
	Profile profile;
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public SyncTask(Context context, Handler mHandler, boolean isAutoSync) {
		this.context = context;
		this.mHandler = mHandler;
		this.isAutoSync = isAutoSync;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {

			KiiUser currentUser = KiiUser.getCurrentUser();
			profile = dao.getProfile(context, currentUser.getUsername());
			// 下载服务器数据
			KiiQuery query = new KiiQuery();

			// 获取食物记录
			KiiQueryResult<KiiObject> result = currentUser.bucket(
					Constants.MyMeal).query(query);
			List<KiiObject> objList = result.getResult();
			ArrayList<Meal> syncMealList = new ArrayList<Meal>();
			if (objList != null && objList.size() > 0) {
				for (KiiObject kiiObject : objList) {
					// kiiObject.delete();
					Meal meal = new Meal();
					meal.userId = profile.id;
					meal.title = kiiObject.getString(Constants.mealTitle);
					meal.createDate = kiiObject
							.getString(Constants.mealRecordDate);
					meal.mealId = kiiObject.getString(Constants.mealID);
					try {
						meal.remark = kiiObject.getString(Constants.mealRemark);
					} catch (Exception e2) {
					}
					try {
						meal.comment = kiiObject
								.getString(Constants.mealComment);
					} catch (Exception e2) {
					}
					try {
						meal.like = kiiObject.getInt(Constants.mealLike);
					} catch (Exception e2) {
					}
					try {
						meal.syncId = kiiObject.getInt(Constants.mealSyncID);
					} catch (Exception e1) {
						meal.syncId = 1;
					}
					try {
						String image = kiiObject.getString(Constants.mealImage);
						String bitmap = ImagesUtils.decodeImageURl(image);
						if (bitmap != null) {
							meal.image = bitmap;
						}
					} catch (Exception e1) {
					}

					try {
						String food = kiiObject.getString(Constants.mealFood);
						Log.i(TAG, "--" + food);

						meal.mealDetail = new ArrayList<MealDetail>();

						JSONArray array = new JSONArray(food);
						for (int i = 0; i < array.length(); i++) {
							MealDetail detail = new MealDetail();
							JSONObject jsonObj = array.getJSONObject(i);

							detail.foodId = jsonObj.getString(Constants.FOOD_ID);
							double qty = jsonObj.getDouble(Constants.QTY);
							detail.Amount = CommUtils.getDouble(qty);
							detail.unit = jsonObj.getString(Constants.FOOD_UNIT);

							try {
								double portion = jsonObj.getDouble(Constants.PORTION);
								detail.portion = CommUtils.getDouble(portion)
										+ jsonObj.getString(Constants.PORTION_UNIT);
							} catch (Exception e) {
							}
							detail.Category = jsonObj.getString(Constants.CATEGORY);
							detail.calorie = jsonObj.getString(Constants.KCAL);
							Food entity = FoodDao.getInstance().getFoodById(
									context, detail.foodId);
							detail.name = entity.name;
							meal.mealDetail.add(detail);
						}
					} catch (Exception e) {
					}
					syncMealList.add(meal);
				}
			} else {
				ArrayList<Meal> locaMealList = mealDao.getSyncDelMeal(context,
						profile.id);
				for (Meal delMeal : locaMealList) {
					delMeal.syncId = 0;
					int count = mealDao.delMeal(context, delMeal);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								delMeal.id, "1");
						Date date = dateFormat.parse(delMeal.createDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}

					}
				}
			}

			ArrayList<Meal> syncDelMealList = new ArrayList<Meal>();

			ArrayList<Meal> locaDelMealList = mealDao.getAllMeal(context,
					profile.id, "3");
			for (Meal meal : syncMealList) {
				syncDelMealList.add(meal);
				// 判断是否有同步并且修改过的数据
				if (locaDelMealList != null && locaDelMealList.size() > 0) {
					for (Meal delMeal : locaDelMealList) {
						// 如果数据不相同，进行添加。否则忽略
						if (delMeal.mealId.equals(meal.mealId)) {
							syncDelMealList.remove(meal);
						}
					}
				}
			}

			ArrayList<Meal> addMealList = new ArrayList<Meal>();
			ArrayList<Meal> locaMealList = mealDao.getAllMeal(context,
					profile.id, "1");

			// 判断是否有同步的数据
			for (Meal meal : syncDelMealList) {
				addMealList.add(meal);
				// 判断本地数据是否有同步的
				if (locaMealList != null && locaMealList.size() > 0) {
					for (Meal locaMeal : locaMealList) {
						// 有相同数据，进行修改
						if (locaMeal.mealId.equals(meal.mealId)) {
							addMealList.remove(meal);
							meal.id = locaMeal.id;
							meal.syncId = 1;
							int rowId = mealDao.updateMeal(context, meal);
							if (rowId > 0) {
								try {
									mealDetailDao.delMealDetail(context, meal.id);
									for (MealDetail detail : meal.mealDetail) {
										detail.recordId = meal.id;
										mealDetailDao.insert(context, detail);
									}
								} catch (Exception e) {
								}

								MyRecords info = new MyRecords();
								info.createDate = meal.createDate;
								info.recordId = meal.id;
								info.type = 1;

								MyRecordsDao.getInstance().updateRecords(
										context, info);

								Date date = dateFormat.parse(meal.createDate);
								String dateStr = dateSF.format(date);
								if (MyRecordsDao.getInstance().getRecord(
										context, dateStr, meal.userId)) {
									MyRecords records = new MyRecords();
									records.createDate = dateStr;
									records.recordId = "";
									records.type = 7;
									records.userId = meal.userId;
									MyRecordsDao.getInstance().insert(context,
											records);
								}
							}
						}
					}
				}
			}

			ArrayList<Meal> locUpdateMealList = mealDao.getAllMeal(context,
					profile.id, "2");

			for (Meal meal : addMealList) {
				// 判断本地数据是否有同步并且修改过数据的
				if (locUpdateMealList != null && locUpdateMealList.size() > 0) {
					for (Meal upateMeal : locUpdateMealList) {
						// 如果有相同数据，但是本地标识修改。进行忽略
						if (!upateMeal.mealId.equals(meal.mealId)) {
							addMeal(meal);
						}
					}
				} else {
					addMeal(meal);
				}
			}

			// 获取运动记录数据
			result = currentUser.bucket(Constants.MyActivity).query(query);
			objList = result.getResult();
			ArrayList<Exercise> syncExerciseList = new ArrayList<Exercise>();

			if (objList != null && objList.size() > 0) {
				for (KiiObject kiiObject : objList) {
					// kiiObject.delete();
					Exercise exercise = new Exercise();
					exercise.userId = profile.id;
					exercise.title = kiiObject
							.getString(Constants.activityTitle);

					exercise.createDate = kiiObject
							.getString(Constants.activityRecordDate);

					try {
						exercise.remark = kiiObject
								.getString(Constants.activityRemark);
					} catch (Exception e2) {
					}

					try {
						exercise.comment = kiiObject
								.getString(Constants.activityComment);
					} catch (Exception e2) {
					}

					try {
						exercise.like = kiiObject
								.getInt(Constants.activityLike);
					} catch (Exception e2) {
					}

					try {
						exercise.syncId = kiiObject
								.getInt(Constants.activitySyncID);
					} catch (Exception e1) {
					}

					exercise.activityId = kiiObject
							.getString(Constants.activityID);
					try {
						String image = kiiObject
								.getString(Constants.activityImage);
						String bitmap = ImagesUtils.decodeImageURl(image);
						if (bitmap != null) {
							exercise.image = bitmap;
						}
					} catch (Exception e1) {
					}

					try {
						String activity = kiiObject
								.getString(Constants.activityExercise);
						Log.i(TAG, "--" + activity);

						exercise.exerciseDetail = new ArrayList<ExerciseDetail>();
						JSONArray array = new JSONArray(activity);
						for (int i = 0; i < array.length(); i++) {
							ExerciseDetail detail = new ExerciseDetail();
							JSONObject jsonObj = array.getJSONObject(i);

							detail.activityId = jsonObj
									.getString(Constants.EXERCISE_ID);
							detail.time = jsonObj.getString(Constants.TIME);
							detail.unit = jsonObj
									.getString(Constants.FOOD_UNIT);
							detail.calorie = jsonObj
									.getString(Constants.CALORIE);
							ActivityEntity entity = ActivityDao
									.getInstance()
									.getExerciseById(context, detail.activityId);
							detail.name = entity.name;

							exercise.exerciseDetail.add(detail);
						}
					} catch (Exception e) {
					}
					syncExerciseList.add(exercise);
				}
			} else {
				ArrayList<Exercise> locaExerciseList = activityDao
						.getSyncDelActivity(context, profile.id);
				for (Exercise delExercise : locaExerciseList) {
					delExercise.syncId = 0;
					int count = activityDao.delActivity(context, delExercise);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								delExercise.id, "2");
						Date date = dateFormat.parse(delExercise.createDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}

					}
				}
			}

			ArrayList<Exercise> syncDelExerciseList = new ArrayList<Exercise>();
			ArrayList<Exercise> locaDelExerciseList = activityDao.getActivity(
					context, "3", profile.id);
			for (Exercise exercise : syncExerciseList) {
				syncDelExerciseList.add(exercise);
				// 判断是否有同步并且修改过的数据
				if (locaDelExerciseList != null
						&& locaDelExerciseList.size() > 0) {
					for (Exercise delExercise : locaDelExerciseList) {
						// 如果数据不相同，进行添加。否则忽略
						if (delExercise.activityId.equals(exercise.activityId)) {
							syncDelExerciseList.remove(exercise);
						}
					}
				}
			}

			ArrayList<Exercise> locaExerciseList = activityDao.getActivity(
					context, "1", profile.id);
			ArrayList<Exercise> addExerciseList = new ArrayList<Exercise>();

			for (Exercise exercise : syncDelExerciseList) {
				addExerciseList.add(exercise);
				// 判断本地数据是否有同步的
				if (locaExerciseList != null && locaExerciseList.size() > 0) {
					for (Exercise locaExercise : locaExerciseList) {
						// 判断数据是否相同，相同进行修改
						if (locaExercise.activityId.equals(exercise.activityId)) {
							addExerciseList.remove(exercise);
							exercise.id = locaExercise.id;
							int rowId = activityDao.updateActivity(context,
									exercise);
							if (rowId > 0) {
								activityDetailDao.delRecordActivity(context,
										exercise.id);
								for (ExerciseDetail detail : exercise.exerciseDetail) {
									detail.recordId = exercise.id;
									activityDetailDao.insert(context, detail);
								}

								MyRecords info = new MyRecords();
								info.createDate = exercise.createDate;
								info.recordId = exercise.id;
								info.type = 2;

								MyRecordsDao.getInstance().updateRecords(
										context, info);

								Date date = dateFormat
										.parse(exercise.createDate);
								String dateStr = dateSF.format(date);
								if (MyRecordsDao.getInstance().getRecord(
										context, dateStr, exercise.userId)) {
									MyRecords records = new MyRecords();
									records.createDate = dateStr;
									records.recordId = "";
									records.type = 7;
									records.userId = exercise.userId;
									MyRecordsDao.getInstance().insert(context,
											records);
								}
							}
						}
					}
				}
			}

			ArrayList<Exercise> locaUpdateExerciseList = activityDao
					.getActivity(context, "2", profile.id);
			for (Exercise exercise : addExerciseList) {
				// 判断是否有同步并且修改过的数据
				if (locaUpdateExerciseList != null
						&& locaUpdateExerciseList.size() > 0) {
					for (Exercise updateExercise : locaUpdateExerciseList) {
						// 如果数据不相同，进行添加。否则忽略
						if (!updateExercise.activityId
								.equals(exercise.activityId)) {
							addExercise(exercise);
						}
					}
				} else {
					// 进行添加
					addExercise(exercise);
				}
			}

			// 获取测量记录数据
			result = currentUser.bucket(Constants.MyMeasurement).query(query);
			objList = result.getResult();
			ArrayList<Measurement> syncMeasurementList = new ArrayList<Measurement>();
			if (objList != null && objList.size() > 0) {
				for (KiiObject kiiObject : objList) {
					// kiiObject.delete();
					Measurement measurement = new Measurement();
					measurement.userId = profile.id;
					try {
						measurement.weight = kiiObject
								.getDouble(Constants.measurementWeight);
					} catch (Exception e1) {
					}
					try {
						measurement.bodyFat = kiiObject
								.getDouble(Constants.measurementBodyFat);
					} catch (Exception e1) {
					}
					try {
						measurement.leanMusde = kiiObject
								.getDouble(Constants.measurementLeanMuscle);
					} catch (Exception e1) {
					}
					try {
						measurement.bodyAge = kiiObject
								.getDouble(Constants.measurementBodyAge);

					} catch (Exception e1) {
					}
					try {
						measurement.visceralFat = kiiObject
								.getDouble(Constants.measurementVisceralFat);
					} catch (Exception e1) {
					}
					try {
						measurement.BMI = kiiObject
								.getDouble(Constants.measurementBMI);
					} catch (Exception e1) {
					}
					try {
						measurement.BMR = kiiObject
								.getDouble(Constants.measurementBMR);
					} catch (Exception e1) {
					}
					try {
						measurement.arm = kiiObject
								.getDouble(Constants.measurementArm);
					} catch (Exception e1) {
					}
					try {
						measurement.waist = kiiObject
								.getDouble(Constants.measurementWaist);
					} catch (Exception e1) {
					}
					try {
						measurement.hip = kiiObject
								.getDouble(Constants.measurementHip);
					} catch (Exception e1) {
					}
					try {
						measurement.abd = kiiObject
								.getDouble(Constants.measurementAbd);
					} catch (Exception e1) {
					}
					try {
						measurement.thigh = kiiObject
								.getDouble(Constants.measurementThigh);
					} catch (Exception e1) {
					}
					try {
						measurement.calf = kiiObject
								.getDouble(Constants.measurementCalf);
					} catch (Exception e1) {
					}
					try {
						measurement.createDate = kiiObject
								.getString(Constants.measurementReportDate);
					} catch (Exception e1) {
					}
					try {
						measurement.remark = kiiObject
								.getString(Constants.measurementRemark);
					} catch (Exception e2) {
					}

					try {
						measurement.syncId = kiiObject
								.getInt(Constants.measurementSyncID);
					} catch (Exception e1) {
					}

					measurement.measurementId = kiiObject
							.getString(Constants.measurementID);
					try {
						String image = kiiObject
								.getString(Constants.measurementImage1);
						String bitmap = ImagesUtils.decodeImageURl(image);
						if (bitmap != null) {
							measurement.image1 = bitmap;
						}
					} catch (Exception e1) {
						Log.i(TAG, "--" + e1.getMessage());
					}

					try {
						String image = kiiObject
								.getString(Constants.measurementImage2);
						String bitmap = ImagesUtils.decodeImageURl(image);
						if (bitmap != null) {
							measurement.image2 = bitmap;
						}
					} catch (Exception e1) {
						Log.i(TAG, "--" + e1.getMessage());
					}

					try {
						String image = kiiObject
								.getString(Constants.measurementImage3);
						String bitmap = ImagesUtils.decodeImageURl(image);
						if (bitmap != null) {
							measurement.image3 = bitmap;
						}
					} catch (Exception e1) {
					}

					syncMeasurementList.add(measurement);
				}
			} else {
				ArrayList<Measurement> locaMeasurementList = measurementDao
						.getSyncDelMeasurement(context, profile.id);
				for (Measurement delMeasurement : locaMeasurementList) {
					delMeasurement.syncId = 0;
					int count = measurementDao.delMeasurement(context,
							delMeasurement);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								delMeasurement.id, "3");
						Date date = dateFormat.parse(delMeasurement.createDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}

					}
				}
			}

			ArrayList<Measurement> syncDelMeasurementList = new ArrayList<Measurement>();
			ArrayList<Measurement> locaDelMeasurementList = measurementDao
					.getMeasurementBySyncId(context, "3", profile.id);
			for (Measurement measurement : syncMeasurementList) {
				syncDelMeasurementList.add(measurement);
				if (locaDelMeasurementList != null
						&& locaDelMeasurementList.size() > 0) {
					for (Measurement delMeasurement : locaDelMeasurementList) {
						if (delMeasurement.measurementId
								.equals(measurement.measurementId)) {
							syncDelMeasurementList.remove(measurement);
						}
					}
				}
			}

			ArrayList<Measurement> addMeasurement = new ArrayList<Measurement>();
			ArrayList<Measurement> locaMeasurementList = measurementDao
					.getMeasurementBySyncId(context, "1", profile.id);
			for (Measurement measurement : syncDelMeasurementList) {
				addMeasurement.add(measurement);
				if (locaMeasurementList != null
						&& locaMeasurementList.size() > 0) {
					for (Measurement locaMeasurement : locaMeasurementList) {
						if (locaMeasurement.measurementId
								.equals(measurement.measurementId)) {
							addMeasurement.remove(measurement);
							measurement.id = locaMeasurement.id;
							int rowId = measurementDao.updateMeasurement(
									context, measurement);

							if (rowId > 0) {

								MyRecords info = new MyRecords();
								info.createDate = measurement.createDate;
								info.recordId = measurement.id;
								info.type = 3;

								MyRecordsDao.getInstance().updateRecords(
										context, info);

								Date date = dateFormat
										.parse(measurement.createDate);
								String dateStr = dateSF.format(date);
								if (MyRecordsDao.getInstance().getRecord(
										context, dateStr, measurement.userId)) {
									MyRecords records = new MyRecords();
									records.createDate = dateStr;
									records.recordId = "";
									records.type = 7;
									records.userId = measurement.userId;
									MyRecordsDao.getInstance().insert(context,
											records);
								}
							}
						}
					}
				}
			}

			ArrayList<Measurement> locaUpdateMeasurementList = measurementDao
					.getMeasurementBySyncId(context, "2", profile.id);
			for (Measurement measurement : addMeasurement) {
				if (locaUpdateMeasurementList != null
						&& locaUpdateMeasurementList.size() > 0) {
					for (Measurement updateMeasurement : locaUpdateMeasurementList) {
						if (!updateMeasurement.measurementId
								.equals(measurement.measurementId)) {
							addMeasurement(measurement);
						}
					}
				} else {
					addMeasurement(measurement);
				}
			}

			// 获取睡眠记录
			result = currentUser.bucket(Constants.MySleep).query(query);
			objList = result.getResult();
			ArrayList<Sleep> syncSleep = new ArrayList<Sleep>();
			if (objList != null && objList.size() > 0) {
				for (KiiObject kiiObject : objList) {
					// kiiObject.delete();
					Sleep sleep = new Sleep();
					sleep.userId = profile.id;
					sleep.sleepId = kiiObject.getString(Constants.sleepID);
					sleep.syncId = kiiObject.getInt(Constants.sleepSyncID);
					sleep.createDate = kiiObject
							.getString(Constants.sleepReportDate);
					sleep.sleepStart = kiiObject
							.getString(Constants.sleepStart);
					sleep.sleepEnd = kiiObject.getString(Constants.sleepEnd);

					syncSleep.add(sleep);
				}
			} else {
				ArrayList<Sleep> locaSleepList = sleepDao.getSyncDelSleep(
						context, profile.id);
				for (Sleep sleep : locaSleepList) {
					sleep.syncId = 0;
					int count = sleepDao.delSleep(context, sleep);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								sleep.id, "4");
						Date date = dateFormat.parse(sleep.createDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}
					}
				}
			}

			ArrayList<Sleep> syncDelSleep = new ArrayList<Sleep>();
			ArrayList<Sleep> locaDelSleepList = sleepDao.getSleepBySyncId(
					context, "3", profile.id);
			for (Sleep sleep : syncSleep) {
				syncDelSleep.add(sleep);
				if (locaDelSleepList != null && locaDelSleepList.size() > 0) {
					for (Sleep locaSleep : locaDelSleepList) {
						if (locaSleep.sleepId.equals(sleep.sleepId)) {
							syncDelSleep.remove(sleep);
						}
					}
				}
			}

			ArrayList<Sleep> addSleepList = new ArrayList<Sleep>();
			ArrayList<Sleep> locaSleepList = sleepDao.getSleepBySyncId(context,
					"1", profile.id);
			for (Sleep sleep : syncDelSleep) {
				addSleepList.add(sleep);
				if (locaSleepList != null && locaSleepList.size() > 0) {
					for (Sleep locaSleep : locaSleepList) {
						if (locaSleep.sleepId.equals(sleep.sleepId)) {
							addSleepList.remove(sleep);
						}
					}
				}
			}
			
			for (Sleep sleep : addSleepList) {
				addSleep(sleep);
			}

			// 获取喝水和排泄记录
			ArrayList<WaterCount> syncWater = new ArrayList<WaterCount>();
			ArrayList<BowelCount> syncBowel = new ArrayList<BowelCount>();

			result = currentUser.bucket(Constants.MyDailycount).query(query);
			objList = result.getResult();

			if (objList != null && objList.size() > 0) {
				for (KiiObject kiiObject : objList) {
//					 kiiObject.delete();
					String yearMonth = kiiObject
							.getString(Constants.dailycountRecordDate);
					String dailycountWater = kiiObject
							.getString(Constants.dailycountWater);
					String dailycountBowel = kiiObject
							.getString(Constants.dailycountBowel);

					try {
						JSONArray arrayWater = new JSONArray(dailycountWater);
						Log.i(TAG, "--" + arrayWater);
						for (int i = 0; i < arrayWater.length(); i++) {
							JSONObject json = arrayWater.getJSONObject(i);
							if (json != null && json.length() > 0) {
								WaterCount water = new WaterCount();
								water.userId = profile.id;
								water.yearMonth = yearMonth;
								try {
									water.waterDate = json
											.getString(Constants.waterCreateDate);
									water.syncId = Integer.valueOf(json
											.getString(Constants.waterSyncID));
								} catch (Exception e) {
									Log.e(TAG, "--" + e.getMessage());
								}
								syncWater.add(water);
							}
						}
					} catch (Exception e1) {
					}

					try {
						JSONArray arrayBowel = new JSONArray(dailycountBowel);
						Log.i(TAG, "--" + arrayBowel);
						for (int i = 0; i < arrayBowel.length(); i++) {
							JSONObject json = arrayBowel.getJSONObject(i);
							if (json != null && json.length() > 0) {
								BowelCount bowel = new BowelCount();
								bowel.userId = profile.id;
								bowel.yearMonth = yearMonth;
								try {
									bowel.bowelDate = json
											.getString(Constants.bowelCreateDate);
									bowel.syncId = Integer.valueOf(json
											.getString(Constants.bowelSyncID));
								} catch (Exception e) {
									Log.e(TAG, "--" + e.getMessage());
								}
								syncBowel.add(bowel);
							}
						}
					} catch (Exception e) {
					}
				}
			} else {
				ArrayList<WaterCount> locaWaterList = waterDao
						.getSyncDelWaterCount(context, profile.id);
				for (WaterCount waterCount : locaWaterList) {
					waterCount.syncId = 0;
					int count = waterDao.delWater(context, waterCount);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								waterCount.id, "5");
						Date date = dateFormat.parse(waterCount.waterDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}
					}
				}
				ArrayList<BowelCount> locaBowelList = bowelDao
						.getSyncDelBowelCount(context, profile.id);
				for (BowelCount bowelCount : locaBowelList) {
					bowelCount.syncId = 0;
					int count = bowelDao.delBowel(context, bowelCount);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								bowelCount.id, "6");
						Date date = dateFormat.parse(bowelCount.bowelDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}
					}
				}
			}

			ArrayList<WaterCount> syncDelWater = new ArrayList<WaterCount>();
			ArrayList<WaterCount> locaDelWaterList = waterDao
					.getWaterCountBySyncId(context, "3", profile.id);
			for (WaterCount waterCount : syncWater) {
				syncDelWater.add(waterCount);
				if (locaDelWaterList != null && locaDelWaterList.size() > 0) {
					for (WaterCount locaWaterCount : locaDelWaterList) {
						if (locaWaterCount.waterDate
								.equals(waterCount.waterDate)) {
							syncDelWater.remove(waterCount);
						}
					}
				}
			}

			ArrayList<WaterCount> addWaterList = new ArrayList<WaterCount>();
			ArrayList<WaterCount> locaWaterList = waterDao
					.getWaterCountBySyncId(context, "1", profile.id);
			for (WaterCount waterCount : syncDelWater) {
				addWaterList.add(waterCount);
				if (locaWaterList != null && locaWaterList.size() > 0) {
					for (WaterCount locaWaterCount : locaWaterList) {
						if (locaWaterCount.waterDate.equals(waterCount.waterDate)) {
							addWaterList.remove(waterCount);
						}
					}
				}
			}
			
			for (WaterCount waterCount : addWaterList) {
				addWater(waterCount);
			}

			ArrayList<BowelCount> syncDelBowelList = new ArrayList<BowelCount>();
			ArrayList<BowelCount> locaDelBowelList = bowelDao
					.getBowelCountBySyncId(context, "3", profile.id);
			for (BowelCount bowelCount : syncBowel) {
				syncDelBowelList.add(bowelCount);
				if (locaDelBowelList != null && locaDelBowelList.size() > 0) {
					for (BowelCount locaBowelCount : locaDelBowelList) {
						if (locaBowelCount.bowelDate
								.equals(bowelCount.bowelDate)) {
							syncDelBowelList.remove(bowelCount);
						}
					}
				}
			}

			ArrayList<BowelCount> addBowelList = new ArrayList<BowelCount>();
			ArrayList<BowelCount> locaBowelList = bowelDao
					.getBowelCountBySyncId(context, "1", profile.id);
			for (BowelCount bowelCount : syncDelBowelList) {
				addBowelList.add(bowelCount);
				if (locaBowelList != null && locaBowelList.size() > 0) {
					for (BowelCount locaBowelCount : locaBowelList) {
						if (locaBowelCount.bowelDate
								.equals(bowelCount.bowelDate)) {
							addBowelList.remove(bowelCount);
						}
					}
				}
			}
			
			for (BowelCount bowelCount : addBowelList) {
				addBowel(bowelCount);
			}

			// 删除同步食物记录
			ArrayList<Meal> delMealList = mealDao.getAllMeal(context,
					profile.id, "3");
			for (Meal meal : delMealList) {
				query = new KiiQuery(KiiClause.equals(Constants.mealID,
						meal.mealId));
				result = currentUser.bucket(Constants.MyMeal).query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					try {
						Uri uri = kiiObject.getUri(Constants.mealImageUri);
						if (uri != null) {
							KiiFile file = KiiFile.createByUri(uri);
							file.delete();
						}
					} catch (Exception e) {
					}

					kiiObject.delete();
					int count = mealDao.delMeal(context, meal);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context, meal.id,
								"1");
						Date date = dateFormat.parse(meal.createDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}

					}
				}

			}

			// 修改同步食物记录
			ArrayList<Meal> updateMealList = mealDao.getAllMeal(context,
					profile.id, "2");
			for (Meal meal : updateMealList) {
				query = new KiiQuery(KiiClause.equals(Constants.mealID,
						meal.mealId));
				result = currentUser.bucket(Constants.MyMeal).query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					setObject(kiiObject, meal, true);
					kiiObject.save();

					meal.syncId = 1;
					mealDao.updateMeal(context, meal);
				}

			}

			// 同步食物记录
			ArrayList<Meal> mealList = mealDao.getAllMeal(context, profile.id,
					"0");
			for (Meal meal : mealList) {
				KiiObject object = currentUser.bucket(Constants.MyMeal)
						.object();
				setObject(object, meal, false);
				object.save();

				meal.syncId = 1;
				mealDao.updateMeal(context, meal);
			}

			// 删除同步运动记录
			ArrayList<Exercise> delExerciseList = MyActivityDao.getInstance()
					.getActivity(context, "3", profile.id);
			for (Exercise exercise : delExerciseList) {
				query = new KiiQuery(KiiClause.equals(Constants.activityID,
						exercise.activityId));
				result = currentUser.bucket(Constants.MyActivity).query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {

					try {
						Uri uri = kiiObject.getUri(Constants.mealImageUri);
						if (uri != null) {
							KiiFile file = KiiFile.createByUri(uri);
							file.delete();
						}
					} catch (Exception e) {
					}

					kiiObject.delete();
					int count = activityDao.delActivity(context, exercise);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								exercise.id, "2");
						Date date = dateFormat.parse(exercise.createDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}

					}
				}

			}

			// 修改同步运动记录
			ArrayList<Exercise> updateExerciseList = MyActivityDao
					.getInstance().getActivity(context, "2", profile.id);
			for (Exercise exercise : updateExerciseList) {
				query = new KiiQuery(KiiClause.equals(Constants.activityID,
						exercise.activityId));
				result = currentUser.bucket(Constants.MyActivity).query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					setActivityObject(kiiObject, exercise, true);
					kiiObject.save();

					exercise.syncId = 1;
					activityDao.updateActivity(context, exercise);
				}

			}

			// 同步运动记录
			ArrayList<Exercise> exerciseList = activityDao.getActivity(context,
					"0", profile.id);
			for (Exercise exercise : exerciseList) {
				KiiObject object = currentUser.bucket(Constants.MyActivity)
						.object();
				setActivityObject(object, exercise, false);
				object.save();

				exercise.syncId = 1;
				activityDao.updateActivity(context, exercise);
			}

			// 删除同步测量记录
			ArrayList<Measurement> delMeasurementList = MeasurementDao
					.getInstance().getMeasurementBySyncId(context, "3",
							profile.id);
			for (Measurement measurement : delMeasurementList) {
				query = new KiiQuery(KiiClause.equals(Constants.measurementID,
						measurement.measurementId));
				result = currentUser.bucket(Constants.MyMeasurement).query(
						query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {

					try {
						Uri uri = kiiObject.getUri(Constants.imageUri1);
						if (uri != null) {
							KiiFile file = KiiFile.createByUri(uri);
							file.delete();
						}
					} catch (Exception e) {
					}

					try {
						Uri uri = kiiObject.getUri(Constants.imageUri2);
						if (uri != null) {
							KiiFile file = KiiFile.createByUri(uri);
							file.delete();
						}
					} catch (Exception e) {
					}

					try {
						Uri uri = kiiObject.getUri(Constants.imageUri3);
						if (uri != null) {
							KiiFile file = KiiFile.createByUri(uri);
							file.delete();
						}
					} catch (Exception e) {
					}

					kiiObject.delete();
					int count = measurementDao.delMeasurement(context,
							measurement);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								measurement.measurementId, "3");
						Date date = dateFormat.parse(measurement.createDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}

					}
				}

			}

			// 修改同步测量记录
			ArrayList<Measurement> updateMeasurementList = MeasurementDao
					.getInstance().getMeasurementBySyncId(context, "2",
							profile.id);
			for (Measurement measurement : updateMeasurementList) {
				query = new KiiQuery(KiiClause.equals(Constants.measurementID,
						measurement.measurementId));
				result = currentUser.bucket(Constants.MyMeasurement).query(
						query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					setMeasurementObject(kiiObject, measurement, true);
					kiiObject.save();

					measurement.syncId = 1;
					measurementDao.updateMeasurement(context, measurement);
				}

			}

			// 同步测量记录
			ArrayList<Measurement> measurementList = MeasurementDao
					.getInstance().getMeasurementBySyncId(context, "0",
							profile.id);
			for (Measurement measurement : measurementList) {
				KiiObject object = currentUser.bucket(Constants.MyMeasurement)
						.object();
				setMeasurementObject(object, measurement, false);
				object.save();

				measurement.syncId = 1;
				measurementDao.updateMeasurement(context, measurement);
			}

			// 删除同步睡眠记录
			ArrayList<Sleep> delSleeplist = sleepDao.getSleepBySyncId(context,
					"3", profile.id);
			for (Sleep sleep : delSleeplist) {
				query = new KiiQuery(KiiClause.equals(Constants.sleepID,
						sleep.sleepId));
				result = currentUser.bucket(Constants.MySleep).query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					kiiObject.delete();
					int count = sleepDao.delSleep(context, sleep);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								sleep.id, "4");
						Date date = dateFormat.parse(sleep.createDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}
					}
				}
			}

			// 同步睡眠记录
			ArrayList<Sleep> updateSleeplist = sleepDao.getSleepBySyncId(
					context, "0", profile.id);
			for (Sleep sleep : updateSleeplist) {
				KiiObject object = currentUser.bucket(Constants.MySleep)
						.object();
				object.set(Constants.sleepID, sleep.sleepId);
				object.set(Constants.sleepSyncID, 1);
				object.set(Constants.sleepReportDate, sleep.createDate);
				object.set(Constants.sleepStart, sleep.sleepStart);
				object.set(Constants.sleepEnd, sleep.sleepEnd);
				object.save();

				sleep.syncId = 1;
				sleepDao.updateSleep(context, sleep);
			}

			// 删除同步喝水记录
			ArrayList<WaterCount> delWaterList = WaterCountDao.getInstance()
					.getWaterCountBySyncId(context, "3", profile.id);
			for (WaterCount waterCount : delWaterList) {
				Date date = dateFormat.parse(waterCount.waterDate);
				String recordDate = dateSF.format(date);
				query = new KiiQuery(KiiClause.equals(
						Constants.dailycountRecordDate, recordDate));
				result = currentUser.bucket(Constants.MyDailycount)
						.query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					String jsonStr = kiiObject
							.getString(Constants.dailycountWater);
					JSONArray array = new JSONArray(jsonStr);
					JSONObject newJson = new JSONObject();
					JSONArray newArray = new JSONArray();
					try {
						for (int i = 0; i < array.length(); i++) {
							String waterDate = array.getJSONObject(i)
									.getString(Constants.waterCreateDate);
							if (waterDate != null
									&& !waterCount.waterDate.equals(waterDate)) {
								newJson.put(Constants.waterCreateDate,
										waterDate);
								newJson.put(Constants.waterSyncID, 1);

							}
						}
					} catch (Exception e) {
					}
					newArray.put(newJson);
					kiiObject.set(Constants.dailycountWater, newArray);
					kiiObject.save();

					int count = waterDao.delWater(context, waterCount);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								waterCount.id, "5");
						date = dateFormat.parse(waterCount.waterDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}
					}
				}
			}

			// 同步喝水记录
			ArrayList<String> dateList = waterDao.getGroupDate(context,
					profile.id);
			for (String date : dateList) {
				ArrayList<WaterCount> waterList = WaterCountDao.getInstance()
						.getWaterCount(context, date, profile.id);
				for (WaterCount water : waterList) {
					Date currentDate = dateFormat.parse(water.waterDate);
					String recordDate = dateSF.format(currentDate);
					query = new KiiQuery(KiiClause.equals(
							Constants.dailycountRecordDate, recordDate));
					result = currentUser.bucket(Constants.MyDailycount).query(
							query);
					List<KiiObject> listObj = result.getResult();
					if (listObj != null && listObj.size() > 0) {
						for (KiiObject kiiObject : listObj) {
							String jsonStr = kiiObject
									.getString(Constants.dailycountWater);
							JSONArray array = new JSONArray();
							if (jsonStr != null && jsonStr.length() > 0) {
								array = new JSONArray(jsonStr);
							}
							JSONObject jsonObj = new JSONObject();
							jsonObj.put(Constants.waterCreateDate,
									water.waterDate);
							jsonObj.put(Constants.waterSyncID, 1);

							array.put(jsonObj);
							kiiObject.set(Constants.dailycountWater, array);

							kiiObject.save();

							water.syncId = 1;
							waterDao.updateWater(context, water);
						}
					} else {
						KiiObject object = currentUser.bucket(
								Constants.MyDailycount).object();
						object.set(Constants.dailycountRecordDate, date);
						object.set(Constants.dailycountBowel,
								new JSONArray());
						JSONObject jsonObj = new JSONObject();
						jsonObj.put(Constants.waterCreateDate, water.waterDate);
						jsonObj.put(Constants.waterSyncID, 1);

						JSONArray array = new JSONArray();
						array.put(jsonObj);
						object.set(Constants.dailycountWater, array);
						object.save();

						water.syncId = 1;
						waterDao.updateWater(context, water);
					}
				}
			}

			// 删除同步排泄记录
			ArrayList<BowelCount> delBowelList = BowelCountDao.getInstance()
					.getBowelCountBySyncId(context, "3", profile.id);
			for (BowelCount bowelCount : delBowelList) {
				Date date = dateFormat.parse(bowelCount.bowelDate);
				String recordDate = dateSF.format(date);
				query = new KiiQuery(KiiClause.equals(
						Constants.dailycountRecordDate, recordDate));
				result = currentUser.bucket(Constants.MyDailycount)
						.query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					String jsonStr = kiiObject
							.getString(Constants.dailycountBowel);
					JSONArray array = new JSONArray(jsonStr);
					JSONArray newArray = new JSONArray();
					JSONObject newJson = new JSONObject();
					try {
						for (int i = 0; i < array.length(); i++) {
							String bowelDate = array.getJSONObject(i)
									.getString(Constants.bowelCreateDate);
							if (bowelDate != null
									&& !bowelCount.bowelDate.equals(bowelDate)) {
								newJson.put(Constants.bowelCreateDate,
										bowelDate);
								newJson.put(Constants.bowelSyncID, 1);
							}
						}
					} catch (Exception e) {
					}
					newArray.put(newJson);
					kiiObject.set(Constants.dailycountBowel, newArray);
					kiiObject.save();

					int count = bowelDao.delBowel(context, bowelCount);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(context,
								bowelCount.id, "6");
						date = dateFormat.parse(bowelCount.bowelDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(context,
										currentDate, profile.id);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									context);
						}
					}
				}
			}

			// 同步排泄记录
			dateList = bowelDao.getGroupDate(context, profile.id);
			for (String date : dateList) {
				ArrayList<BowelCount> bowelList = BowelCountDao.getInstance()
						.getBowelCount(context, date, profile.id);
				for (BowelCount bowel : bowelList) {
					Date currentDate = dateFormat.parse(bowel.bowelDate);
					String recordDate = dateSF.format(currentDate);
					query = new KiiQuery(KiiClause.equals(
							Constants.dailycountRecordDate, recordDate));
					result = currentUser.bucket(Constants.MyDailycount).query(
							query);
					List<KiiObject> listObj = result.getResult();
					if (listObj != null && listObj.size() > 0) {
						for (KiiObject kiiObject : listObj) {
							String jsonStr = kiiObject
									.getString(Constants.dailycountBowel);
							JSONArray array = new JSONArray();
							if (jsonStr != null && jsonStr.length() > 0) {
								array = new JSONArray(jsonStr);
							}
							JSONObject jsonObj = new JSONObject();
							jsonObj.put(Constants.bowelCreateDate,
									bowel.bowelDate);
							jsonObj.put(Constants.bowelSyncID, 1);
							array.put(jsonObj);
							kiiObject.set(Constants.dailycountBowel, array);
							kiiObject.save();

							bowel.syncId = 1;
							bowelDao.updateBowel(context, bowel);
						}
					} else {
						KiiObject object = currentUser.bucket(
								Constants.MyDailycount).object();
						object.set(Constants.dailycountRecordDate, date);
						object.set(Constants.dailycountWater,
								new JSONArray());
						JSONObject jsonObj = new JSONObject();
						jsonObj.put(Constants.bowelCreateDate, bowel.bowelDate);
						jsonObj.put(Constants.bowelSyncID, 1);
						JSONArray array = new JSONArray();
						array.put(jsonObj);
						object.set(Constants.dailycountBowel, array);
						object.save();

						bowel.syncId = 1;
						bowelDao.updateBowel(context, bowel);
					}
				}
			}

		} catch (Exception e) {
			Log.e(TAG, "--" + e.getMessage());
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(!isAutoSync){
			Message msg = new Message();
			msg.what = 0;
			mHandler.sendMessage(msg);
		}else{
			Message msg = new Message();
			msg.what = 1;
			mHandler.sendMessage(msg);
		}
	}

	private void setObject(KiiObject object, Meal meal, boolean isUpdate) {
		try {
			object.set(Constants.mealID, meal.mealId);
			object.set(Constants.mealTitle, meal.title);
			object.set(Constants.mealRecordDate, meal.createDate);
			object.set(Constants.mealComment, "");
			object.set(Constants.mealLike, meal.like);
			object.set(Constants.mealRemark, "");
			object.set(Constants.mealSyncID, 1);

			FileCache.CreateText();

			if (meal.image != null && meal.image.length() > 0) {

				File localFile = new File(meal.image);
				
				if (isUpdate) {
					Uri uri = object.getUri(Constants.mealImageUri);
					if (uri != null) {
						KiiFile file = KiiFile.createByUri(uri);
						file.save(localFile);

						String url = file.publish();

						// 设置用户可读取图片
						object.set(Constants.mealImage, url);
						object.set(Constants.mealImageUri, file.toUri());
					} else {
						if (localFile != null) {
							KiiFileBucket fileBucket = Kii
									.fileBucket(Constants.MyMeal);

							KiiFile kiiFile = fileBucket.file(localFile);
							kiiFile.save();

							String url = kiiFile.publish();

							// 设置用户可读取图片
							KiiACL acl = kiiFile.acl();
							acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser
									.create(), FileAction.READ_EXISTING_OBJECT,
									true));
							acl.save();
							object.set(Constants.mealImage, url);
							object.set(Constants.mealImageUri, kiiFile.toUri());
						}
					}
				} else {
					if (localFile != null) {
						KiiFileBucket fileBucket = Kii
								.fileBucket(Constants.MyMeal);

						KiiFile kiiFile = fileBucket.file(localFile);
						kiiFile.save();

						String url = kiiFile.publish();

						// 设置用户可读取图片
						KiiACL acl = kiiFile.acl();
						acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser
								.create(), FileAction.READ_EXISTING_OBJECT,
								true));
						acl.save();
						object.set(Constants.mealImage, url);
						object.set(Constants.mealImageUri, kiiFile.toUri());
					}
				}
			} else {
				object.set(Constants.mealImage, "");
				object.set(Constants.mealImageUri, "");
			}
			JSONArray jsonArray = new JSONArray();
			ArrayList<MealDetail> mealDetaillist = mealDetailDao
					.getMealDetailById(context, meal.id);
			for (MealDetail mealDetail : mealDetaillist) {
				JSONObject jsonObj = new JSONObject();

				jsonObj.put(Constants.FOOD_ID, mealDetail.foodId);
				jsonObj.put(Constants.CATEGORY, mealDetail.Category);
				jsonObj.put(Constants.QTY, mealDetail.Amount);
				jsonObj.put(Constants.FOOD_UNIT, mealDetail.unit);
				jsonObj.put(Constants.KCAL, mealDetail.calorie);
				jsonObj.put(
						Constants.PORTION,
						mealDetail.portion.substring(0,
								mealDetail.portion.length() - 2));
				jsonObj.put(Constants.PORTION_UNIT, mealDetail.portion
						.substring(mealDetail.portion.length() - 2));

				jsonArray.put(jsonObj);
			}
			object.set(Constants.mealFood, jsonArray);
		} catch (Exception e) {

		}
	}

	private void setActivityObject(KiiObject object, Exercise exercise,
			boolean isUpdate) {
		try {
			object.set(Constants.activityID, exercise.activityId);
			object.set(Constants.activityTitle, exercise.title);
			object.set(Constants.activityRecordDate, exercise.createDate);
			object.set(Constants.activityComment, "");
			object.set(Constants.activityLike, exercise.like);
			object.set(Constants.activityRemark, "");
			object.set(Constants.activitySyncID, 1);

			FileCache.CreateText();

			if (exercise.image != null && exercise.image.length() > 0) {

				File localFile = new File(exercise.image);
				if (isUpdate) {
					Uri uri = object.getUri(Constants.mealImageUri);
					if (uri != null) {
						if (localFile != null) {
							KiiFile file = KiiFile.createByUri(uri);
							file.save(localFile);

							String url = file.publish();

							// 设置用户可读取图片
							KiiACL acl = file.acl();
							acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser
									.create(), FileAction.READ_EXISTING_OBJECT,
									true));
							acl.save();
							object.set(Constants.activityImage, url);
							object.set(Constants.mealImageUri, file.toUri());
						}
					} else {
						if (localFile != null) {
							KiiFileBucket fileBucket = Kii
									.fileBucket(Constants.MyActivity);

							KiiFile kiiFile = fileBucket.file(localFile);
							kiiFile.save();

							String url = kiiFile.publish();

							// 设置用户可读取图片
							KiiACL acl = kiiFile.acl();
							acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser
									.create(), FileAction.READ_EXISTING_OBJECT,
									true));
							acl.save();
							object.set(Constants.activityImage, url);
							object.set(Constants.mealImageUri, kiiFile.toUri());
						}
					}
				} else {
					if (localFile != null) {
						KiiFileBucket fileBucket = Kii
								.fileBucket(Constants.MyActivity);

						KiiFile kiiFile = fileBucket.file(localFile);
						kiiFile.save();

						String url = kiiFile.publish();

						// 设置用户可读取图片
						KiiACL acl = kiiFile.acl();
						acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser
								.create(), FileAction.READ_EXISTING_OBJECT,
								true));
						acl.save();
						object.set(Constants.activityImage, url);
						object.set(Constants.mealImageUri, kiiFile.toUri());
					}
				}
			} else {
				object.set(Constants.activityImage, "");
				object.set(Constants.mealImageUri, "");
			}
			JSONArray jsonArray = new JSONArray();
			ArrayList<ExerciseDetail> exerciseDetaillist = activityDetailDao
					.getActivityDetailById(context, exercise.id);
			for (ExerciseDetail exerciseDetail : exerciseDetaillist) {
				JSONObject jsonObj = new JSONObject();

				jsonObj.put(Constants.EXERCISE_ID, exerciseDetail.activityId);
				jsonObj.put(Constants.TIME, exerciseDetail.time);
				jsonObj.put(Constants.CALORIE, exerciseDetail.calorie);
				jsonObj.put(Constants.FOOD_UNIT, exerciseDetail.unit);
				jsonArray.put(jsonObj);
			}
			object.set(Constants.activityExercise, jsonArray);
		} catch (Exception e) {

		}
	}

	private void setMeasurementObject(KiiObject object,
			Measurement measurement, boolean isUpdate) {
		try {
			object.set(Constants.measurementID, measurement.measurementId);
			object.set(Constants.measurementWeight, measurement.weight);
			object.set(Constants.measurementReportDate, measurement.createDate);
			object.set(Constants.measurementBodyFat, measurement.bodyFat);
			object.set(Constants.measurementLeanMuscle, measurement.leanMusde);
			object.set(Constants.measurementVisceralFat,
					measurement.visceralFat);
			object.set(Constants.measurementBodyAge, measurement.bodyAge);
			object.set(Constants.measurementBMI, measurement.BMI);
			object.set(Constants.measurementBMR, measurement.BMR);
			object.set(Constants.measurementArm, measurement.arm);
			object.set(Constants.measurementWaist, measurement.waist);
			object.set(Constants.measurementAbd, measurement.abd);
			object.set(Constants.measurementHip, measurement.hip);
			object.set(Constants.measurementThigh, measurement.thigh);
			object.set(Constants.measurementCalf, measurement.calf);
			object.set(Constants.measurementRemark, measurement.remark);
			object.set(Constants.measurementSyncID, 1);

			FileCache.CreateText();

			if (measurement.image1 != null && measurement.image1.length() > 0) {

				File localFile = new File(measurement.image1);

				if (isUpdate) {
					try {
						object.set(Constants.measurementImage1,
								object.getString(Constants.measurementImage1));
					} catch (Exception e) {
					}

					try {
						Uri uri = object.getUri(Constants.imageUri1);
						object.set(Constants.imageUri1, uri);
					} catch (Exception e) {
					}
				} else {
					if (localFile != null) {
						KiiFileBucket fileBucket = Kii
								.fileBucket(Constants.MyMeasurement);

						KiiFile kiiFile = fileBucket.file(localFile);
						kiiFile.save();

						String url = kiiFile.publish();

						// 设置用户可读取图片
						KiiACL acl = kiiFile.acl();
						acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser
								.create(), FileAction.READ_EXISTING_OBJECT,
								true));
						acl.save();
						object.set(Constants.measurementImage1, url);
						object.set(Constants.imageUri1, kiiFile.toUri());
					}
				}
			} else {
				object.set(Constants.measurementImage1, "");
				object.set(Constants.imageUri1, "");
			}

			if (measurement.image2 != null && measurement.image2.length() > 0) {

				File localFile = new File(measurement.image2);

				if (isUpdate) {
					try {
						object.set(Constants.measurementImage2,
								object.getString(Constants.measurementImage2));
					} catch (Exception e) {
					}

					try {
						Uri uri = object.getUri(Constants.imageUri2);
						object.set(Constants.imageUri2, uri);
					} catch (Exception e) {
					}
				} else {
					if (localFile != null) {
						KiiFileBucket fileBucket = Kii
								.fileBucket(Constants.MyMeasurement);

						KiiFile kiiFile = fileBucket.file(localFile);
						kiiFile.save();

						String url = kiiFile.publish();

						// 设置用户可读取图片
						KiiACL acl = kiiFile.acl();
						acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser
								.create(), FileAction.READ_EXISTING_OBJECT,
								true));
						acl.save();
						object.set(Constants.measurementImage2, url);
						object.set(Constants.imageUri2, kiiFile.toUri());
					}
				}
			} else {
				object.set(Constants.measurementImage2, "");
				object.set(Constants.imageUri2, "");
			}

			if (measurement.image3 != null && measurement.image3.length() > 0) {

				File localFile = new File(measurement.image3);

				if (isUpdate) {
					try {
						object.set(Constants.measurementImage3,
								object.getString(Constants.measurementImage3));
					} catch (Exception e) {
					}

					try {
						Uri uri = object.getUri(Constants.imageUri3);
						object.set(Constants.imageUri3, uri);
					} catch (Exception e) {
					}
				} else {
					if (localFile != null) {
						KiiFileBucket fileBucket = Kii
								.fileBucket(Constants.MyMeasurement);

						KiiFile kiiFile = fileBucket.file(localFile);
						kiiFile.save();

						String url = kiiFile.publish();

						// 设置用户可读取图片
						KiiACL acl = kiiFile.acl();
						acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser
								.create(), FileAction.READ_EXISTING_OBJECT,
								true));
						acl.save();
						object.set(Constants.measurementImage3, url);
						object.set(Constants.imageUri3, kiiFile.toUri());
					}
				}
			} else {
				object.set(Constants.mealImageUri, "");
				object.set(Constants.measurementImage3, "");
			}

		} catch (Exception e) {
			Log.i(TAG, "AccountActivity.setObject()" + e.getMessage());

		}
	}

	private void addMeal(Meal meal) {
		if (mealDao.insert(context, meal)) {
			MyRecords info = new MyRecords();
			info.createDate = meal.createDate;
			meal.id = mealDao.getMealId(context, meal.mealId, meal.userId);
			info.recordId = meal.id;
			info.type = 1;
			info.userId = meal.userId;

			MyRecordsDao.getInstance().insert(context, info);

			for (MealDetail detail : meal.mealDetail) {
				detail.recordId = meal.id;
				mealDetailDao.insert(context, detail);
			}

			try {
				Date date = dateFormat.parse(meal.createDate);
				String dateStr = dateSF.format(date);
				if (MyRecordsDao.getInstance().getRecord(context, dateStr,
						meal.userId)) {
					MyRecords records = new MyRecords();
					records.createDate = dateStr;
					records.recordId = "";
					records.type = 7;
					records.userId = meal.userId;
					MyRecordsDao.getInstance().insert(context, records);
				}
			} catch (Exception e) {

			}
		}

	}

	private void addExercise(Exercise exercise) {
		if (activityDao.insert(context, exercise)) {
			MyRecords info = new MyRecords();
			info.createDate = exercise.createDate;
			exercise.id = activityDao.getActivityId(context,
					exercise.activityId, exercise.userId);
			info.recordId = exercise.id;
			info.type = 2;
			info.userId = exercise.userId;

			MyRecordsDao.getInstance().insert(context, info);

			for (ExerciseDetail detail : exercise.exerciseDetail) {
				detail.recordId = exercise.id;
				activityDetailDao.insert(context, detail);
			}

			try {
				Date date = dateFormat.parse(exercise.createDate);
				String dateStr = dateSF.format(date);
				if (MyRecordsDao.getInstance().getRecord(context, dateStr,
						exercise.userId)) {
					MyRecords records = new MyRecords();
					records.createDate = dateStr;
					records.recordId = "";
					records.type = 7;
					records.userId = exercise.userId;
					MyRecordsDao.getInstance().insert(context, records);
				}
			} catch (Exception e) {

			}
		}

	}

	private void addMeasurement(Measurement measurement) {
		if (measurementDao.insert(context, measurement)) {
			MyRecords records = new MyRecords();
			records.createDate = measurement.createDate;
			String recordId = measurementDao.getMeasurementId(context,
					measurement.measurementId, measurement.userId);
			records.recordId = recordId;
			records.type = 3;
			records.userId = measurement.userId;

			MyRecordsDao.getInstance().insert(context, records);

			Date currentDate = null;
			try {
				currentDate = dateFormat.parse(measurement.createDate);
				String dateStr = dateSF.format(currentDate);
				if (MyRecordsDao.getInstance().getRecord(context, dateStr,
						measurement.userId)) {
					MyRecords myRecords = new MyRecords();
					myRecords.createDate = dateStr;
					myRecords.recordId = "";
					myRecords.userId = measurement.userId;
					myRecords.type = 7;
					MyRecordsDao.getInstance().insert(context, myRecords);
				}
			} catch (Exception e) {
			}

		}
	}

	private void addSleep(Sleep sleep) {
		if (sleepDao.insert(context, sleep)) {
			MyRecords info = new MyRecords();
			info.createDate = sleep.createDate;
			info.type = 4;
			info.recordId = sleepDao.getSleepId(context, sleep.sleepId,
					sleep.userId);
			info.userId = sleep.userId;

			MyRecordsDao.getInstance().insert(context, info);

			Date currentDate = null;
			try {
				currentDate = dateFormat.parse(sleep.createDate);
				String dateStr = dateSF.format(currentDate);
				if (MyRecordsDao.getInstance().getRecord(context, dateStr,
						sleep.userId)) {
					MyRecords myRecords = new MyRecords();
					myRecords.createDate = dateStr;
					myRecords.recordId = "";
					myRecords.type = 7;
					myRecords.userId = sleep.userId;
					MyRecordsDao.getInstance().insert(context, myRecords);
				}
			} catch (Exception e) {
			}
		}
	}

	private void addWater(WaterCount water) {
		if (waterDao.insert(context, water)) {
			MyRecords info = new MyRecords();
			info.createDate = water.waterDate;
			info.type = 5;
			info.recordId = waterDao.getWaterCountId(context, water.waterDate,
					water.userId);
			info.userId = water.userId;

			MyRecordsDao.getInstance().insert(context, info);

			if (MyRecordsDao.getInstance().getRecord(context, water.yearMonth,
					water.userId)) {
				MyRecords records = new MyRecords();
				records.createDate = water.yearMonth;
				records.recordId = "";
				records.type = 7;
				records.userId = water.userId;
				MyRecordsDao.getInstance().insert(context, records);
			}
		}
	}

	private void addBowel(BowelCount bowel) {
		if (bowelDao.insert(context, bowel)) {
			MyRecords info = new MyRecords();
			info.createDate = bowel.bowelDate;
			info.type = 6;
			info.recordId = bowelDao.getBowelCountId(context, bowel.bowelDate,
					bowel.userId);
			info.userId = bowel.userId;

			MyRecordsDao.getInstance().insert(context, info);

			if (MyRecordsDao.getInstance().getRecord(context, bowel.yearMonth,
					bowel.userId)) {
				MyRecords records = new MyRecords();
				records.createDate = bowel.yearMonth;
				records.recordId = "";
				records.type = 7;
				records.userId = bowel.userId;
				MyRecordsDao.getInstance().insert(context, records);
			}
		}
	}
}
