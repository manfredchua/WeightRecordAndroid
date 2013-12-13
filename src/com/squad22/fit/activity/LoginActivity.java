package com.squad22.fit.activity;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.Kii.Site;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squad22.fit.R;
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
import com.squad22.fit.dao.SummaryDao;
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
import com.squad22.fit.entity.Summary;
import com.squad22.fit.entity.WaterCount;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.ImagesUtils;
import com.squad22.fit.utils.XmlPullParserMessage;

@SuppressLint("SimpleDateFormat")
public class LoginActivity extends Activity implements OnClickListener {

	ActionBar actionBar;
	EditText etName;
	EditText etPassword;
	TextView txtForgetPassword;
	Button btnLogin;
	Button btnRegister;
	ProgressDialog proDialog;
	boolean isSearch = false;
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	MyActivityDao activityDao = MyActivityDao.getInstance();
	MyMealDao mealDao = MyMealDao.getInstance();
	ActivityDetailDao activityDetailDao = ActivityDetailDao.getInstance();
	MealDetailDao mealDetailDao = MealDetailDao.getInstance();
	MeasurementDao measurementDao = MeasurementDao.getInstance();
	SleepDao sleepDao = SleepDao.getInstance();
	WaterCountDao waterDao = WaterCountDao.getInstance();
	BowelCountDao bowelDao = BowelCountDao.getInstance();
	ProfileDao profileDao = ProfileDao.getInstance();

	static {
		Kii.initialize(Constants.APP_ID, Constants.APP_KEY, Site.CN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.login_layout);

		isSearch = getIntent().getBooleanExtra(Constants.SEARCH, false);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.login));

		initView();

		new GetConfigTask().execute();
	}

	private void initView() {
		etName = (EditText) findViewById(R.id.et_name);
		etPassword = (EditText) findViewById(R.id.et_password);
		txtForgetPassword = (TextView) findViewById(R.id.txt_forget_password);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnRegister = (Button) findViewById(R.id.btn_register);

		btnLogin.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
		txtForgetPassword.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			String name = CommUtils.getEditTextValue(etName);
			final String pwd = CommUtils.getEditTextValue(etPassword);
			if (name.equals("") || name == null) {
				etName.setError(getString(R.string.name_or_email));
				etName.requestFocus();
			} else if (pwd.equals("") || pwd == null) {
				etPassword.setError(getString(R.string.password_error));
				etPassword.requestFocus();
			} else {

				if (CommUtils.checkNetwork(this)) {
					proDialog = CommUtils.createProgressDialog(this, proDialog,
						getString(R.string.login)	+ "...");
					KiiUser.logIn(new KiiUserCallBack() {
						@Override
						public void onLoginCompleted(int token, KiiUser user,
								Exception exception) {
							super.onLoginCompleted(token, user, exception);
							if (proDialog != null && proDialog.isShowing()) {
								proDialog.dismiss();
							}
							if (exception == null) {
								SharedPreferences sp = getSharedPreferences(
										Constants.KIIUSER, MODE_PRIVATE);
								Editor editor = sp.edit();
								editor.putString(Constants.TOKEN,
										user.getAccessToken());
								editor.putString(Constants.USERNAME,
										user.getUsername());
								editor.putBoolean(Constants.LOGIN_STATUS, true);
								editor.commit();
								if (isSearch) {
									LoginActivity.this.finish();
								} else {
									Profile info = ProfileDao.getInstance()
											.getProfile(LoginActivity.this,
													user.getUsername());
									if (info.name == null) {

										String displayname = "";
										try {
											displayname = user
													.getString(Constants.FullName);
											new GetProfileTask(false).execute();
										} catch (Exception e) {
										}
										if (displayname != null
												&& !displayname.equals("")) {

											proDialog = CommUtils
													.createProgressDialog(
															LoginActivity.this,
															proDialog,
															getString(R.string.download_data));
											new GetMessageTask(LoginActivity.this, user).execute();

										} else {
											Intent intent = new Intent(
													LoginActivity.this,
													CompleteProfileActivity.class);
											startActivity(intent);
										}

									} else {
										proDialog = CommUtils
												.createProgressDialog(
														LoginActivity.this,
														proDialog,
														getString(R.string.download_data));
										new GetProfileTask(true).execute();
//										new GetMessageTask(LoginActivity.this,
//												user).execute();
									}
								}
							} else {
								if (exception.getMessage() != null) {
									CommUtils.showError(LoginActivity.this, exception.getMessage());
									
								} else {
									CommUtils.showToast(LoginActivity.this,
											getString(R.string.login_failure));
								}
							}
						}
					}, name, pwd);
				} else {
					CommUtils.showToast(this, getString(R.string.not_network));
				}
			}

			break;
		case R.id.btn_register:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.txt_forget_password:
			intent = new Intent(this, ForgetPasswordActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	private class GetProfileTask extends AsyncTask<Void, Void, Void> {
		
		private boolean isUpdate;
		
		public GetProfileTask(boolean isUpdate){
			this.isUpdate = isUpdate;
		}

		@Override
		protected Void doInBackground(Void... params) {
			KiiUser user = KiiUser.getCurrentUser();
			Profile entity = new Profile();
			entity.userName = user.getUsername();
			try {
				entity.name = user.getString(Constants.FullName);
			} catch (Exception e) {
			}

			try {
				entity.backupState = user.getInt(Constants.BACKUPSTATE);
			} catch (Exception e3) {
			}

			try {
				entity.birthday = user.getString(Constants.Birthday);
			} catch (Exception e1) {
			}
			try {
				entity.height = user.getDouble(Constants.Height);
			} catch (Exception e2) {
			}
			try {
				entity.weight = user.getDouble(Constants.CurrentWeight);
			} catch (Exception e1) {
			}
			try {
				entity.sex = user.getString(Constants.Gender);
			} catch (Exception e1) {
			}
			try {
				entity.unit = user.getString(Constants.UNIT);
			} catch (Exception e) {
				entity.unit = Constants.METRIC;
			}
			try {
				String imgPath = user.getString(Constants.AvatarUrl);

				String bm = ImagesUtils.decodeImageURl(imgPath);
				if (bm != null) {
					entity.image = bm;
				}
			} catch (Exception e) {
			}
			
			if(isUpdate){
				int count = ProfileDao.getInstance().updateProfile(LoginActivity.this, entity);
				if(count > 0){
					SharedPreferences sp = getSharedPreferences(
							Constants.BACKUP_RECORDS, MODE_PRIVATE);
					Editor editor = sp.edit();
					if (entity.backupState == 0) {
						editor.putBoolean(Constants.IS_RECORD, false);
					} else {
						editor.putBoolean(Constants.IS_RECORD, true);
						if (entity.backupState == 1) {
							editor.putString(Constants.WIFI, "wifi");
						} else {
							editor.putString(Constants.WIFI, "wifi_3g");
						}
					}
					editor.commit();
				}
			}else{
				if (ProfileDao.getInstance().insert(LoginActivity.this, entity)) {
	
					SharedPreferences sp = getSharedPreferences(
							Constants.BACKUP_RECORDS, MODE_PRIVATE);
					Editor editor = sp.edit();
					if (entity.backupState == 0) {
						editor.putBoolean(Constants.IS_RECORD, false);
					} else {
						editor.putBoolean(Constants.IS_RECORD, true);
						if (entity.backupState == 1) {
							editor.putString(Constants.WIFI, "wifi");
						} else {
							editor.putString(Constants.WIFI, "wifi_3g");
						}
					}
					editor.commit();
					
				}
			}
			return null;
		}

		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(isUpdate){
				if(proDialog != null && proDialog.isShowing()){
					proDialog.dismiss();
				}
				
				Intent intent = new Intent(
						LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		}
	}

	private class GetMessageTask extends AsyncTask<Void, Void, Void> {

		private static final String TAG = "GetMessageTask";
		Context mContext;
		KiiUser user;

		public GetMessageTask(Context mContext, KiiUser user) {
			this.mContext = mContext;
			this.user = user;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Profile profile = ProfileDao.getInstance().getProfile(mContext,
						user.getUsername());
				KiiUser currentUser = KiiUser.getCurrentUser();

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
							meal.remark = kiiObject
									.getString(Constants.mealRemark);
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
							meal.syncId = kiiObject
									.getInt(Constants.mealSyncID);
						} catch (Exception e1) {
						}
						try {
							String image = kiiObject
									.getString(Constants.mealImage);
							String bitmap = ImagesUtils.decodeImageURl(image);
							if (bitmap != null) {
								meal.image = bitmap;
							}
						} catch (Exception e1) {
						}

						String food = kiiObject.getString(Constants.mealFood);
						Log.i(TAG, "--" + food);

						meal.mealDetail = new ArrayList<MealDetail>();

						JSONArray array = new JSONArray(food);
						for (int i = 0; i < array.length(); i++) {
							MealDetail detail = new MealDetail();
							JSONObject jsonObj = array.getJSONObject(i);

							detail.foodId = jsonObj
									.getString(Constants.FOOD_ID);
							double qty = jsonObj.getDouble(Constants.QTY);
							detail.Amount = CommUtils.getDouble(qty);
							detail.unit = jsonObj
									.getString(Constants.FOOD_UNIT);

							try {
								double portion = jsonObj
										.getDouble(Constants.PORTION);
								detail.portion = CommUtils.getDouble(portion)
										+ jsonObj.getString(Constants.PORTION_UNIT);
							} catch (Exception e) {
							}
							detail.Category = jsonObj
									.getString(Constants.CATEGORY);
							detail.calorie = jsonObj.getString(Constants.KCAL);
							Food entity = FoodDao.getInstance().getFoodById(
									LoginActivity.this, detail.foodId);
							detail.name = entity.name;
							meal.mealDetail.add(detail);
						}
						syncMealList.add(meal);
					}
				} else {
					ArrayList<Meal> locaMealList = mealDao.getSyncDelMeal(
							LoginActivity.this, profile.id);
					for (Meal delMeal : locaMealList) {
						delMeal.syncId = 0;
						int count = mealDao
								.delMeal(LoginActivity.this, delMeal);
						if (count > 0) {
							MyRecordsDao.getInstance().delRecords(
									LoginActivity.this, delMeal.id, "1");
							Date date = dateFormat.parse(delMeal.createDate);
							String currentDate = dateSF.format(date);
							ArrayList<MyRecords> arrayList = MyRecordsDao
									.getInstance().getAllRecordByDate(
											LoginActivity.this, currentDate,
											profile.id);
							if (arrayList != null && arrayList.size() == 0) {
								MyRecordsDao.getInstance().delSummaryRecords(
										LoginActivity.this);
							}

						}
					}
				}

				ArrayList<Meal> syncDelMealList = new ArrayList<Meal>();

				ArrayList<Meal> locaDelMealList = mealDao.getAllMeal(
						LoginActivity.this, profile.id, "3");
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
				ArrayList<Meal> locaMealList = mealDao.getAllMeal(
						LoginActivity.this, profile.id, "1");

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
								int rowId = mealDao.updateMeal(
										LoginActivity.this, meal);
								if (rowId > 0) {
									mealDetailDao.delMealDetail(
											LoginActivity.this, meal.id);
									for (MealDetail detail : meal.mealDetail) {
										detail.recordId = meal.id;
										mealDetailDao.insert(
												LoginActivity.this, detail);
									}

									MyRecords info = new MyRecords();
									info.createDate = meal.createDate;
									info.recordId = meal.id;
									info.type = 1;

									MyRecordsDao.getInstance().updateRecords(
											LoginActivity.this, info);

									Date date = dateFormat
											.parse(meal.createDate);
									String dateStr = dateSF.format(date);
									if (MyRecordsDao.getInstance().getRecord(
											LoginActivity.this, dateStr,
											meal.userId)) {
										MyRecords records = new MyRecords();
										records.createDate = dateStr;
										records.recordId = "";
										records.type = 7;
										records.userId = meal.userId;
										MyRecordsDao.getInstance().insert(
												LoginActivity.this, records);
									}
								}
							}
						}
					}
				}

				ArrayList<Meal> locUpdateMealList = mealDao.getAllMeal(
						LoginActivity.this, profile.id, "2");

				for (Meal meal : addMealList) {
					// 判断本地数据是否有同步并且修改过数据的
					if (locUpdateMealList != null
							&& locUpdateMealList.size() > 0) {
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
										.getInstance().getExerciseById(
												LoginActivity.this,
												detail.activityId);
								detail.name = entity.name;

								exercise.exerciseDetail.add(detail);
							}
						} catch (Exception e) {
						}
						syncExerciseList.add(exercise);
					}
				} else {
					ArrayList<Exercise> locaExerciseList = activityDao
							.getSyncDelActivity(LoginActivity.this, profile.id);
					for (Exercise delExercise : locaExerciseList) {
						delExercise.syncId = 0;
						int count = activityDao.delActivity(LoginActivity.this,
								delExercise);
						if (count > 0) {
							MyRecordsDao.getInstance().delRecords(
									LoginActivity.this, delExercise.id, "2");
							Date date = dateFormat
									.parse(delExercise.createDate);
							String currentDate = dateSF.format(date);
							ArrayList<MyRecords> arrayList = MyRecordsDao
									.getInstance().getAllRecordByDate(
											LoginActivity.this, currentDate,
											profile.id);
							if (arrayList != null && arrayList.size() == 0) {
								MyRecordsDao.getInstance().delSummaryRecords(
										LoginActivity.this);
							}

						}
					}
				}

				ArrayList<Exercise> syncDelExerciseList = new ArrayList<Exercise>();
				ArrayList<Exercise> locaDelExerciseList = activityDao
						.getActivity(LoginActivity.this, "3", profile.id);
				for (Exercise exercise : syncExerciseList) {
					syncDelExerciseList.add(exercise);
					// 判断是否有同步并且修改过的数据
					if (locaDelExerciseList != null
							&& locaDelExerciseList.size() > 0) {
						for (Exercise delExercise : locaDelExerciseList) {
							// 如果数据不相同，进行添加。否则忽略
							if (delExercise.activityId
									.equals(exercise.activityId)) {
								syncDelExerciseList.remove(exercise);
							}
						}
					}
				}

				ArrayList<Exercise> locaExerciseList = activityDao.getActivity(
						LoginActivity.this, "1", profile.id);
				ArrayList<Exercise> addExerciseList = new ArrayList<Exercise>();

				for (Exercise exercise : syncDelExerciseList) {
					addExerciseList.add(exercise);
					// 判断本地数据是否有同步的
					if (locaExerciseList != null && locaExerciseList.size() > 0) {
						for (Exercise locaExercise : locaExerciseList) {
							// 判断数据是否相同，相同进行修改
							if (locaExercise.activityId
									.equals(exercise.activityId)) {
								addExerciseList.remove(exercise);
								exercise.id = locaExercise.id;
								int rowId = activityDao.updateActivity(
										LoginActivity.this, exercise);
								if (rowId > 0) {
									activityDetailDao.delRecordActivity(
											LoginActivity.this, exercise.id);
									for (ExerciseDetail detail : exercise.exerciseDetail) {
										detail.recordId = exercise.id;
										activityDetailDao.insert(
												LoginActivity.this, detail);
									}

									MyRecords info = new MyRecords();
									info.createDate = exercise.createDate;
									info.recordId = exercise.id;
									info.type = 2;

									MyRecordsDao.getInstance().updateRecords(
											LoginActivity.this, info);

									Date date = dateFormat
											.parse(exercise.createDate);
									String dateStr = dateSF.format(date);
									if (MyRecordsDao.getInstance().getRecord(
											LoginActivity.this, dateStr,
											exercise.userId)) {
										MyRecords records = new MyRecords();
										records.createDate = dateStr;
										records.recordId = "";
										records.type = 7;
										records.userId = exercise.userId;
										MyRecordsDao.getInstance().insert(
												LoginActivity.this, records);
									}
								}
							}
						}
					}
				}

				ArrayList<Exercise> locaUpdateExerciseList = activityDao
						.getActivity(LoginActivity.this, "2", profile.id);
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
				result = currentUser.bucket(Constants.MyMeasurement).query(
						query);
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
						}

						try {
							String image = kiiObject
									.getString(Constants.measurementImage2);
							String bitmap = ImagesUtils.decodeImageURl(image);
							if (bitmap != null) {
								measurement.image2 = bitmap;
							}
						} catch (Exception e1) {
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
							.getSyncDelMeasurement(LoginActivity.this,
									profile.id);
					for (Measurement delMeasurement : locaMeasurementList) {
						delMeasurement.syncId = 0;
						int count = measurementDao.delMeasurement(
								LoginActivity.this, delMeasurement);
						if (count > 0) {
							MyRecordsDao.getInstance().delRecords(
									LoginActivity.this, delMeasurement.id, "3");
							Date date = dateFormat
									.parse(delMeasurement.createDate);
							String currentDate = dateSF.format(date);
							ArrayList<MyRecords> arrayList = MyRecordsDao
									.getInstance().getAllRecordByDate(
											LoginActivity.this, currentDate,
											profile.id);
							if (arrayList != null && arrayList.size() == 0) {
								MyRecordsDao.getInstance().delSummaryRecords(
										LoginActivity.this);
							}

						}
					}
				}

				ArrayList<Measurement> syncDelMeasurementList = new ArrayList<Measurement>();
				ArrayList<Measurement> locaDelMeasurementList = measurementDao
						.getMeasurementBySyncId(LoginActivity.this, "3",
								profile.id);
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
						.getMeasurementBySyncId(LoginActivity.this, "1",
								profile.id);
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
										LoginActivity.this, measurement);

								if (rowId > 0) {

									MyRecords info = new MyRecords();
									info.createDate = measurement.createDate;
									info.recordId = measurement.id;
									info.type = 3;

									MyRecordsDao.getInstance().updateRecords(
											LoginActivity.this, info);

									Date date = dateFormat
											.parse(measurement.createDate);
									String dateStr = dateSF.format(date);
									if (MyRecordsDao.getInstance().getRecord(
											LoginActivity.this, dateStr,
											measurement.userId)) {
										MyRecords records = new MyRecords();
										records.createDate = dateStr;
										records.recordId = "";
										records.type = 7;
										records.userId = measurement.userId;
										MyRecordsDao.getInstance().insert(
												LoginActivity.this, records);
									}
								}
							}
						}
					}
				}

				ArrayList<Measurement> locaUpdateMeasurementList = measurementDao
						.getMeasurementBySyncId(LoginActivity.this, "2",
								profile.id);
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
						sleep.sleepEnd = kiiObject
								.getString(Constants.sleepEnd);

						syncSleep.add(sleep);
					}
				} else {
					ArrayList<Sleep> locaSleepList = sleepDao.getSyncDelSleep(
							LoginActivity.this, profile.id);
					for (Sleep sleep : locaSleepList) {
						sleep.syncId = 0;
						int count = sleepDao
								.delSleep(LoginActivity.this, sleep);
						if (count > 0) {
							MyRecordsDao.getInstance().delRecords(
									LoginActivity.this, sleep.id, "4");
							Date date = dateFormat.parse(sleep.createDate);
							String currentDate = dateSF.format(date);
							ArrayList<MyRecords> arrayList = MyRecordsDao
									.getInstance().getAllRecordByDate(
											LoginActivity.this, currentDate,
											profile.id);
							if (arrayList != null && arrayList.size() == 0) {
								MyRecordsDao.getInstance().delSummaryRecords(
										LoginActivity.this);
							}
						}
					}
				}

				ArrayList<Sleep> syncDelSleep = new ArrayList<Sleep>();
				ArrayList<Sleep> locaDelSleepList = sleepDao.getSleepBySyncId(
						LoginActivity.this, "3", profile.id);
				for (Sleep sleep : syncSleep) {
					syncDelSleep.add(sleep);
					if (locaDelSleepList != null && locaDelSleepList.size() > 0) {
						for (Sleep locaSleep : locaDelSleepList) {
							if (!locaSleep.sleepId.equals(sleep.sleepId)) {
								syncDelSleep.remove(sleep);
							}
						}
					}
				}

				ArrayList<Sleep> addSleepList = new ArrayList<Sleep>();
				ArrayList<Sleep> locaSleepList = sleepDao.getSleepBySyncId(
						LoginActivity.this, "1", profile.id);
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

				result = currentUser.bucket(Constants.MyDailycount)
						.query(query);
				objList = result.getResult();

				if (objList != null && objList.size() > 0) {
					for (KiiObject kiiObject : objList) {
						// kiiObject.delete();
						String yearMonth = kiiObject
								.getString(Constants.dailycountRecordDate);
						String dailycountWater = kiiObject
								.getString(Constants.dailycountWater);
						String dailycountBowel = kiiObject
								.getString(Constants.dailycountBowel);

						try {
							JSONArray arrayWater = new JSONArray(
									dailycountWater);
							for (int i = 0; i < arrayWater.length(); i++) {
								JSONObject json = arrayWater.getJSONObject(i);
								if (json != null && json.length() > 0) {
									WaterCount water = new WaterCount();
									water.userId = profile.id;
									water.yearMonth = yearMonth;
									try {
										water.waterDate = json
												.getString(Constants.waterCreateDate);
										water.syncId = Integer
												.valueOf(json
														.getString(Constants.waterSyncID));
									} catch (Exception e) {
										Iterator<String> iterator = json.keys();
										while (iterator.hasNext()) {
											water.waterDate = (String) iterator
													.next();
											water.syncId = Integer
													.valueOf(json
															.getString(water.waterDate));
										}
									}
									syncWater.add(water);
								}
							}
						} catch (Exception e1) {
						}

						try {
							JSONArray arrayBowel = new JSONArray(
									dailycountBowel);
							for (int i = 0; i < arrayBowel.length(); i++) {
								JSONObject json = arrayBowel.getJSONObject(i);
								if (json != null && json.length() > 0) {
									BowelCount bowel = new BowelCount();
									bowel.userId = profile.id;
									bowel.yearMonth = yearMonth;
									try {
										bowel.bowelDate = json
												.getString(Constants.bowelCreateDate);
										bowel.syncId = Integer
												.valueOf(json
														.getString(Constants.bowelSyncID));
									} catch (Exception e) {
										Iterator<String> iterator = json.keys();
										while (iterator.hasNext()) {
											bowel.bowelDate = (String) iterator
													.next();
											bowel.syncId = Integer
													.valueOf(json
															.getString(bowel.bowelDate));
										}
									}
									syncBowel.add(bowel);
								}
							}
						} catch (Exception e) {
						}
					}

				} else {
					ArrayList<WaterCount> locaWaterList = waterDao
							.getSyncDelWaterCount(LoginActivity.this,
									profile.id);
					for (WaterCount waterCount : locaWaterList) {
						waterCount.syncId = 0;
						int count = waterDao.delWater(LoginActivity.this,
								waterCount);
						if (count > 0) {
							MyRecordsDao.getInstance().delRecords(
									LoginActivity.this, waterCount.id, "5");
							Date date = dateFormat.parse(waterCount.waterDate);
							String currentDate = dateSF.format(date);
							ArrayList<MyRecords> arrayList = MyRecordsDao
									.getInstance().getAllRecordByDate(
											LoginActivity.this, currentDate,
											profile.id);
							if (arrayList != null && arrayList.size() == 0) {
								MyRecordsDao.getInstance().delSummaryRecords(
										LoginActivity.this);
							}
						}
					}
					ArrayList<BowelCount> locaBowelList = bowelDao
							.getSyncDelBowelCount(LoginActivity.this,
									profile.id);
					for (BowelCount bowelCount : locaBowelList) {
						bowelCount.syncId = 0;
						int count = bowelDao.delBowel(LoginActivity.this,
								bowelCount);
						if (count > 0) {
							MyRecordsDao.getInstance().delRecords(
									LoginActivity.this, bowelCount.id, "6");
							Date date = dateFormat.parse(bowelCount.bowelDate);
							String currentDate = dateSF.format(date);
							ArrayList<MyRecords> arrayList = MyRecordsDao
									.getInstance().getAllRecordByDate(
											LoginActivity.this, currentDate,
											profile.id);
							if (arrayList != null && arrayList.size() == 0) {
								MyRecordsDao.getInstance().delSummaryRecords(
										LoginActivity.this);
							}
						}
					}
				}

				ArrayList<WaterCount> syncDelWater = new ArrayList<WaterCount>();
				ArrayList<WaterCount> locaDelWaterList = waterDao
						.getWaterCountBySyncId(LoginActivity.this, "3",
								profile.id);
				for (WaterCount waterCount : syncWater) {
					if (locaDelWaterList != null && locaDelWaterList.size() > 0) {
						for (WaterCount locaWaterCount : locaDelWaterList) {
							if (!locaWaterCount.waterDate
									.equals(waterCount.waterDate)) {
								syncDelWater.add(waterCount);
							}
						}
					} else {
						syncDelWater.add(waterCount);
					}
				}

				
				ArrayList<WaterCount> addWaterList = new ArrayList<WaterCount>();
				ArrayList<WaterCount> locaWaterList = waterDao
						.getWaterCountBySyncId(LoginActivity.this, "1", profile.id);
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
						.getBowelCountBySyncId(LoginActivity.this, "3",
								profile.id);
				for (BowelCount bowelCount : syncBowel) {
					if (locaDelBowelList != null && locaDelBowelList.size() > 0) {
						for (BowelCount locaBowelCount : locaDelBowelList) {
							if (!locaBowelCount.bowelDate
									.equals(bowelCount.bowelDate)) {
								syncDelBowelList.add(bowelCount);
							}
						}
					} else {
						syncDelBowelList.add(bowelCount);
					}
				}

				ArrayList<BowelCount> addBowelList = new ArrayList<BowelCount>();
				ArrayList<BowelCount> locaBowelList = bowelDao
						.getBowelCountBySyncId(LoginActivity.this, "1", profile.id);
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

			} catch (Exception e) {
				Log.i(TAG, e + "----" + e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (proDialog != null && proDialog.isShowing()) {
				proDialog.dismiss();
			}

			SharedPreferences sp = getSharedPreferences(
					Constants.BACKUP_RECORDS, MODE_PRIVATE);
			Editor editor = sp.edit();
			Date date = new Date();
			String currentDate = dateFormat.format(date);
			editor.putString(Constants.SYNC_DATE, currentDate);
			editor.commit();
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}

	class GetConfigTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			SharedPreferences sp = getSharedPreferences(Constants.KIIUSER,
					MODE_PRIVATE);
			try {
				Boolean qty = sp.getBoolean(Constants.QTY, false);
				if (!qty) {
					Editor editor = sp.edit();
					editor.putBoolean(Constants.QTY, true);
					editor.commit();

					InputStream inputStream = LoginActivity.this.getAssets()
							.open("food.xml");
					List<Food> foods = XmlPullParserMessage.getFoods(inputStream);
					FoodDao.getInstance().bulkInsert(LoginActivity.this,
							(ArrayList<Food>) foods);

					inputStream = LoginActivity.this.getAssets().open(
							"activity.xml");
					List<ActivityEntity> list = XmlPullParserMessage
							.getActivityEntity(inputStream);
					ActivityDao.getInstance().bulkInsert(LoginActivity.this,
							(ArrayList<ActivityEntity>) list);

					inputStream = LoginActivity.this.getAssets()
							.open("ana.xml");
					List<Summary> summaryList = XmlPullParserMessage
							.getSummary(inputStream);
					SummaryDao.getInstance().bulkInsert(LoginActivity.this,
							(ArrayList<Summary>) summaryList);
				}
			} catch (Exception e) {
			}

			return null;
		}

	}

	private void addMeal(Meal meal) {
		if (mealDao.insert(LoginActivity.this, meal)) {
			MyRecords info = new MyRecords();
			info.createDate = meal.createDate;
			meal.id = mealDao.getMealId(LoginActivity.this, meal.mealId,
					meal.userId);
			info.recordId = meal.id;
			info.type = 1;
			info.userId = meal.userId;

			MyRecordsDao.getInstance().insert(LoginActivity.this, info);

			for (MealDetail detail : meal.mealDetail) {
				detail.recordId = meal.id;
				mealDetailDao.insert(LoginActivity.this, detail);
			}

			try {
				Date date = dateFormat.parse(meal.createDate);
				String dateStr = dateSF.format(date);
				if (MyRecordsDao.getInstance().getRecord(LoginActivity.this,
						dateStr, meal.userId)) {
					MyRecords records = new MyRecords();
					records.createDate = dateStr;
					records.recordId = "";
					records.type = 7;
					records.userId = meal.userId;
					MyRecordsDao.getInstance().insert(LoginActivity.this,
							records);
				}
			} catch (Exception e) {

			}
		}

	}

	private void addExercise(Exercise exercise) {
		if (activityDao.insert(LoginActivity.this, exercise)) {
			MyRecords info = new MyRecords();
			info.createDate = exercise.createDate;
			exercise.id = activityDao.getActivityId(LoginActivity.this,
					exercise.activityId, exercise.userId);
			info.recordId = exercise.id;
			info.type = 2;
			info.userId = exercise.userId;

			MyRecordsDao.getInstance().insert(LoginActivity.this, info);

			for (ExerciseDetail detail : exercise.exerciseDetail) {
				detail.recordId = exercise.id;
				activityDetailDao.insert(LoginActivity.this, detail);
			}

			try {
				Date date = dateFormat.parse(exercise.createDate);
				String dateStr = dateSF.format(date);
				if (MyRecordsDao.getInstance().getRecord(LoginActivity.this,
						dateStr, exercise.userId)) {
					MyRecords records = new MyRecords();
					records.createDate = dateStr;
					records.recordId = "";
					records.type = 7;
					records.userId = exercise.userId;
					MyRecordsDao.getInstance().insert(LoginActivity.this,
							records);
				}
			} catch (Exception e) {

			}
		}

	}

	private void addMeasurement(Measurement measurement) {
		if (measurementDao.insert(LoginActivity.this, measurement)) {
			MyRecords records = new MyRecords();
			records.createDate = measurement.createDate;
			String recordId = measurementDao.getMeasurementId(
					LoginActivity.this, measurement.measurementId,
					measurement.userId);
			records.recordId = recordId;
			records.type = 3;
			records.userId = measurement.userId;

			MyRecordsDao.getInstance().insert(LoginActivity.this, records);

			Date currentDate = null;
			try {
				currentDate = dateFormat.parse(measurement.createDate);
				String dateStr = dateSF.format(currentDate);
				if (MyRecordsDao.getInstance().getRecord(LoginActivity.this,
						dateStr, measurement.userId)) {
					MyRecords myRecords = new MyRecords();
					myRecords.createDate = dateStr;
					myRecords.recordId = "";
					myRecords.userId = measurement.userId;
					myRecords.type = 7;
					MyRecordsDao.getInstance().insert(LoginActivity.this,
							myRecords);
				}
			} catch (Exception e) {
			}

		}
	}

	private void addSleep(Sleep sleep) {
		if (sleepDao.insert(LoginActivity.this, sleep)) {
			MyRecords info = new MyRecords();
			info.createDate = sleep.createDate;
			info.type = 4;
			info.recordId = sleepDao.getSleepId(LoginActivity.this,
					sleep.sleepId, sleep.userId);
			info.userId = sleep.userId;

			MyRecordsDao.getInstance().insert(LoginActivity.this, info);

			Date currentDate = null;
			try {
				currentDate = dateFormat.parse(sleep.createDate);
				String dateStr = dateSF.format(currentDate);
				if (MyRecordsDao.getInstance().getRecord(LoginActivity.this,
						dateStr, sleep.userId)) {
					MyRecords myRecords = new MyRecords();
					myRecords.createDate = dateStr;
					myRecords.recordId = "";
					myRecords.type = 7;
					myRecords.userId = sleep.userId;
					MyRecordsDao.getInstance().insert(LoginActivity.this,
							myRecords);
				}
			} catch (Exception e) {
			}
		}
	}

	private void addWater(WaterCount water) {
		if (waterDao.insert(LoginActivity.this, water)) {
			MyRecords info = new MyRecords();
			info.createDate = water.waterDate;
			info.type = 5;
			info.recordId = waterDao.getWaterCountId(LoginActivity.this,
					water.waterDate, water.userId);
			info.userId = water.userId;

			MyRecordsDao.getInstance().insert(LoginActivity.this, info);

			if (MyRecordsDao.getInstance().getRecord(LoginActivity.this,
					water.yearMonth, water.userId)) {
				MyRecords records = new MyRecords();
				records.createDate = water.yearMonth;
				records.recordId = "";
				records.type = 7;
				records.userId = water.userId;
				MyRecordsDao.getInstance().insert(LoginActivity.this, records);
			}
		}
	}

	private void addBowel(BowelCount bowel) {
		if (bowelDao.insert(LoginActivity.this, bowel)) {
			MyRecords info = new MyRecords();
			info.createDate = bowel.bowelDate;
			info.type = 6;
			info.recordId = bowelDao.getBowelCountId(LoginActivity.this,
					bowel.bowelDate, bowel.userId);
			info.userId = bowel.userId;

			MyRecordsDao.getInstance().insert(LoginActivity.this, info);

			if (MyRecordsDao.getInstance().getRecord(LoginActivity.this,
					bowel.yearMonth, bowel.userId)) {
				MyRecords records = new MyRecords();
				records.createDate = bowel.yearMonth;
				records.recordId = "";
				records.type = 7;
				records.userId = bowel.userId;
				MyRecordsDao.getInstance().insert(LoginActivity.this, records);
			}
		}
	}
}
