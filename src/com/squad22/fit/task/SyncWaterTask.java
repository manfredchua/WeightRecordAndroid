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
import com.squad22.fit.dao.WaterCountDao;
import com.squad22.fit.entity.WaterCount;
import com.squad22.fit.utils.Constants;

public class SyncWaterTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "SyncSleepTask";
	Context mContext;
	WaterCount water;

	public SyncWaterTask(Context mContext, WaterCount water) {
		this.water = water;
		this.mContext = mContext;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			KiiUser currentUser = KiiUser.getCurrentUser();
			if (currentUser != null) {
				KiiQuery query = new KiiQuery(KiiClause.equals(
						Constants.dailycountRecordDate, water.yearMonth));
				KiiQueryResult<KiiObject> result = currentUser.bucket(
						Constants.MyDailycount).query(query);
				List<KiiObject> listObj = result.getResult();
				if (listObj != null && listObj.size() > 0) {
					for (KiiObject kiiObject : listObj) {
						String jsonStr = kiiObject
								.getString(Constants.dailycountWater);
						JSONArray array = new JSONArray();
						if (jsonStr != null && jsonStr.length() > 0) {
							array = new JSONArray(jsonStr);
						}
						JSONObject jsonObj = new JSONObject();
						jsonObj.put(Constants.waterCreateDate, water.waterDate);
						jsonObj.put(Constants.waterSyncID, 1);

						array.put(jsonObj);
						kiiObject.set(Constants.dailycountWater, array);

						kiiObject.save();
						
						water.syncId = 1;
						WaterCountDao.getInstance().updateWater(mContext, water);
					}
				} else {
					KiiObject object = currentUser.bucket(
							Constants.MyDailycount).object();
					object.set(Constants.dailycountRecordDate, water.yearMonth);
					object.set(Constants.dailycountBowel,
							new JSONObject().toString());
					JSONObject jsonObj = new JSONObject();
					jsonObj.put(Constants.waterCreateDate, water.waterDate);
					jsonObj.put(Constants.waterSyncID, 1);

					JSONArray array = new JSONArray();
					array.put(jsonObj);
					object.set(Constants.dailycountWater, array);
					object.save();
					
					water.syncId = 1;
					WaterCountDao.getInstance().updateWater(mContext, water);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "--" + e.getMessage());
		}
		return null;
	}

}
