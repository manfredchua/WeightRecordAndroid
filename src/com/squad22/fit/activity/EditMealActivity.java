package com.squad22.fit.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
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
import com.squad22.fit.dao.MealDetailDao;
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.dialogfragment.DateDialogFragment;
import com.squad22.fit.dialogfragment.TimeDialogFragment;
import com.squad22.fit.entity.Food;
import com.squad22.fit.entity.Meal;
import com.squad22.fit.entity.MealDetail;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.task.SyncMealTask;
import com.squad22.fit.task.SyncUpdateMealTask;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.ImagesUtils;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class EditMealActivity extends Activity implements OnClickListener {

	private static final String TAG = "EditMealActivity";
	ActionBar actionBar;
	ImageView ivCamera;
	ImageView ivFoodPhoto;
	ImageView ivEdit;
	ImageView ivAdd;
	EditText etDate;
	EditText etTime;
	LinearLayout llContent;
	EditText etTitle;
	String imgbm;
	Meal meal = new Meal();
	ImageView ivDel;
	ImageView ivLine = null;
	boolean isEdit = false;
	View view = null;
	int index = 0;
	Calendar mCalendar = Calendar.getInstance();
	boolean isName = false;
	Food food = new Food();
	Food foodNumber = new Food();
	ArrayList<String> units = new ArrayList<String>();
	ArrayList<String> weight = new ArrayList<String>();
	ArrayList<Food> numberList = new ArrayList<Food>();
	ArrayList<Food> foodList = new ArrayList<Food>();
	ArrayList<ImageView> arrayList = new ArrayList<ImageView>();
	ArrayList<View> arrayView = new ArrayList<View>();
	ArrayList<ImageView> arrayLine = new ArrayList<ImageView>();
	ArrayList<MealDetail> foods = new ArrayList<MealDetail>();
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat zhSF = new SimpleDateFormat("yyyy年MM月dd日");
	SimpleDateFormat timeSF = new SimpleDateFormat("HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setActionBar();

		setContentView(R.layout.record_meal_layout);

		initView();
		meal = (Meal) getIntent().getSerializableExtra(Constants.MEAL);

		setValue(meal);
	}

	private void initView() {
		ivCamera = (ImageView) findViewById(R.id.iv_camera);
		ivFoodPhoto = (ImageView) findViewById(R.id.iv_food_photo);
		ivEdit = (ImageView) findViewById(R.id.iv_edit);
		ivAdd = (ImageView) findViewById(R.id.iv_add);
		etTitle = (EditText) findViewById(R.id.et_title);
		llContent = (LinearLayout) findViewById(R.id.ll_content);
		etDate = (EditText) findViewById(R.id.et_date);
		etTime = (EditText) findViewById(R.id.et_time);

		ivCamera.setOnClickListener(this);
		ivFoodPhoto.setOnClickListener(this);
		ivEdit.setOnClickListener(this);
		ivAdd.setOnClickListener(this);
		etDate.setOnClickListener(this);
		etTime.setOnClickListener(this);
	}

	private void setValue(Meal meal) {
		try {

			if (meal.image != null && meal.image.length() > 0) {
				ivCamera.setVisibility(View.GONE);
				Bitmap bm = ImagesUtils.locDecodeImage(meal.image);

				if (bm != null) {
					byte[] image = ImagesUtils.convertImageToByte(bm);
					bm = BitmapFactory.decodeByteArray(image, 0, image.length);
					ivFoodPhoto.setImageBitmap(bm);
				} else {
					ivCamera.setVisibility(View.VISIBLE);
				}
			} else {
				ivCamera.setVisibility(View.VISIBLE);
			}

			etTitle.setText(meal.title);
			Date date = sf.parse(meal.createDate);
			mCalendar.setTime(date);

			etDate.setText(zhSF.format(date));
			etDate.setTag(dateSF.format(date));
			etTime.setText(timeSF.format(date));

			ArrayList<MealDetail> mealList = MealDetailDao.getInstance()
					.getMealDetailById(this, meal.id);
			for (MealDetail detail : mealList) {
				View foodView = getLayoutInflater().inflate(
						R.layout.meal_item_layout, null);
				final TextView etPortion = (TextView) foodView
						.findViewById(R.id.et_portion);
				TextView txtFoodName = (TextView) foodView
						.findViewById(R.id.txt_food_name);
				TextView txtQty = (TextView) foodView
						.findViewById(R.id.txt_qty);
				ivDel = (ImageView) foodView.findViewById(R.id.iv_del);
				foodView.setTag(detail.name);

				arrayList.add(ivDel);
				arrayView.add(foodView);

				etPortion.setText(detail.portion);

				txtFoodName.setText(detail.name);
				if (detail.calorie != null && !detail.calorie.equals("")) {
					if (detail.calorie.equals("0")) {
						txtQty.setText(detail.Amount + detail.unit + "(-卡路里)");
					} else {
						txtQty.setText(detail.Amount + detail.unit + "("
								+ detail.calorie + "卡路里)");
					}
				} else {
					txtQty.setText(detail.Amount + detail.unit + "(-卡路里)");
				}
				foods.add(detail);
				llContent.addView(foodView);

				ivLine = new ImageView(this);
				ivLine.setBackgroundResource(R.drawable.divider_grey_top);
				llContent.addView(ivLine);
				arrayLine.add(ivLine);
			}

		} catch (Exception e) {

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_camera:
		case R.id.iv_food_photo:
			CommUtils.showPhoto(this);
			break;
		case R.id.iv_edit:
			if (isEdit) {

				if (llContent != null && llContent.getChildCount() > 0) {
					isEdit = false;
					for (ImageView ivDel : arrayList) {
						ivDel.setVisibility(View.GONE);
					}
				}
			} else {
				if (llContent != null && llContent.getChildCount() > 0) {
					isEdit = true;
					index = 0;
					for (final ImageView ivDel : arrayList) {
						ivDel.setVisibility(View.VISIBLE);
						final View view = arrayView.get(index);

						ivDel.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {

								llContent.removeView(view);
								arrayView.remove(view);

								MealDetail entity = null;
								for (MealDetail food : foods) {
									if (view.getTag().equals(food.name)) {
										entity = food;
									}
								}

								if (entity != null) {
									foods.remove(entity);
								}
								if (llContent.getChildCount() > 1) {
									try {
										final ImageView ivLine = arrayLine
												.get(arrayLine.size() - 1);
										llContent.removeView(ivLine);
										arrayLine.remove(ivLine);
									} catch (Exception e) {
									}
								}
								arrayList.remove(ivDel);
							}
						});
						index++;
					}
				}
			}
			break;
		case R.id.iv_add:
			Intent intent = new Intent(this, SearchFoodActivity.class);
			startActivityForResult(intent, Constants.SUCCESS);
			break;
		case R.id.et_date:
			int curr_year = mCalendar.get(Calendar.YEAR);
			int curr_month = mCalendar.get(Calendar.MONTH);
			int curr_day = mCalendar.get(Calendar.DATE);
			DateDialogFragment newFragment = new DateDialogFragment(curr_year,
					curr_month, curr_day, handler);
			newFragment.show(getFragmentManager(), TAG);
			break;
		case R.id.et_time:
			int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = mCalendar.get(Calendar.MINUTE);
			TimeDialogFragment timeFragment = new TimeDialogFragment(hour,
					minute, handler);
			timeFragment.show(getFragmentManager(), TAG);
			break;
		default:
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Bundle data = msg.getData();
				etDate.setText(data.getString("createDate"));
				String currentDate = data.getString(Constants.CURRENT_DATE);
				etDate.setTag(currentDate);

				currentDate = currentDate + " " + etTime.getText().toString();
				try {
					Date date = sf.parse(currentDate);
					mCalendar.setTime(date);
				} catch (ParseException e) {
				}
				break;
			case 2:
				etTime.setText(String.valueOf(msg.obj));
				currentDate = etDate.getTag().toString();
				currentDate = currentDate + " " + String.valueOf(msg.obj);
				try {
					Date date = sf.parse(currentDate);
					mCalendar.setTime(date);
				} catch (ParseException e) {
				}
				break;
			default:
				break;
			}
		};
	};

	private void setLinearLayout(Food food) {

		view = getLayoutInflater().inflate(R.layout.meal_item_layout, null);
		final TextView etPortion = (TextView) view
				.findViewById(R.id.et_portion);
		TextView txtFoodName = (TextView) view.findViewById(R.id.txt_food_name);
		TextView txtQty = (TextView) view.findViewById(R.id.txt_qty);
		ivDel = (ImageView) view.findViewById(R.id.iv_del);
		view.setTag(food.name);

		arrayList.add(ivDel);
		arrayView.add(view);

		etPortion.setText(food.portion);

		txtFoodName.setText(food.name);
		if (food.kcal != null && !food.kcal.equals("")) {
			if (food.kcal.equals("0")) {
				txtQty.setText(food.qty + food.unit + "(-卡路里)");
			} else {
				txtQty.setText(food.qty + food.unit + "(" + food.kcal + "卡路里)");
			}
		} else {
			txtQty.setText(food.qty + food.unit + "(-卡路里)");
		}

		MealDetail detail = new MealDetail();
		detail.foodId = food.foodId;
		detail.name = food.name;
		detail.Category = food.category;
		detail.Amount = food.qty;
		detail.portion = etPortion.getText().toString().trim();
		detail.calorie = food.kcal;
		detail.unit = food.unit;

		foods.add(detail);

		llContent.addView(view);

		ivLine = new ImageView(this);
		ivLine.setBackgroundResource(R.drawable.divider_grey_top);
		llContent.addView(ivLine);
		arrayLine.add(ivLine);
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

						meal.image = localFile.getAbsolutePath();
						ivCamera.setVisibility(View.GONE);
						ivFoodPhoto.setImageBitmap(photo);
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

					meal.image = localFile.getAbsolutePath();
					ivCamera.setVisibility(View.GONE);
					ivFoodPhoto.setImageBitmap(photo);
				}
			}

			break;
		case Constants.SUCCESS:
			if (data != null) {
				Food food = (Food) data.getSerializableExtra(Constants.FOOD);
				setLinearLayout(food);
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
						meal.title = etTitle.getText().toString().trim();

						if (meal.title.equals("") && meal.title != null) {
							meal.title = "饮食记录";
						}

						if (foods != null && foods.size() == 0) {
							CommUtils.showToast(EditMealActivity.this,
									getString(R.string.add_meal));
						} else {

							String dateStr = etDate.getTag().toString();
							String time = etTime.getText().toString();
							String dateTime = dateStr + " " + time;

							meal.createDate = dateTime;

							if (meal.syncId == 1) {
								meal.syncId = 2;
							}

							int result = MyMealDao.getInstance().updateMeal(
									EditMealActivity.this, meal);
							if (result > 0) {
								MyRecords info = new MyRecords();
								info.createDate = meal.createDate;
								info.recordId = meal.id;
								info.type = 1;

								MyRecordsDao.getInstance().updateRecords(
										EditMealActivity.this, info);

								MealDetailDao.getInstance().delMealDetail(
										EditMealActivity.this, meal.id);

								for (MealDetail detail : foods) {
									detail.recordId = meal.id;
									MealDetailDao.getInstance().insert(
											EditMealActivity.this, detail);
								}

								if (MyRecordsDao.getInstance().getRecord(
										EditMealActivity.this, dateStr,
										meal.userId)) {
									MyRecords records = new MyRecords();
									records.createDate = dateStr;
									records.recordId = "";
									records.type = 7;
									records.userId = meal.userId;
									MyRecordsDao.getInstance().insert(
											EditMealActivity.this, records);
								}

								SharedPreferences sp = getSharedPreferences(
										Constants.BACKUP_RECORDS, MODE_PRIVATE);
								boolean isRecord = sp.getBoolean(
										Constants.IS_RECORD, false);
								if (isRecord) {
									if (CommUtils
											.checkNetwork(EditMealActivity.this)) {
										String network = sp.getString(
												Constants.WIFI, "wifi");
										if (network.equals("wifi")) {
											if (CommUtils
													.isWIFIAvailable(EditMealActivity.this)) {
												if (meal.syncId == 0) {
													new SyncMealTask(
															EditMealActivity.this,
															meal).execute();
												} else {
													new SyncUpdateMealTask(
															EditMealActivity.this,
															meal).execute();
												}
											} else if (CommUtils
													.isWIFIAvailable(EditMealActivity.this)
													|| CommUtils
															.is3GAvailable(EditMealActivity.this)) {
												if (meal.syncId == 0) {
													new SyncMealTask(
															EditMealActivity.this,
															meal).execute();
												} else {
													new SyncUpdateMealTask(
															EditMealActivity.this,
															meal).execute();
												}
											}
										}
									}
								}
								CommUtils.showToast(EditMealActivity.this,
										"饮食记录修改成功");
								Intent intent = new Intent(
										EditMealActivity.this,
										MainActivity.class);
								startActivity(intent);
								finish();

							} else {
								CommUtils.showToast(EditMealActivity.this,
										"饮食记录修改失败");
							}

						}

					}
				});
		customActionBarView.findViewById(R.id.actionbar_cancel)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CommUtils.showAlertDialog(EditMealActivity.this);
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
