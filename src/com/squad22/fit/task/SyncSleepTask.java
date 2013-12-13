package com.squad22.fit.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.squad22.fit.dao.SleepDao;
import com.squad22.fit.entity.Sleep;
import com.squad22.fit.utils.Constants;

public class SyncSleepTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "SyncSleepTask";
	Context mContext;
	Sleep sleep;

	public SyncSleepTask(Context mContext, Sleep sleep) {
		this.sleep = sleep;
		this.mContext = mContext;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			KiiUser currentUser = KiiUser.getCurrentUser();
			if (currentUser != null) {
				KiiObject object = currentUser.bucket(Constants.MySleep)
						.object();
				object.set(Constants.sleepID, sleep.sleepId);
				object.set(Constants.sleepSyncID, 1);
				object.set(Constants.sleepReportDate, sleep.createDate);
				object.set(Constants.sleepStart, sleep.sleepStart);
				object.set(Constants.sleepEnd, sleep.sleepEnd);
				object.save();
				
				sleep.syncId = 1;
				SleepDao.getInstance().updateSleep(mContext, sleep);
			}
		} catch (Exception e) {
			Log.e(TAG, "--" + e.getMessage());
		}
		return null;
	}

	
}
