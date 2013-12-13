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
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.ImagesUtils;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak", "ServiceCast" })
public class RecordMealActivity extends Activity implements OnClickListener {

	private static final String TAG = "RecordMealActivity";
	ActionBar actionBar;
	ImageView ivCamera;
	ImageView ivFoodPhoto;
	ImageView ivEdit;
	ImageView ivAdd;
	LinearLayout llContent;
	EditText etTitle;
	EditText etDate;
	EditText etTime;
	String imgbm;
	Meal meal = new Meal();
	ImageView ivDel;
	ImageView ivLine = null;
	boolean isEdit = false;
	View view = null;
	int index = 0;
	Calendar mCalendar;
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

		mCalendar = Calendar.getInstance();

		initView();

		units.add("毫升");
		units.add("升");
		units.add("杯");
		units.add("汤匙");
		units.add("茶匙");
		units.add("品脱");

		weight.add("公斤");
		weight.add("英镑");
		weight.add("盎司");
		weight.add("克");
		weight.add("斤");

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

		ivAdd.getBackground().setAlpha(100);
		ivAdd.invalidate();

		ivEdit.getBackground().setAlpha(100);
		ivEdit.invalidate();

		ivCamera.setOnClickListener(this);
		ivFoodPhoto.setOnClickListener(this);
		ivEdit.setOnClickListener(this);
		ivAdd.setOnClickListener(this);
		etDate.setOnClickListener(this);
		etTime.setOnClickListener(this);

		etDate.setText(zhSF.format(mCalendar.getTime()));
		etDate.setTag(dateSF.format(mCalendar.getTime()));
		etTime.setText(timeSF.format(mCalendar.getTime()));
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

								MealDetail entity = null;
								for (MealDetail food : foods) {
									if (view.getTag().equals(food.name)) {
										entity = food;
									}
								}

								llContent.removeView(view);
								arrayView.remove(view);

								if (entity != null) {
									foods.remove(entity);
								}
								if (llContent.getChildCount() > 0) {
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

	private void setLinearLayout(final Food food) {

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
		ivLine.setBackgroundResource(R.drawable.divider_greyline);
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

				Log.d(TAG, "uri: " + uri.toString());
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

						int random = (int) (Math.random() * 10000);
						meal.mealId = "a" + random + "s";

						String userId = CommUtils
								.getUserId(RecordMealActivity.this);
						meal.userId = userId;

						meal.title = etTitle.getText().toString().trim();

						if (meal.title.equals("")) {
							meal.title = "饮食记录";
						}

						if (foods != null && foods.size() == 0) {
							CommUtils.showToast(RecordMealActivity.this,
									getString(R.string.add_meal));
						} else {

							String dateStr = etDate.getTag().toString();
							String time = etTime.getText().toString();
							String dateTime = dateStr + " " + time;

							meal.createDate = dateTime;
							if (MyMealDao.getInstance().insert(
									RecordMealActivity.this, meal)) {
								MyRecords info = new MyRecords();
								info.createDate = meal.createDate;
								meal.id = MyMealDao.getInstance().getMealId(
										RecordMealActivity.this, meal.mealId,
										meal.userId);
								info.recordId = meal.id;
								info.type = 1;
								info.userId = meal.userId;

								MyRecordsDao.getInstance().insert(
										RecordMealActivity.this, info);

								for (MealDetail detail : foods) {
									detail.recordId = meal.id;
									MealDetailDao.getInstance().insert(
											RecordMealActivity.this, detail);
								}

								if (MyRecordsDao.getInstance().getRecord(
										RecordMealActivity.this, dateStr,
										meal.userId)) {
									MyRecords records = new MyRecords();
									records.createDate = dateStr;
									records.recordId = "";
									records.type = 7;
									records.userId = meal.userId;
									MyRecordsDao.getInstance().insert(
											RecordMealActivity.this, records);
								}
								if (CommUtils
										.isAutoSync(RecordMealActivity.this)) {
									new SyncMealTask(RecordMealActivity.this,
											meal).execute();
								}

								CommUtils.showToast(RecordMealActivity.this,
										"饮食记录保存成功");

								Intent intent = new Intent(
										RecordMealActivity.this,
										MainActivity.class);
								startActivity(intent);
								RecordMealActivity.this.finish();

							} else {
								CommUtils.showToast(RecordMealActivity.this,
										"饮食记录保存失败");
							}

						}
					}
				});
		customActionBarView.findViewById(R.id.actionbar_cancel)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CommUtils.showAlertDialog(RecordMealActivity.this);
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
