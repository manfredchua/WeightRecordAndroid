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
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.entity.Meal;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.utils.Constants;

@SuppressLint("SimpleDateFormat")
public class SyncDeleteMealTask extends AsyncTask<Void, Void, Void> {
	Context mContext;
	Meal meal;
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public SyncDeleteMealTask(Context mContext, Meal meal) {
		this.mContext = mContext;
		this.meal = meal;
	}

	@Override
	protected Void doInBackground(Void... params) {

		KiiUser currentUser = KiiUser.getCurrentUser();
		try {
			if (currentUser != null) {
				KiiQuery query = new KiiQuery(KiiClause.equals(
						Constants.mealID, meal.mealId));

				KiiQueryResult<KiiObject> result = currentUser.bucket(
						Constants.MyMeal).query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					kiiObject.delete();
					int count = MyMealDao.getInstance().delMeal(
							mContext, meal);
					if (count > 0) {
						MyRecordsDao.getInstance().delRecords(
								mContext, meal.id, "1");
						Date date = dateFormat.parse(meal.createDate);
						String currentDate = dateSF.format(date);
						ArrayList<MyRecords> arrayList = MyRecordsDao
								.getInstance().getAllRecordByDate(
										mContext,
										currentDate, meal.userId);
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
