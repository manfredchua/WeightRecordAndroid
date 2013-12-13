package com.squad22.fit.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
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
import com.kii.cloud.storage.query.KiiClause;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squad22.fit.dao.MealDetailDao;
import com.squad22.fit.dao.MyMealDao;
import com.squad22.fit.entity.Meal;
import com.squad22.fit.entity.MealDetail;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.FileCache;

public class SyncUpdateMealTask extends AsyncTask<Void, Void, Void>{
	private static final String TAG = "SyncUpdateMealTask";
	Context mContext;
	Meal meal;
	
	public SyncUpdateMealTask(Context mContext, Meal meal){
		this.mContext = mContext;
		this.meal = meal;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		KiiUser currentUser = KiiUser.getCurrentUser();
		try{
			if(currentUser != null){
				KiiQuery query = new KiiQuery(KiiClause.equals(
						Constants.mealID, meal.mealId));
				KiiQueryResult<KiiObject> result = currentUser.bucket(
						Constants.MyMeal).query(query);
				List<KiiObject> listObj = result.getResult();
				for (KiiObject kiiObject : listObj) {
					setObject(kiiObject, meal);
					kiiObject.save();
	
					meal.syncId = 1;
					MyMealDao.getInstance().updateMeal(
							mContext, meal);
				}
			}
		}catch(Exception e){
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

			Uri uri = object.getUri(Constants.mealImageUri);
			if(uri != null){
				if (localFile != null) {
					KiiFile file = KiiFile.createByUri(uri);
					file.save(localFile);
					String url = file.publish();
					
					object.set(Constants.mealImage, url);
					object.set(Constants.mealImageUri, file.toUri());
				}
			}else{
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
		Log.e(TAG, "--" + e.getMessage());

	}
}
}
