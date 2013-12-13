package com.squad22.fit.task;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiACL;
import com.kii.cloud.storage.KiiACL.FileAction;
import com.kii.cloud.storage.KiiACLEntry;
import com.kii.cloud.storage.KiiAnonymousUser;
import com.kii.cloud.storage.KiiFile;
import com.kii.cloud.storage.KiiFileBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.squad22.fit.dao.MealDetailDao;
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.entity.Meal;
import com.squad22.fit.entity.MealDetail;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.FileCache;

public class SyncMealTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "SyncMealTask";
	Context mContext;
	Meal meal;

	public SyncMealTask(Context mContext, Meal meal) {
		this.meal = meal;
		this.mContext = mContext;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			KiiUser currentUser = KiiUser.getCurrentUser();
			if (currentUser != null) {
				KiiObject object = currentUser.bucket(Constants.MyMeal)
						.object();
				setObject(object, meal);
				object.save();

				meal.syncId = 1;
				MyMealDao.getInstance().updateMeal(mContext, meal);
			}
		} catch (Exception e) {
			Log.e(TAG, "--" + e.getMessage());
		}
		return null;
	}

	private void setObject(KiiObject object, Meal meal) {
		try {
			object.set(Constants.mealID, meal.mealId);
			object.set(Constants.mealTitle, meal.title);
			object.set(Constants.mealRecordDate, meal.createDate);
			object.set(Constants.mealComment, "");
			object.set(Constants.mealLike, meal.like);
			object.set(Constants.mealRemark, "");
			object.set(Constants.mealSyncID, 1);

			FileCache.CreateText();

			if (meal.image != null && meal.image.length() > 0) {

				File localFile = new File(meal.image);

				if (localFile != null) {
					KiiFileBucket fileBucket = Kii.fileBucket(Constants.MyMeal);

					KiiFile kiiFile = fileBucket.file(localFile);
					kiiFile.save();

					String url = kiiFile.publish();

					// 设置用户可读取图片
					KiiACL acl = kiiFile.acl();
					acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser.create(),
							FileAction.READ_EXISTING_OBJECT, true));
					acl.save();
					object.set(Constants.mealImage, url);
					object.set(Constants.mealImageUri, kiiFile.toUri());
				}
			} else {
				object.set(Constants.mealImage, "");
				object.set(Constants.mealImageUri, "");
			}
			JSONArray jsonArray = new JSONArray();
			ArrayList<MealDetail> mealDetaillist = MealDetailDao.getInstance()
					.getMealDetailById(mContext, meal.id);
			for (MealDetail mealDetail : mealDetaillist) {
				JSONObject jsonObj = new JSONObject();

				jsonObj.put(Constants.FOOD_ID, mealDetail.foodId);
				jsonObj.put(Constants.CATEGORY, mealDetail.Category);
				jsonObj.put(Constants.QTY, mealDetail.Amount);
				jsonObj.put(Constants.FOOD_UNIT, mealDetail.unit);
				jsonObj.put(Constants.KCAL, mealDetail.calorie);
				jsonObj.put(
						Constants.PORTION,
						mealDetail.portion.substring(0,
								mealDetail.portion.length() - 2));
				jsonObj.put(Constants.PORTION_UNIT, mealDetail.portion
						.substring(mealDetail.portion.length() - 2));

				jsonArray.put(jsonObj);
			}
			object.set(Constants.mealFood, jsonArray);
		} catch (Exception e) {

		}
	}
}
