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
import com.squad22.fit.entity.BowelCount;
import com.squad22.fit.utils.Constants;

@SuppressLint("SimpleDateFormat")
public class SyncDeleteBowelTask extends AsyncTask<Void, Void, Void> {
	Context mContext;
	BowelCount bowel;
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public SyncDeleteBowelTask(Context mContext, BowelCount bowel) {
		this.mContext = mContext;
		this.bowel = bowel;
	}

	@Override
	protected Void doInBackground(Void... params) {

		KiiUser currentUser = KiiUser.getCurrentUser();
		try {
			if (currentUser != null) {
				KiiQuery query = new KiiQuery(KiiClause.equals(
						Constants.dailycountRecordDate, bowel.yearMonth));
				KiiQueryResult<KiiObject> result = currentUser.bucket(
						Constants.MyDailycount).query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					String jsonStr = kiiObject
							.getString(Constants.dailycountBowel);
					JSONArray array = new JSONArray(jsonStr);
					JSONArray newArray = new JSONArray();
					JSONObject newJson = new JSONObject();
					try {
						for (int i = 0; i < array.length(); i++) {
							String bowelDate = array.getJSONObject(i)
									.getString(Constants.bowelCreateDate);
							if (bowelDate != null
									&& !bowel.bowelDate
											.equals(bowelDate)) {
								newJson.put(Constants.bowelCreateDate,
										bowelDate);
								newJson.put(Constants.bowelSyncID, 1);
							}
						}
					} catch (Exception e) {
					}
					newArray.put(newJson);
					kiiObject.set(Constants.dailycountBowel, newArray);
					kiiObject.save();
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

}
