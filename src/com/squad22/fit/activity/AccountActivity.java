package com.squad22.fit.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kii.cloud.storage.KiiUser;
import com.squad22.fit.R;
import com.squad22.fit.dao.ActivityDetailDao;
import com.squad22.fit.dao.BowelCountDao;
import com.squad22.fit.dao.MealDetailDao;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.dao.ProfileDao;
import com.squad22.fit.dao.SleepDao;
import com.squad22.fit.dao.WaterCountDao;
import com.squad22.fit.entity.Profile;
import com.squad22.fit.task.SyncTask;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

@SuppressLint("SimpleDateFormat")
public class AccountActivity extends Activity implements OnClickListener {

	@SuppressWarnings("unused")
	private static final String TAG = "AccountActivity";
	ActionBar actionBar;
	Switch btnAutomatic;
	ImageView ivWifi;
	ImageView ivWifiOr3G;
	ImageView ivLine;
	ImageView ivWifiLine;
	ImageView ivWifi3GLine;
	RelativeLayout rlWifi;
	RelativeLayout rlWifi3G;
	TextView txtEmail;
	TextView txtResetPwd;
	RelativeLayout llSync;
	TextView txtSyncDate;
	ImageView ivSync;
	RelativeLayout rlFreeTime;
	TextView txtFreeEndTime;
	// TextView txtEndTime;
	// TextView txt3Month;
	// TextView txt6Month;
	// TextView txt1Year;
	Button btnSignOut;
	SharedPreferences sp;
	String userId;
	MyActivityDao activityDao = MyActivityDao.getInstance();
	MyMealDao mealDao = MyMealDao.getInstance();
	ActivityDetailDao activityDetailDao = ActivityDetailDao.getInstance();
	MealDetailDao mealDetailDao = MealDetailDao.getInstance();
	MeasurementDao measurementDao = MeasurementDao.getInstance();
	SleepDao sleepDao = SleepDao.getInstance();
	WaterCountDao waterDao = WaterCountDao.getInstance();
	BowelCountDao bowelDao = BowelCountDao.getInstance();
	ProfileDao profileDao = ProfileDao.getInstance();
	AnimationDrawable frameAnimation;
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat cnFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm a",
			Locale.CHINA);

	SimpleDateFormat cnFreeFormat = new SimpleDateFormat("yyyy年MM月dd日",
			Locale.CHINA);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.account_layout);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.menu_account));

		initView();

		SharedPreferences sp = getSharedPreferences(Constants.BACKUP_RECORDS,
				MODE_PRIVATE);
		String syncDate = sp.getString(Constants.SYNC_DATE, "");
		try {
			Date date = dateFormat.parse(syncDate);
			String current = cnFormat.format(date);
			txtSyncDate.setText(current);
		} catch (Exception e) {

		}
		SharedPreferences userSP = getSharedPreferences(Constants.KIIUSER,
				MODE_PRIVATE);
		String userName = userSP.getString(Constants.USERNAME, "");
		Profile profile = ProfileDao.getInstance().getProfile(this, userName);

		userId = profile.id;
		KiiUser user = KiiUser.getCurrentUser();
		if (user != null) {
			String freeDate = "";
			try {
				freeDate = user.getString(Constants.FreeDateMember);
			} catch (Exception e) {

			}
			if (freeDate != null && !freeDate.equals("")) {
				try {
					Date date = dateFormat.parse(freeDate);
					txtFreeEndTime.setText(getString(R.string.free_date)
							+ cnFreeFormat.format(date));
				} catch (ParseException e) {
				}
				rlFreeTime.setVisibility(View.VISIBLE);
			} else {
				rlFreeTime.setVisibility(View.GONE);
			}
		} else {
			rlFreeTime.setVisibility(View.GONE);
		}
	}

	private void initView() {
		btnAutomatic = (Switch) findViewById(R.id.btn_toggle_sub);
		ivWifi = (ImageView) findViewById(R.id.iv_wifi);
		ivWifiOr3G = (ImageView) findViewById(R.id.iv_wifi_3g);
		ivLine = (ImageView) findViewById(R.id.iv_line);
		ivWifiLine = (ImageView) findViewById(R.id.iv_wifi_line);
		ivWifi3GLine = (ImageView) findViewById(R.id.iv_wifi_3g_line);
		rlWifi = (RelativeLayout) findViewById(R.id.rl_wifi);
		rlWifi3G = (RelativeLayout) findViewById(R.id.rl_wifi_3g);

		txtEmail = (TextView) findViewById(R.id.txt_email);
		txtResetPwd = (TextView) findViewById(R.id.txt_reset_pwd);

		llSync = (RelativeLayout) findViewById(R.id.rl_sync);
		txtSyncDate = (TextView) findViewById(R.id.txt_sync_date);
		ivSync = (ImageView) findViewById(R.id.iv_sync);

		rlFreeTime = (RelativeLayout) findViewById(R.id.rl_free_time);
		txtFreeEndTime = (TextView) findViewById(R.id.txt_free_end_time);

		// txtEndTime = (TextView) findViewById(R.id.txt_end_time);
		// txt3Month = (TextView) findViewById(R.id.txt_3_month);
		// txt6Month = (TextView) findViewById(R.id.txt_6_month);
		// txt1Year = (TextView) findViewById(R.id.txt_1_year);

		btnSignOut = (Button) findViewById(R.id.btn_sign_out);

		rlWifi.setOnClickListener(this);
		rlWifi3G.setOnClickListener(this);
		txtResetPwd.setOnClickListener(this);
		llSync.setOnClickListener(this);
		btnSignOut.setOnClickListener(this);

		KiiUser user = KiiUser.getCurrentUser();
		txtEmail.setText(user.getEmail());

		sp = getSharedPreferences(Constants.BACKUP_RECORDS, MODE_PRIVATE);
		boolean isRecord = sp.getBoolean(Constants.IS_RECORD, false);
		setVisibility(isRecord, sp);

		btnAutomatic
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPreferences sp = getSharedPreferences(
								Constants.BACKUP_RECORDS, MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putBoolean(Constants.IS_RECORD, isChecked);
						editor.commit();

						setVisibility(isChecked, sp);

						KiiUser user = KiiUser.getCurrentUser();
						if (user != null) {

							Profile info = profileDao.getProfile(
									AccountActivity.this, user.getUsername());

							if (isChecked) {
								String network = sp.getString(Constants.WIFI,
										"wifi");
								if (network.equals("wifi")) {
									info.backupState = 1;
								} else {
									info.backupState = 2;
								}
							} else {
								info.backupState = 0;
							}

							new KiiUserTask(AccountActivity.this, info, user
									.toUri(), null).execute();
						}
					}
				});
	}

	private void setVisibility(boolean isChecked, SharedPreferences sp) {
		if (isChecked) {
			btnAutomatic.setChecked(true);
			ivWifiLine.setVisibility(View.VISIBLE);
			ivLine.setVisibility(View.VISIBLE);
			rlWifi.setVisibility(View.VISIBLE);
			rlWifi3G.setVisibility(View.VISIBLE);
			ivWifi3GLine.setVisibility(View.VISIBLE);
			String network = sp.getString(Constants.WIFI, "wifi");
			if (network.equals("wifi")) {
				ivWifi.setVisibility(View.VISIBLE);
				ivWifi.setImageResource(R.drawable.light_navigation_accept);
				ivWifiOr3G.setVisibility(View.INVISIBLE);
			} else {
				ivWifiOr3G.setVisibility(View.VISIBLE);
				ivWifiOr3G.setImageResource(R.drawable.light_navigation_accept);
				ivWifi.setVisibility(View.INVISIBLE);
			}
		} else {
			ivWifiLine.setVisibility(View.GONE);
			ivLine.setVisibility(View.GONE);
			rlWifi.setVisibility(View.GONE);
			rlWifi3G.setVisibility(View.GONE);
			ivWifi3GLine.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_reset_pwd:
			Intent intent = new Intent(this, ResetPasswordActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_wifi:
			Editor editor = sp.edit();
			editor.putString(Constants.WIFI, "wifi");
			editor.commit();
			ivWifi.setVisibility(View.VISIBLE);
			ivWifi.setImageResource(R.drawable.light_navigation_accept);
			ivWifiOr3G.setVisibility(View.INVISIBLE);

			KiiUser user = KiiUser.getCurrentUser();
			if (user != null) {
				Profile info = profileDao.getProfile(AccountActivity.this,
						user.getUsername());
				info.backupState = 1;
				new KiiUserTask(AccountActivity.this, info, user.toUri(), null);
			}
			break;
		case R.id.rl_wifi_3g:
			editor = sp.edit();
			editor.putString(Constants.WIFI, "wifi_3g");
			editor.commit();
			ivWifiOr3G.setVisibility(View.VISIBLE);
			ivWifiOr3G.setImageResource(R.drawable.light_navigation_accept);
			ivWifi.setVisibility(View.INVISIBLE);

			user = KiiUser.getCurrentUser();
			if (user != null) {
				Profile info = profileDao.getProfile(AccountActivity.this,
						user.getUsername());
				info.backupState = 2;
				new KiiUserTask(AccountActivity.this, info, user.toUri(), null);
			}
			break;
		case R.id.rl_sync:
			if (CommUtils.checkNetwork(this)) {
				user = KiiUser.getCurrentUser();
				if (user != null) {
					String freeDateMember = null;
					try {
						freeDateMember = user
								.getString(Constants.FreeDateMember);
					} catch (Exception e1) {
					}
					if (freeDateMember != null && !freeDateMember.equals("")) {

						try {
							Date date = new Date();
							Date freeDate = dateFormat.parse(freeDateMember);

							if (freeDate.getTime() > date.getTime()) {
								ivSync.setBackgroundResource(R.anim.sync_thrust);
								startAnim();
								new SyncTask(AccountActivity.this, mHandler,
										false).execute();
							}
						} catch (ParseException e) {
						}
					} else {
						ivSync.setBackgroundResource(R.anim.sync_thrust);
						startAnim();
						new SyncTask(AccountActivity.this, mHandler, false)
								.execute();
					}
				}
			} else {
				CommUtils.showToast(this, "无网络，请设置网络");
			}
			break;
		case R.id.btn_sign_out:
			KiiUser.logOut();
			SharedPreferences sp = getSharedPreferences(Constants.KIIUSER,
					MODE_PRIVATE);
			editor = sp.edit();
			editor.putBoolean(Constants.LOGIN_STATUS, false);
			editor.commit();

			intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			WeightRecordApplication.getInstance().exit();
			break;
		default:
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			SharedPreferences sp = getSharedPreferences(
					Constants.BACKUP_RECORDS, MODE_PRIVATE);
			Editor editor = sp.edit();
			Date date = new Date();
			String currentDate = dateFormat.format(date);
			editor.putString(Constants.SYNC_DATE, currentDate);
			editor.commit();

			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTime(date);
			mCalendar.add(Calendar.MONTH, 1);
			String dateStr = dateFormat.format(mCalendar.getTime());

			sp = getSharedPreferences(Constants.IsMember, MODE_PRIVATE);
			boolean isMember = sp.getBoolean(Constants.FreeDateMember, true);
			KiiUser user = KiiUser.getCurrentUser();
			if (user != null) {
				String freeDate = "";
				try {
					freeDate = user.getString(Constants.FreeDateMember);
				} catch (Exception e) {

				}
				if (!isMember && freeDate.equals("")) {
					Profile info = profileDao.getProfile(AccountActivity.this,
							user.getUsername());
					info.freeDateMember = dateStr;
					new KiiUserTask(AccountActivity.this, info, user.toUri(),
							null).execute();
				}
			}
			switch (msg.what) {
			case 0:
				stopAnim();

				String current = cnFormat.format(date);
				txtSyncDate.setText(current);
				break;

			default:
				break;
			}

		};
	};

	// 执行同步动画 --开始
	private void startAnim() {

		if (ivSync.getBackground() instanceof AnimationDrawable) {

			frameAnimation = (AnimationDrawable) ivSync.getBackground();

			if (!frameAnimation.isRunning()) {
				frameAnimation.start();
				frameAnimation.run();
			}
		}
	};

	private void stopAnim() {

		if (frameAnimation != null && frameAnimation.isRunning()) {
			frameAnimation.stop();
		}
		frameAnimation = null;
	}

}
