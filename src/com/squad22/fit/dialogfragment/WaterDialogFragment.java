package com.squad22.fit.dialogfragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.squad22.fit.activity.MainActivity;
import com.squad22.fit.dao.BowelCountDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.dao.WaterCountDao;
import com.squad22.fit.entity.BowelCount;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.entity.WaterCount;
import com.squad22.fit.task.SyncBowelTask;
import com.squad22.fit.task.SyncWaterTask;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

@SuppressLint({ "ValidFragment", "SimpleDateFormat" })
public class WaterDialogFragment extends DialogFragment {
	Activity activity;
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");

	public WaterDialogFragment(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("选项");
		builder.setItems(Constants.water,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Calendar mCalendar = Calendar.getInstance();
						String userId = CommUtils.getUserId(activity);

						String date = sf.format(mCalendar.getTime());
						String dateTime = dateSF.format(mCalendar.getTime());
						switch (which) {
						case 0:
							WaterCount info = new WaterCount();
							info.waterDate = date;
							info.yearMonth = dateTime;
							info.userId = userId;

							if (WaterCountDao.getInstance().insert(activity,
									info)) {

								if (CommUtils.isAutoSync(activity)) {
									new SyncWaterTask(activity, info).execute();
								}

								String recordId = WaterCountDao.getInstance()
										.getWaterCountId(activity,
												info.waterDate, userId);
								MyRecords myRecords = new MyRecords();

								myRecords.recordId = recordId;
								myRecords.type = 5;
								myRecords.userId = userId;
								myRecords.createDate = info.waterDate;

								MyRecordsDao.getInstance().insert(activity,
										myRecords);

								String createDate = dateSF.format(mCalendar
										.getTime());
								if (MyRecordsDao.getInstance().getRecord(
										activity, createDate, userId)) {
									MyRecords records = new MyRecords();
									records.createDate = createDate;
									records.recordId = "";
									records.type = 7;
									records.userId = userId;
									MyRecordsDao.getInstance().insert(activity,
											records);
								}

								Intent intent = new Intent(activity,
										MainActivity.class);
								activity.startActivity(intent);
								CommUtils.showToast(activity, "记录成功");
							} else {
								CommUtils.showToast(activity, "记录失败");
							}
							dialog.dismiss();
							break;
						case 1:
							final BowelCount bowelCount = new BowelCount();

							bowelCount.bowelDate = date;
							bowelCount.yearMonth = dateTime;
							bowelCount.userId = userId;

							if (BowelCountDao.getInstance().insert(activity,
									bowelCount)) {

								if (CommUtils.isAutoSync(activity)) {
									new SyncBowelTask(activity, bowelCount).execute();
								}
								
								String recordId = BowelCountDao.getInstance()
										.getBowelCountId(activity,
												bowelCount.bowelDate, userId);
								MyRecords myRecords = new MyRecords();

								myRecords.recordId = recordId;
								myRecords.type = 6;
								myRecords.userId = bowelCount.userId;
								myRecords.createDate = bowelCount.bowelDate;

								MyRecordsDao.getInstance().insert(activity,
										myRecords);

								String createDate = dateSF.format(mCalendar
										.getTime());
								if (MyRecordsDao.getInstance().getRecord(
										activity, createDate, userId)) {
									MyRecords records = new MyRecords();
									records.createDate = createDate;
									records.recordId = "";
									records.type = 7;
									records.userId = bowelCount.userId;
									MyRecordsDao.getInstance().insert(activity,
											records);
								}

								CommUtils.showToast(activity, "记录成功");
								Intent intent = new Intent(activity, MainActivity.class);
								activity.startActivity(intent);
							} else {
								CommUtils.showToast(activity, "记录失败");
							}
							dialog.dismiss();
							break;
						case 2:
							dialog.dismiss();
							break;
						default:
							break;
						}
					}
				});

		return builder.create();
	}

}
