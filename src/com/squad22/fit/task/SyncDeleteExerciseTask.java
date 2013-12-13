package com.squad22.fit.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.kii.cloud.storage.KiiFile;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.query.KiiClause;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.entity.Exercise;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.utils.Constants;

@SuppressLint("SimpleDateFormat")
public class SyncDeleteExerciseTask extends AsyncTask<Void, Void, Void> {
	Context mContext;
	Exercise exercise;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");

	public SyncDeleteExerciseTask(Context mContext, Exercise exercise) {
		this.mContext = mContext;
		this.exercise = exercise;
	}

	@Override
	protected Void doInBackground(Void... params) {
		KiiUser currentUser = KiiUser.getCurrentUser();
		try {
			KiiQuery query = new KiiQuery(KiiClause.equals(
					Constants.activityID, exercise.activityId));
			KiiQueryResult<KiiObject> result = currentUser.bucket(
					Constants.MyActivity).query(query);
			List<KiiObject> listObj = result.getResult();
			for (KiiObject kiiObject : listObj) {

				try {
					Uri uri = kiiObject.getUri(Constants.mealImageUri);
					if (uri != null) {
						KiiFile file = KiiFile.createByUri(uri);
						file.delete();
					}
				} catch (Exception e) {
				}

				kiiObject.delete();
				int count = MyActivityDao.getInstance().delActivity(mContext,
						exercise);
				if (count > 0) {
					MyRecordsDao.getInstance().delRecords(mContext,
							exercise.id, "2");
					Date date = dateFormat.parse(exercise.createDate);
					String currentDate = dateSF.format(date);
					ArrayList<MyRecords> arrayList = MyRecordsDao.getInstance()
							.getAllRecordByDate(mContext, currentDate,
									exercise.userId);
					if (arrayList != null && arrayList.size() == 0) {
						MyRecordsDao.getInstance().delSummaryRecords(mContext);
					}

				}
			}
		} catch (Exception e) {
			Log.e("Exception", "--" + e.getMessage());
		}

		return null;
	}
}
