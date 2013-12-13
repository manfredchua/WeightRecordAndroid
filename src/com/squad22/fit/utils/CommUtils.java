package com.squad22.fit.utils;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squad22.fit.R;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.dao.ProfileDao;
import com.squad22.fit.dialogfragment.PhotoDialogFragment;
import com.squad22.fit.entity.ActionEntity;
import com.squad22.fit.entity.Exercise;
import com.squad22.fit.entity.Meal;
import com.squad22.fit.entity.Measurement;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.entity.Profile;
import com.squad22.fit.wheelview.NumericWheelAdapter;
import com.squad22.fit.wheelview.OnWheelChangedListener;
import com.squad22.fit.wheelview.WheelView;

@SuppressLint("SimpleDateFormat")
public class CommUtils {
	// 照相机拍照得到的图片
	public static File mCurrentPhotoFile;
	private static OnDateTimeSetListener mCallBack;
	private static int curr_year;
	private static int curr_month;
	private static int curr_day;
	// 添加大小月月份并将其转换为list,方便之后的判断
	private static String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	private static String[] months_little = { "4", "6", "9", "11" };
	private static int START_YEAR = 1900, END_YEAR = 3100;
	private static WheelView wv_month;
	private static WheelView wv_day;
	private static WheelView wv_year;
	private static List<String> list_big;
	private static List<String> list_little;
	private static PopupWindow pw;
	private static View view;

	public static void showPhoto(Activity activity) {
		PhotoDialogFragment fragment = new PhotoDialogFragment(activity);
		fragment.show(activity.getFragmentManager(), "photo");
	}

	// @SuppressWarnings("deprecation")
	// public static void showPhoto(final Activity activity) {
	// View view = activity.getLayoutInflater().inflate(R.layout.pw_layout,
	// null);
	//
	// TextView txtTitle = (TextView) view.findViewById(R.id.popup_title);
	// txtTitle.setText(activity.getString(R.string.add_photo));
	// ListView lvPosition = (ListView) view.findViewById(R.id.lv_position);
	// lvPosition.setAdapter(new PhotoAdapter(activity, Constants.Photo));
	// final PopupWindow PhotoPw = new PopupWindow(view,
	// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
	// PhotoPw.setBackgroundDrawable(new BitmapDrawable());
	// PhotoPw.showAtLocation(view.findViewById(R.id.rl_main),
	// Gravity.CENTER_HORIZONTAL, 0, 0);
	//
	// view.setOnKeyListener(new View.OnKeyListener() {
	//
	// @Override
	// public boolean onKey(View v, int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// PhotoPw.dismiss();
	// }
	// return false;
	// }
	// });
	//
	// lvPosition.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	// if (arg2 == 0) {
	// // 调用拍照方法
	// doTakePhoto(activity);
	// PhotoPw.dismiss();
	// } else if (arg2 == 1) {
	// // 图库
	// Intent intents = new Intent();
	// intents.setType("image/*");
	// intents.setAction(Intent.ACTION_GET_CONTENT);
	// activity.startActivityForResult(intents, Constants.GALLERY);
	// PhotoPw.dismiss();
	// }
	// }
	// });
	//
	// }

	/***
	 * Launches Camera to take a picture and store it in a file.这个方法是启动照相程序
	 */
	public static void doTakePhoto(Activity activity) {

		// Launch camera to take photo for selected contact
		if (!Constants.PHOTO_DIR.exists()) {
			Constants.PHOTO_DIR.mkdirs();
		}

		mCurrentPhotoFile = new File(Constants.PHOTO_DIR, getPhotoFileName());

		final Intent intent = getTakePickIntent(mCurrentPhotoFile);
		activity.startActivityForResult(intent, Constants.TAKE_A_PICTURE);

	}

	/***
	 * Create a file name for the icon photo using current time.创建图片名称
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	/***
	 * Constructs an intent for capturing a photo and storing it in a temporary
	 * file.
	 */
	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	public static String doCropPhoto(Context activity) {

		// Add the image to the media store
		MediaScannerConnection.scanFile(activity,
				new String[] { mCurrentPhotoFile.getAbsolutePath() },
				new String[] { null }, null);

		// Launch gallery to crop the photo
		final Intent intent = getCropImageIntent(activity,
				Uri.fromFile(mCurrentPhotoFile));
		((Activity) activity).startActivityForResult(intent,
				Constants.PHOTO_PICKED_WITH_DATA);
		return mCurrentPhotoFile.getAbsolutePath();
	}

	public static void startPhotoZoom(Context activity, Uri photoUri, File file) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(Uri.fromFile(file), "image/**");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("scale", false);
		intent.putExtra("outputX", 768);
		intent.putExtra("outputY", 1024);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("setWallpaper", false);
		intent.putExtra("noFaceDetection", true); // no face detection
		((Activity) activity).startActivityForResult(intent,
				Constants.PHOTO_PICKED_WITH_DATA);

	}

	/***
	 * Constructs an intent for image cropping.
	 */
	public static Intent getCropImageIntent(Context context, Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/**");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("scale", false);
		intent.putExtra("outputX", 768);
		intent.putExtra("outputY", 1024);
		intent.putExtra("return-data", true);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		return intent;
	}

	@SuppressWarnings("deprecation")
	public static void showSetDateTime(final Context context, final int year,
			int month, int day, final Handler handler) {

		view = LayoutInflater.from(context).inflate(R.layout.time_layout, null);

		pw = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		mCallBack = new OnDateTimeSetListener() {

			@SuppressWarnings("static-access")
			@Override
			public void onDateTimeSet(int year, int monthOfYear, int dayOfMonth) {

				String sessionDate = year + "年" + monthOfYear + "月"
						+ dayOfMonth + "日";
				SharedPreferences sp = context.getSharedPreferences("profile",
						context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("birthday", sessionDate);
				editor.putString("target", year + "-" + monthOfYear + "-"
						+ dayOfMonth);
				editor.commit();
				handler.sendEmptyMessage(0);

				Log.i("onDateTimeSet", sessionDate);
			}
		};

		list_big = Arrays.asList(months_big);
		list_little = Arrays.asList(months_little);

		Button btnConfirm = (Button) view.findViewById(R.id.confirm);
		Button btnCancel = (Button) view.findViewById(R.id.cancel);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pw != null && pw.isShowing()) {
					pw.dismiss();
				}
			}
		});
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				curr_year = wv_year.getCurrentItem() + START_YEAR;
				curr_month = wv_month.getCurrentItem() + 1;
				curr_day = wv_day.getCurrentItem() + 1;
				if (mCallBack != null) {
					mCallBack.onDateTimeSet(curr_year, curr_month, curr_day);
				}
				if (pw != null && pw.isShowing()) {
					pw.dismiss();
				}
			}
		});

		int textSize = 0;
		textSize = adjustFontSize(((Activity) context).getWindow()
				.getWindowManager());
		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));//
		wv_year.setCyclic(true);//
		wv_year.setLabel("年");//
		wv_year.setCurrentItem(year - START_YEAR);//

		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");//
		wv_month.setCurrentItem(month);

		// 日
		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setLabel("日");//
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setCurrentItem(day - 1);

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};

		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
		wv_year.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_day.TEXT_SIZE = textSize;
		pw.setBackgroundDrawable(new BitmapDrawable());
		pw.showAtLocation(view.findViewById(R.id.rl_main),
				Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	public interface OnDateTimeSetListener {
		void onDateTimeSet(int year, int monthOfYear, int dayOfMonth);
	}

	@SuppressWarnings("deprecation")
	public static int adjustFontSize(WindowManager windowmanager) {
		int screenWidth = windowmanager.getDefaultDisplay().getWidth();
		if (screenWidth <= 240) { // 240X320 屏幕
			return 10;
		} else if (screenWidth <= 320) { // 320X480 屏幕
			return 14;
		} else if (screenWidth <= 480) { // 480X800 或 480X854 屏幕
			return 22;
		} else if (screenWidth <= 540) { // 540X960 屏幕
			return 22;
		} else if (screenWidth <= 800) { // 800X1280 屏幕
			return 35;
		} else { // 大于 800X1280
			return 35;
		}
	}

	@SuppressWarnings("deprecation")
	public static int adjust(WindowManager windowmanager) {
		int screenWidth = windowmanager.getDefaultDisplay().getWidth();
		if (screenWidth <= 540) { // 540X960 屏幕
			return 75;
		} else if (screenWidth <= 800) { // 800X1280 屏幕
			return 256;
		} else { // 大于 800X1280
			return 256;
		}
	}

	public static void setLightFontFamily(Context context, TextView view) {
		Typeface tf = Typeface.createFromAsset(context.getAssets(),
				"Gill Sans/Gill Sans MT Light.ttf");
		view.setTypeface(tf);
	}

	public static void setFontFamily(Context context, TextView view) {
		Typeface tf = Typeface.createFromAsset(context.getAssets(),
				"Gill Sans/Gill Sans MT.ttf");
		view.setTypeface(tf);
	}

	public static void setNameFontFamily(Context context, TextView view) {
		Typeface tf = Typeface.createFromAsset(context.getAssets(),
				"Gill Sans/Gill Sans MT Bold.ttf");
		view.setTypeface(tf);
	}

	public static String getEditTextValue(EditText etName) {
		return etName.getText().toString().toString().trim();
	}

	public static String getTextViewValue(TextView etName) {
		return etName.getText().toString().toString();
	}

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void showAlertDialog(final Activity activity) {
		new AlertDialog.Builder(activity)
				.setTitle(
						activity.getResources().getString(R.string.alert_title))
				.setMessage(
						activity.getResources().getString(R.string.alert_msg))
				.setNegativeButton(
						activity.getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setPositiveButton(
						activity.getResources().getString(
								R.string.alert_discard),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								activity.finish();
								dialog.dismiss();
							}
						}).show();
	}

	/**
	 * 设置ProgressDialog
	 * 
	 * @param
	 * @result
	 */
	public static ProgressDialog createProgressDialog(Context context,
			ProgressDialog proDialog, String message) {
		proDialog = new ProgressDialog(context);
		proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置最大值
		proDialog.setMax(100);
		if (message != null) {
			proDialog.setMessage(message);
		}
		// 设置ProgressDialog 的进度条是否不明确
		proDialog.setIndeterminate(false);
		proDialog.show();
		proDialog.setCanceledOnTouchOutside(false);
		return proDialog;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	public static int getAge(String birthday) {
		int age = 0;
		Date currentDate = new Date();
		int year = currentDate.getYear();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sf.parse(birthday);
			age = year - date.getYear();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return age;
	}

	public static String getTargetWeight(double height, String sex) {
		double targetWeight = 0;
		if (sex.equals("男")) {
			targetWeight = 21 * (height * height);
		} else {
			targetWeight = 20 * (height * height);
		}
		return getDouble(targetWeight);
	}

	public static String getDouble(double value) {
		DecimalFormat df = new DecimalFormat("#####0.0");
		String target = df.format(value);
		return target;
	}

	public static String getString(double value) {
		DecimalFormat df = new DecimalFormat("#####0");
		String target = df.format(value);
		return target;
	}

	public static String getBodyFat(String sex, int age) {
		String body = "";
		if (sex.equals("男")) {
			if (age >= 20) {
				body = "6.2-12.5%";
			} else if (age >= 21 && age <= 25) {
				body = "7.4-15.4%";
			} else if (age >= 26 && age <= 30) {
				body = "8.5-16.4%";
			} else if (age >= 31 && age <= 35) {
				body = "11.8-17.5%";
			} else if (age >= 36 && age <= 40) {
				body = "12.8-20.2%";
			} else if (age >= 41 && age <= 45) {
				body = "13.9-21.3%";
			} else if (age >= 46 && age <= 50) {
				body = "14.9-22.4%";
			} else if (age >= 51 && age <= 55) {
				body = "16.0-25.0%";
			} else if (age >= 56) {
				body = "19.2-26.0%";
			}
		} else {
			if (age >= 20) {
				body = "17.8-23.2%";
			} else if (age >= 21 && age <= 25) {
				body = "18.5-23.8%";
			} else if (age >= 26 && age <= 30) {
				body = "19.1-24.5%";
			} else if (age >= 31 && age <= 35) {
				body = "21.6-26.2%";
			} else if (age >= 36 && age <= 40) {
				body = "22.3-27.3%";
			} else if (age >= 41 && age <= 45) {
				body = "22.9-27.9%";
			} else if (age >= 46 && age <= 50) {
				body = "23.5-28.6%";
			} else if (age >= 51 && age <= 55) {
				body = "24.1-30.7%";
			} else if (age >= 56) {
				body = "26.6-31.3%";
			}
		}
		return body;
	}

	public static String getLeanMusde(String sex, int age) {
		String musde = "";
		if (sex.equals("男")) {
			if (age >= 18 && age <= 39) {
				musde = "33.3-39.3%";
			} else if (age >= 40 && age <= 59) {
				musde = "33.1-39.1%";
			} else if (age >= 60) {
				musde = "32.9-38.9%";
			}
		} else {
			if (age >= 18 && age <= 39) {
				musde = "24.3-30.3%";
			} else if (age >= 40 && age <= 59) {
				musde = "24.1-30.1%";
			} else if (age >= 60) {
				musde = "23.9-29.9%";
			}
		}

		return musde;
	}

	public static String getKGBMR(String sex, double weight, double height,
			int age) {
		double bmr = 0;
		if (sex.equals("男")) {
			bmr = 66 + (13.7 * weight) + (5 * (height * 100)) - (6.8 * age);
		} else {
			bmr = 655 + (9.6 * weight) + (1.8 * (height * 100)) - (4.7 * age);
		}
		return getString(bmr);
	}

	/**
	 * 计算磅的BMR
	 * 
	 * @param sex
	 *            性别(男),(女)
	 * @param weight
	 *            体重(lb/磅)
	 * @param height
	 *            身高(in/英寸)
	 * @param age
	 *            年龄
	 * @return
	 */
	public static String getLBBMR(String sex, double weight, double height,
			int age) {
		double bmr = 0;
		if (sex.equals("男")) {
			bmr = 66 + (6.23 * weight) + (12.7 * height) - (6.8 * age);

		} else {
			bmr = 655 + (4.35 * weight) + (4.7 * height) - (4.7 * age);
		}
		return getString(bmr);
	}

	public static String getKGBMI(double weight, double height) {
		double bmi = weight / (height * height);
		return getString(bmi);
	}

	public static String getLBBMI(double weight, double height) {
		double bmi = weight / (height * height) * 703;
		return getString(bmi);
	}

	public static String getVisceralFat(int age) {
		String target = "";
		if (age >= 1 && age <= 20) {
			target = "5";
		} else if (age >= 21 && age <= 40) {
			target = "6";
		} else if (age >= 41 && age <= 60) {
			target = "7";
		} else if (age >= 61 && age <= 80) {
			target = "8";
		} else {
			target = "9";
		}

		return target;
	}

	public static ArrayList<ActionEntity> setArrayList() {
		ArrayList<ActionEntity> arrayList = new ArrayList<ActionEntity>();

		ActionEntity action = new ActionEntity();
		action.index = 1;
		// action.title = "睡眠";
		action.resourceId = R.drawable.moon_circle;
		arrayList.add(action);

		ActionEntity action2 = new ActionEntity();
		action2.index = 2;
		// action2.title = "饮食";
		action2.resourceId = R.drawable.bowl_circle;
		arrayList.add(action2);

		ActionEntity action3 = new ActionEntity();
		action3.index = 3;
		// action3.title = "新测量";
		action3.resourceId = R.drawable.scale_circle;
		arrayList.add(action3);

		ActionEntity action4 = new ActionEntity();
		action4.index = 4;
		// action4.title = "运动";
		action4.resourceId = R.drawable.dumbbell_circle;
		arrayList.add(action4);

		ActionEntity action5 = new ActionEntity();
		action5.index = 5;
		// action5.title = "喝水";
		action5.resourceId = R.drawable.mug_circle;
		arrayList.add(action5);

		return arrayList;
	}



	/**
	 * 检查网络是否可用 <功能详细描述>
	 * 
	 * @author user
	 */
	public static boolean checkNetwork(Context context) {

		ConnectivityManager localConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ninfo = localConnectivityManager.getActiveNetworkInfo();
		if (null != ninfo && ninfo.isConnected()) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isWIFIAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Service.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public static boolean is3GAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Service.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	public static String getWeek(String pTime) {

		String Week = "星期";

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {

			c.setTime(format.parse(pTime));

		} catch (ParseException e) {

		}
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			Week += "日";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			Week += "一";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			Week += "二";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			Week += "三";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			Week += "四";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			Week += "五";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			Week += "六";
		}

		return Week;
	}

	public static String getHour(int Hour) {
		StringBuilder hourStr = new StringBuilder();
		if (Hour < 10) {
			hourStr.append("0" + Hour);
		} else {
			hourStr.append(Hour);
		}

		return hourStr.toString();
	}

	public static String getWaterHour(int Hour) {
		StringBuilder hourStr = new StringBuilder();

		if (Hour <= 12) {
			hourStr.append("上午");
		} else {
			hourStr.append("下午");
		}

		if (Hour < 10) {
			hourStr.append("0" + Hour);
		} else {
			hourStr.append(Hour);
		}

		return hourStr.toString();
	}

	public static String getMinute(int Minute) {
		String MinuteStr = "";
		if (Minute < 10) {
			MinuteStr = "0" + Minute;
		} else {
			MinuteStr = String.valueOf(Minute);
		}

		return MinuteStr;
	}



	public static double formatML(String unit) {
		double MLresult = 1.0;
		if (unit.equals("升")) {
			MLresult = 1000.0;
		}
		if (unit.equals("杯")) {
			MLresult = 240.0;
		}
		if (unit.equals("汤匙")) {
			MLresult = 15.0;
		}
		if (unit.equals("茶匙")) {
			MLresult = 5.0;
		}
		if (unit.equals("品脱")) {
			MLresult = 568.26125;
		}
		return MLresult;
	}

	public static double formatG(String unit) {
		double result = 1.0;
		if (unit.equals("公斤")) {
			result = 1000.0;
		}
		if (unit.equals("英磅") || unit.equals("磅")) {
			result = 453.59;
		}
		if (unit.equals("盎司")) {
			result = 28.34;
		}

		if (unit.equals("斤")) {
			result = 500;
		}

		return result;
	}

	public static void getPhoto(Context context, MyRecords myRecord,
			ArrayList<byte[]> photos) {
		try {
			switch (myRecord.type) {
			case 1:
				Meal meal = MyMealDao.getInstance().getMealById(context,
						myRecord.recordId);
				if (meal.image != null && meal.image.length() > 0) {
					Bitmap bitmap = ImagesUtils.locDecodeImage(meal.image);
					byte[] mealImage = ImagesUtils.convertImageToByte(bitmap);
					photos.add(mealImage);

					bitmap.recycle();
				}
				break;
			case 2:
				Exercise activity = MyActivityDao.getInstance()
						.getActivityById(context, myRecord.recordId);
				if (activity.image != null && activity.image.length() > 0) {
					Bitmap bitmap = ImagesUtils.locDecodeImage(activity.image);
					byte[] mealImage = ImagesUtils.convertImageToByte(bitmap);
					photos.add(mealImage);
					bitmap.recycle();
				}
				break;
			case 3:
				Measurement measurement = MeasurementDao.getInstance()
						.getMeasurementById(context, myRecord.recordId);
				if (measurement.image1 != null
						&& measurement.image1.length() > 0) {
					Bitmap bitmap = ImagesUtils
							.locDecodeImage(measurement.image1);
					byte[] mealImage = ImagesUtils.convertImageToByte(bitmap);
					photos.add(mealImage);
					bitmap.recycle();
				}

				if (measurement.image2 != null
						&& measurement.image2.length() > 0) {
					Bitmap bitmap = ImagesUtils
							.locDecodeImage(measurement.image2);
					byte[] mealImage = ImagesUtils.convertImageToByte(bitmap);
					photos.add(mealImage);
					bitmap.recycle();
				}

				if (measurement.image3 != null
						&& measurement.image3.length() > 0) {
					Bitmap bitmap = ImagesUtils
							.locDecodeImage(measurement.image3);
					byte[] mealImage = ImagesUtils.convertImageToByte(bitmap);
					photos.add(mealImage);
					bitmap.recycle();
				}
				break;

			}
		} catch (Exception e) {

		}
	}



	@SuppressWarnings("static-access")
	public static String getUserId(Context mContext) {
		SharedPreferences userSP = mContext.getSharedPreferences(
				Constants.KIIUSER, mContext.MODE_PRIVATE);
		String userName = userSP.getString(Constants.USERNAME, "");
		Profile profile = ProfileDao.getInstance().getProfile(mContext,
				userName);
		return profile.id;
	}

	@SuppressWarnings("static-access")
	public static boolean isAutoSync(Context mContext) {
		boolean autoSync = false;
		// 判断是否自动同步
		SharedPreferences sp = mContext.getSharedPreferences(
				Constants.BACKUP_RECORDS, mContext.MODE_PRIVATE);
		boolean isRecord = sp.getBoolean(Constants.IS_RECORD, false);
		if (isRecord) {
			if (CommUtils.checkNetwork(mContext)) {
				String network = sp.getString(Constants.WIFI, "wifi");
				if (network.equals("wifi")) {
					if (CommUtils.isWIFIAvailable(mContext)) {
						autoSync = true;
					}
				} else {
					if (CommUtils.isWIFIAvailable(mContext)
							|| CommUtils.is3GAvailable(mContext)) {
						autoSync = true;
					}
				}
			}
		}
		return autoSync;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getMap(String jsonString) {
		JSONObject jsonObject;
		Map<String, String> valueMap = new HashMap<String, String>();
		try {
			jsonObject = new JSONObject(jsonString);
			Iterator<String> keyIter = jsonObject.keys();

			JSONArray array = jsonObject.names();
			if (array != null) {
				String key;
				String value;
				while (keyIter.hasNext()) {
					key = keyIter.next();
					value = jsonObject.getString(key);
					valueMap.put(key, value);
				}
				return valueMap;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return valueMap;
	}
	
	
	public static void showError(Context context, String code){
		try{
			code = code.substring(34, 37);
			InputStream inputStream = context.getAssets()
					.open("errors.xml");
			List<HashMap<String, String>> list = XmlPullParserMessage.getKiiError(inputStream);
			for (HashMap<String, String> hashMap : list) {
				if(hashMap.get("code").equals(code)){
					CommUtils.showToast(context,
							hashMap.get("msg"));
				}
			}
			
			
		}catch(Exception e){
			
		}
	}
}
