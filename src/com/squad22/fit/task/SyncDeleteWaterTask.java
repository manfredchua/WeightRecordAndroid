package com.squad22.fit.task;

import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.query.KiiClause;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squad22.fit.entity.WaterCount;
import com.squad22.fit.utils.Constants;

@SuppressLint("SimpleDateFormat")
public class SyncDeleteWaterTask extends AsyncTask<Void, Void, Void> {
	Context mContext;
	WaterCount water;
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public SyncDeleteWaterTask(Context mContext, WaterCount water) {
		this.mContext = mContext;
		this.water = water;
	}

	@Override
	protected Void doInBackground(Void... params) {

		KiiUser currentUser = KiiUser.getCurrentUser();
		try {
			if (currentUser != null) {
				KiiQuery query = new KiiQuery(KiiClause.equals(
						Constants.dailycountRecordDate, water.yearMonth));
				KiiQueryResult<KiiObject> result = currentUser.bucket(
						Constants.MyDailycount).query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					String jsonStr = kiiObject
							.getString(Constants.dailycountWater);
					JSONArray array = new JSONArray(jsonStr);
					JSONObject newJson = new JSONObject();
					JSONArray newArray = new JSONArray();
					try {
						for (int i = 0; i < array.length(); i++) {
							String waterDate = array.getJSONObject(i)
									.getString(Constants.waterCreateDate);
							if (waterDate != null
									&& !water.waterDate
											.equals(waterDate)) {
								newJson.put(Constants.waterCreateDate,
										waterDate);
								newJson.put(Constants.waterSyncID, 1);

							}
						}
					} catch (Exception e) {
					}
					newArray.put(newJson);
					kiiObject.set(Constants.dailycountWater, newArray);
					kiiObject.save();
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

}
