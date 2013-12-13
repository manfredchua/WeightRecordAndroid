package com.squad22.fit.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.dao.SleepDao;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.entity.Sleep;
import com.squad22.fit.task.SyncSleepTask;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class RecordSleepActivity extends Activity {

	TextView txtStartTime;
	TextView chronometer;
	ImageView ivMoonFull;
	RelativeLayout rlPhoto;
	Button btnEndTime;
	SimpleDateFormat sf = new SimpleDateFormat("HH:mm a", Locale.CHINA);
	SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	TranslateAnimation animLoading;
	int recLen = 0;
	Timer timer;
	Date date;
	Calendar mCalendar = Calendar.getInstance();

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_sleep_layout);
		WeightRecordApplication.getInstance().addActivity(this);
		initView();

		date = (Date) getIntent().getSerializableExtra(Constants.CURRENT_DATE);

		String startTime = sf.format(date);

		txtStartTime.setText(startTime + "开始睡觉");

		ivMoonFull = new ImageView(this);
		ivMoonFull.setImageResource(R.drawable.moon_full);

		ivMoonFull.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.menu_in));
		WindowManager wManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		int screenWidth = wManager.getDefaultDisplay().getWidth();
		ivMoonFull.setPaddingRelative(screenWidth / 4, 0, screenWidth / 4, 0);
		rlPhoto.addView(ivMoonFull);

	}

	private void initView() {
		txtStartTime = (TextView) findViewById(R.id.txt_start_time);
		chronometer = (TextView) findViewById(R.id.chronometer);
		btnEndTime = (Button) findViewById(R.id.btn_end_time);
		rlPhoto = (RelativeLayout) findViewById(R.id.rl_photo);

		btnEndTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (timer != null) {
					timer.cancel();
					timer = null;
				}

				Date currentDate = new Date();

				Sleep sleep = new Sleep();
				sleep.createDate = dateTime.format(new Date());
				sleep.sleepStart = dateTime.format(date);
				sleep.sleepEnd = dateTime.format(currentDate);
				int random = (int) (Math.random() * 10000);
				sleep.sleepId = "s" + random + "s";
				sleep.userId = CommUtils.getUserId(RecordSleepActivity.this);

				if (SleepDao.getInstance().insert(RecordSleepActivity.this,
						sleep)) {
					MyRecords info = new MyRecords();
					info.createDate = sleep.createDate;
					info.type = 4;
					info.recordId = SleepDao.getInstance().getSleepId(
							RecordSleepActivity.this, sleep.sleepId,
							sleep.userId);
					info.userId = sleep.userId;

					MyRecordsDao.getInstance().insert(RecordSleepActivity.this,
							info);

					String createDate = dateSF.format(currentDate);
					if (MyRecordsDao.getInstance().getRecord(
							RecordSleepActivity.this, createDate, sleep.userId)) {
						MyRecords records = new MyRecords();
						records.createDate = createDate;
						records.recordId = "";
						records.type = 7;
						info.userId = sleep.userId;
						MyRecordsDao.getInstance().insert(
								RecordSleepActivity.this, records);
					}

					// 判断是否自动同步
					if (CommUtils.isAutoSync(RecordSleepActivity.this)) {
						new SyncSleepTask(RecordSleepActivity.this, sleep).execute();
					}

					Intent intent = new Intent(RecordSleepActivity.this,
							MainActivity.class);
					startActivity(intent);
					RecordSleepActivity.this.finish();
				}
			}
		});

		timer = new Timer(true);
		timer.schedule(task, 60 * 1000, 60 * 1000);
	}

	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				recLen++;
				chronometer.setText(GetRecTime(recLen));
				break;
			}
			super.handleMessage(msg);
		}
	};

	public static CharSequence GetRecTime(int minits) {
		if (minits == 0)
			return "00:00";
		int hour = minits / 60;

		return intToTime(hour) + ":" + intToTime(minits);
	}

	public static String intToTime(int time) {
		if (time <= 0)
			return "00";
		if (time < 10)
			return "0" + time;
		else
			return time + "";
	}

}
