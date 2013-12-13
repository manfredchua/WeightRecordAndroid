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
import com.squad22.fit.dao.ActivityDetailDao;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.entity.Exercise;
import com.squad22.fit.entity.ExerciseDetail;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.FileCache;

public class SyncUpdateExerciseTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "SyncUpdateExerciseTask";
	Context mContext;
	Exercise exercise;

	public SyncUpdateExerciseTask(Context mContext, Exercise exercise) {
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
				setActivityObject(kiiObject, exercise);
				kiiObject.save();

				exercise.syncId = 1;
				MyActivityDao.getInstance().updateActivity(
						mContext, exercise);
			}
		} catch (Exception e) {
			Log.e(TAG, "--" + e.getMessage());
		}
		return null;
	}



private void setActivityObject(KiiObject object, Exercise exercise) {
	try {
		object.set(Constants.activityID, exercise.activityId);
		object.set(Constants.activityTitle, exercise.title);
		object.set(Constants.activityRecordDate, exercise.createDate);
		object.set(Constants.activityComment, "");
		object.set(Constants.activityLike, exercise.like);
		object.set(Constants.activityRemark, "");
		object.set(Constants.activitySyncID, 1);

		FileCache.CreateText();

		if (exercise.image != null && exercise.image.length() > 0) {

			File localFile = new File(exercise.image);
			Uri uri = object.getUri(Constants.mealImageUri);
			if (uri != null) {
				if (localFile != null) {
					KiiFile file = KiiFile.createByUri(uri);
					file.save(localFile);

					String url = file.publish();

					// 设置用户可读取图片
					KiiACL acl = file.acl();
					acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser
							.create(), FileAction.READ_EXISTING_OBJECT,
							true));
					acl.save();
					object.set(Constants.activityImage, url);
					object.set(Constants.mealImageUri, file.toUri());
				}
			} else {
				if (localFile != null) {
					KiiFileBucket fileBucket = Kii
							.fileBucket(Constants.MyActivity);

					KiiFile kiiFile = fileBucket.file(localFile);
					kiiFile.save();

					String url = kiiFile.publish();

					// 设置用户可读取图片
					KiiACL acl = kiiFile.acl();
					acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser
							.create(), FileAction.READ_EXISTING_OBJECT,
							true));
					acl.save();
					object.set(Constants.activityImage, url);
					object.set(Constants.mealImageUri, kiiFile.toUri());
				}
			}
		} else {
			object.set(Constants.activityImage, "");
			object.set(Constants.mealImageUri, "");
		}
		JSONArray jsonArray = new JSONArray();
		ArrayList<ExerciseDetail> exerciseDetaillist = ActivityDetailDao
				.getInstance().getActivityDetailById(
						mContext, exercise.id);
		for (ExerciseDetail exerciseDetail : exerciseDetaillist) {
			JSONObject jsonObj = new JSONObject();

			jsonObj.put(Constants.EXERCISE_ID, exerciseDetail.activityId);
			jsonObj.put(Constants.TIME, exerciseDetail.time);
			jsonObj.put(Constants.CALORIE, exerciseDetail.calorie);
			jsonObj.put(Constants.FOOD_UNIT, exerciseDetail.unit);
			jsonArray.put(jsonObj);
		}
		object.set(Constants.activityExercise, jsonArray);
	} catch (Exception e) {

	}
}
}
