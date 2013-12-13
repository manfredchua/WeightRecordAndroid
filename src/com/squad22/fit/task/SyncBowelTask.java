package com.squad22.fit.task;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.query.KiiClause;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squad22.fit.dao.BowelCountDao;
import com.squad22.fit.entity.BowelCount;
import com.squad22.fit.utils.Constants;

public class SyncBowelTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "SyncSleepTask";
	Context mContext;
	BowelCount bowel;

	public SyncBowelTask(Context mContext, BowelCount bowel) {
		this.bowel = bowel;
		this.mContext = mContext;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			KiiUser currentUser = KiiUser.getCurrentUser();
			if (currentUser != null) {
				KiiQuery query = new KiiQuery(KiiClause.equals(
						Constants.dailycountRecordDate, bowel.yearMonth));
				KiiQueryResult<KiiObject> result = currentUser.bucket(
						Constants.MyDailycount).query(query);
				List<KiiObject> listObj = result.getResult();
				if (listObj != null && listObj.size() > 0) {
					for (KiiObject kiiObject : listObj) {
						String jsonStr = kiiObject
								.getString(Constants.dailycountBowel);
						JSONArray array = new JSONArray();
						if (jsonStr != null && jsonStr.length() > 0) {
							array = new JSONArray(jsonStr);
						}
						JSONObject jsonObj = new JSONObject();
						jsonObj.put(Constants.bowelCreateDate, bowel.bowelDate);
						jsonObj.put(Constants.bowelSyncID, 1);
						array.put(jsonObj);
						kiiObject.set(Constants.dailycountBowel, array);
						kiiObject.save();
						
						bowel.syncId = 1;
						BowelCountDao.getInstance().updateBowel(mContext, bowel);
					}
				} else {
					KiiObject object = currentUser.bucket(
							Constants.MyDailycount).object();
					object.set(Constants.dailycountRecordDate, bowel.yearMonth);
					object.set(Constants.dailycountWater,
							new JSONObject().toString());
					JSONObject jsonObj = new JSONObject();
					jsonObj.put(Constants.bowelCreateDate, bowel.bowelDate);
					jsonObj.put(Constants.bowelSyncID, 1);
					JSONArray array = new JSONArray();
					array.put(jsonObj);
					object.set(Constants.dailycountBowel, array);
					object.save();
					bowel.syncId = 1;
					BowelCountDao.getInstance().updateBowel(mContext, bowel);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "--" + e.getMessage());
		}
		return null;
	}

}
