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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.dao.ActivityDetailDao;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.dialogfragment.DateDialogFragment;
import com.squad22.fit.dialogfragment.TimeDialogFragment;
import com.squad22.fit.entity.ActivityEntity;
import com.squad22.fit.entity.Exercise;
import com.squad22.fit.entity.ExerciseDetail;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.DensityUtil;
import com.squad22.fit.utils.ImagesUtils;
import com.squad22.fit.wheelview.NumericWheelAdapter;
import com.squad22.fit.wheelview.WheelView;
import com.squad22.fit.task.SyncExerciseTask;

;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class RecordExerciseActivity extends Activity implements OnClickListener {

	private static final String TAG = null;
	ActionBar actionBar;
	ImageView ivCamera;
	ImageView ivExercisePhoto;
	ImageView ivEdit;
	ImageView ivAdd;
	LinearLayout llContent;
	EditText etTitle;
	EditText etDate;
	EditText etTime;
	String imgbm;
	Exercise exercise = new Exercise();
	ImageView ivDel;
	ImageView ivLine = null;
	boolean isEdit = false;
	View view = null;
	int index = 0;
	Calendar mCalendar = Calendar.getInstance();
	ActivityEntity entity = new ActivityEntity();
	ArrayList<String> minuteList = new ArrayList<String>();
	ArrayList<ActivityEntity> contains = new ArrayList<ActivityEntity>();
	ArrayList<ActivityEntity> activityList = new ArrayList<ActivityEntity>();
	ArrayList<EditText> timeList = new ArrayList<EditText>();
	ArrayList<TextView> kcalList = new ArrayList<TextView>();
	ArrayList<ImageView> arrayList = new ArrayList<ImageView>();
	ArrayList<View> arrayView = new ArrayList<View>();
	ArrayList<ImageView> arrayLine = new ArrayList<ImageView>();
	ArrayList<ExerciseDetail> detailList = new ArrayList<ExerciseDetail>();
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat zhSF = new SimpleDateFormat("yyyy年MM月dd日");
	SimpleDateFormat timeSF = new SimpleDateFormat("HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setActionBar();

		setContentView(R.layout.record_exercise_layout);

		initView();

		for (int i = 0; i < 181; i++) {
			minuteList.add(i + "分钟");
		}
	}

	private void initView() {
		ivCamera = (ImageView) findViewById(R.id.iv_camera);
		ivExercisePhoto = (ImageView) findViewById(R.id.iv_exercise_photo);
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
		ivExercisePhoto.setOnClickListener(this);
		ivEdit.setOnClickListener(this);
		ivAdd.setOnClickListener(this);
		etDate.setOnClickListener(this);
		etTime.setOnClickListener(this);

		etDate.setText(zhSF.format(mCalendar.getTime()));
		etDate.setTag(dateSF.format(mCalendar.getTime()));
		etTime.setText(timeSF.format(mCalendar.getTime()));

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_camera:
		case R.id.iv_exercise_photo:
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
								arrayList.remove(ivDel);
								ExerciseDetail entity = null;
								for (ExerciseDetail detail : detailList) {
									if (view.getTag().equals(detail.name)) {
										entity = detail;
									}
								}

								if (entity != null) {
									detailList.remove(entity);
								}

								EditText time = null;
								for (EditText etTime : timeList) {
									if (etTime.getTag().equals(view.getTag())) {
										time = etTime;
									}
								}

								if (time != null) {
									timeList.remove(time);
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
							}
						});
						index++;
					}
				}
			}
			break;
		case R.id.iv_add:
			Intent intent = new Intent(this, SearchExerciseActivity.class);
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

	private void setLinearLayout(final ActivityEntity entity) {
		view = getLayoutInflater().inflate(R.layout.activity_item_layout, null);
		final EditText etTime = (EditText) view.findViewById(R.id.et_time);
		TextView txtActivityName = (TextView) view
				.findViewById(R.id.txt_activity_name);
		final TextView txtKcal = (TextView) view.findViewById(R.id.txt_kcal);
		ivDel = (ImageView) view.findViewById(R.id.iv_del);
		view.setTag(entity.name);
		etTime.setTag(entity.name);
		txtKcal.setTag(entity.name);

		arrayList.add(ivDel);
		arrayView.add(view);
		timeList.add(etTime);
		kcalList.add(txtKcal);

		etTime.setText("60分钟");
		txtActivityName.setText(entity.name);
		txtKcal.setText(entity.targetKcal + "卡路里");

		ExerciseDetail detail = new ExerciseDetail();
		detail.activityId = entity.activityId;
		detail.name = entity.name;
		detail.calorie = entity.targetKcal;
		detail.time = etTime.getText().toString().trim();

		detailList.add(detail);

		llContent.addView(view);

		ivLine = new ImageView(this);
		ivLine.setBackgroundResource(R.drawable.divider_grey_top);
		llContent.addView(ivLine);
		arrayLine.add(ivLine);

		etTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String times = etTime.getText().toString().trim();
				String timeStr = (String) times.subSequence(0,
						times.length() - 2);
				int time = Integer.valueOf(timeStr);
				showSetDateTime(RecordExerciseActivity.this, time,
						entity.targetKcal, etTime, txtKcal);
			}
		});
	}

	@SuppressWarnings("deprecation")
	public static void showSetDateTime(final Context context, final int time,
			final String kcal, final EditText etTime, final TextView txtKcal) {

		View view = LayoutInflater.from(context).inflate(
				R.layout.activity_time_layout, null);

		int textSize = 0;
		textSize = CommUtils.adjustFontSize(((Activity) context).getWindow()
				.getWindowManager());
		final WheelView wvNumber = (WheelView) view
				.findViewById(R.id.wv_number);
		wvNumber.setAdapter(new NumericWheelAdapter(0, 180));//
		wvNumber.setCyclic(true);//
		wvNumber.setLabel("分钟");//
		wvNumber.setCurrentItem(time);
		wvNumber.TEXT_SIZE = textSize;

		final PopupWindow pw = new PopupWindow(view, DensityUtil.dip2px(
				context, 120), DensityUtil.dip2px(context, 230), true);
		final OnTimeSetListener mCallBack = new OnTimeSetListener() {

			@Override
			public void onDateTimeSet(int currentTime) {

				double targetTime = currentTime / 60.0;
				double target = targetTime * Double.parseDouble(kcal);

				etTime.setText(currentTime + "分钟");
				txtKcal.setText(CommUtils.getString(target) + "卡路里");
			}
		};

		Button btnConfirm = (Button) view.findViewById(R.id.confirm);

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCallBack != null) {
					int time = wvNumber.getCurrentItem();
					mCallBack.onDateTimeSet(time);
				}
				if (pw != null && pw.isShowing()) {
					pw.dismiss();
				}
			}
		});
		pw.setBackgroundDrawable(new BitmapDrawable());
		pw.showAsDropDown(etTime, 0, 0);
	}

	public interface OnTimeSetListener {
		void onDateTimeSet(int time);
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

						exercise.image = localFile.getAbsolutePath();
						ivCamera.setVisibility(View.GONE);
						ivExercisePhoto.setImageBitmap(photo);
					}
				}
			}
			break;

		case Constants.TAKE_A_PICTURE:
			// 图库中图片路径
			imgbm = CommUtils.doCropPhoto(RecordExerciseActivity.this);
			if (imgbm != null) {
				Bitmap photo = ImagesUtils.decodeImage(imgbm);
				if (photo != null) {
					byte[] photos = ImagesUtils.convertImageToByte(photo);
					String path = imgbm
							.substring(imgbm.lastIndexOf("/") + 1);
					File localFile = ImagesUtils.getFileFromBytes(photos,
							Constants.DOWNLOAD_PHOTO_PATH + "" + path);

					exercise.image = localFile.getAbsolutePath();
					ivCamera.setVisibility(View.GONE);
					ivExercisePhoto.setImageBitmap(photo);
				}
			}
			break;
		case Constants.SUCCESS:
			if (data != null) {
				ActivityEntity entity = (ActivityEntity) data
						.getSerializableExtra(Constants.ACTIVITY);
				setLinearLayout(entity);
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

						String userId = CommUtils
								.getUserId(RecordExerciseActivity.this);
						exercise.title = etTitle.getText().toString().trim();
						exercise.userId = userId;

						if (exercise.title.equals("")) {
							exercise.title = "运动记录";
						}
						if (detailList != null && detailList.size() == 0) {
							CommUtils.showToast(RecordExerciseActivity.this,
									getString(R.string.add_exercise));
						} else {

							String dateStr = etDate.getTag().toString();
							String timeStr = etTime.getText().toString();
							String dateTime = dateStr + " " + timeStr;

							exercise.createDate = dateTime;
							int random = (int) (Math.random() * 10000);

							exercise.activityId = "e" + random + "s";

							if (MyActivityDao.getInstance().insert(
									RecordExerciseActivity.this, exercise)) {
								MyRecords info = new MyRecords();
								info.createDate = exercise.createDate;
								exercise.id = MyActivityDao.getInstance()
										.getActivityId(
												RecordExerciseActivity.this,
												exercise.activityId,
												exercise.userId);
								info.recordId = exercise.id;
								info.type = 2;
								info.userId = exercise.userId;

								MyRecordsDao.getInstance().insert(
										RecordExerciseActivity.this, info);

								for (ExerciseDetail detail : detailList) {

									for (EditText etTime : timeList) {
										if (etTime.getTag().equals(detail.name)) {
											detail.time = etTime.getText()
													.toString().trim();
											String time = "";
											if (detail.time != null
													&& !detail.time.equals("")) {
												time = detail.time;
												detail.unit = detail.time
														.substring(detail.time
																.length() - 2);
												detail.time = time.substring(0,
														time.length() - 2);
											}

										}
									}

									for (TextView txtKcal : kcalList) {
										if (txtKcal.getTag()
												.equals(detail.name)) {
											String kcal = txtKcal.getText()
													.toString().trim();
											detail.calorie = kcal.substring(0,
													kcal.length() - 3);
										}
									}

									detail.recordId = exercise.id;
									ActivityDetailDao
											.getInstance()
											.insert(RecordExerciseActivity.this,
													detail);
								}
								if (MyRecordsDao.getInstance().getRecord(
										RecordExerciseActivity.this, dateStr,
										exercise.userId)) {
									MyRecords records = new MyRecords();
									records.createDate = dateStr;
									records.recordId = "";
									records.type = 7;
									records.userId = exercise.userId;
									MyRecordsDao.getInstance().insert(
											RecordExerciseActivity.this,
											records);
								}

								if (CommUtils.isAutoSync(RecordExerciseActivity.this)) {
									new SyncExerciseTask(
											RecordExerciseActivity.this,
											exercise).execute();
								}

								CommUtils.showToast(RecordExerciseActivity.this,
												"运动记录保存成功");

								Intent intent = new Intent(
										RecordExerciseActivity.this,
										MainActivity.class);
								startActivity(intent);
								finish();
							} else {
								CommUtils
										.showToast(RecordExerciseActivity.this,
												"运动记录保存失败");
							}

						}

					}
				});
		customActionBarView.findViewById(R.id.actionbar_cancel)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CommUtils.showAlertDialog(RecordExerciseActivity.this);
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
