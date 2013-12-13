package com.squad22.fit.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.query.KiiClause;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.dao.SleepDao;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.entity.Sleep;
import com.squad22.fit.utils.Constants;

@SuppressLint("SimpleDateFormat")
public class SyncDeleteSleepTask extends AsyncTask<Void, Void, Void> {
	Context mContext;
	Sleep sleep;
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public SyncDeleteSleepTask(Context mContext, Sleep sleep) {
		this.mContext = mContext;
		this.sleep = sleep;
	}

	@Override
	protected Void doInBackground(Void... params) {

		KiiUser currentUser = KiiUser.getCurrentUser();
		try {
			if (currentUser != null) {
				KiiQuery query = new KiiQuery(KiiClause.equals(
						Constants.sleepID, sleep.sleepId));
				KiiQueryResult<KiiObject> result = currentUser.bucket(
						Constants.MySleep).query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					kiiObject.delete();
					int count = SleepDao.getInstance().delSleep(
							mContext, sleep);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(
								mContext, sleep.id, "4");
						Date date = dateFormat.parse(sleep.createDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(
										mContext, currentDate,
										sleep.userId);
						if (arrayList != null && arrayList.size() == 0) {
							MyRecordsDao.getInstance().delSummaryRecords(
									mContext);
						}
					}
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

}
