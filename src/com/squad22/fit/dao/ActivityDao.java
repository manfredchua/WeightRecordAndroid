package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.ActivityEntity;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ActivityDao {

	private static final String TAG = "ActivityDao";

	private static ActivityDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private ActivityDao() {

	}

	public static synchronized ActivityDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new ActivityDao();
		}
		return mSingleton;
	}

	public ArrayList<ActivityEntity> getExerciseByName(Context context, String name) {
		 ArrayList<ActivityEntity> arrayList = new ArrayList<ActivityEntity>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.ActivityUri, null, "name like ?", new String[] { "%"+name+"%" }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					ActivityEntity info = new ActivityEntity();
					info.name = cursor.getString(cursor
							.getColumnIndex("name"));
					info.activityId = cursor.getString(cursor
							.getColumnIndex("activityId"));
					info.calorie1 = cursor.getInt(cursor
							.getColumnIndex("calorie1"));
					info.calorie2 = cursor.getInt(cursor
							.getColumnIndex("calorie2"));
					info.calorie3 = cursor.getInt(cursor
							.getColumnIndex("calorie3"));
					info.calorie4 = cursor.getInt(cursor
							.getColumnIndex("calorie4"));
					arrayList.add(info);
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return arrayList;
	}

	
	public ActivityEntity getExerciseById(Context context, String activityId) {
		ActivityEntity info = new ActivityEntity();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.ActivityUri, null, "activityId=?", new String[] { activityId}, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					
					info.name = cursor.getString(cursor
							.getColumnIndex("name"));
					info.activityId = cursor.getString(cursor
							.getColumnIndex("activityId"));
					info.calorie1 = cursor.getInt(cursor
							.getColumnIndex("calorie1"));
					info.calorie2 = cursor.getInt(cursor
							.getColumnIndex("calorie2"));
					info.calorie3 = cursor.getInt(cursor
							.getColumnIndex("calorie3"));
					info.calorie4 = cursor.getInt(cursor
							.getColumnIndex("calorie4"));
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return info;
	}
	
	
	public ArrayList<ActivityEntity> getAllExercise(Context context) {
		 ArrayList<ActivityEntity> arrayList = new ArrayList<ActivityEntity>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.ActivityUri, null, null, null, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					ActivityEntity info = new ActivityEntity();
					info.name = cursor.getString(cursor
							.getColumnIndex("name"));
					info.activityId = cursor.getString(cursor
							.getColumnIndex("activityId"));
					info.calorie1 = cursor.getInt(cursor
							.getColumnIndex("calorie1"));
					info.calorie2 = cursor.getInt(cursor
							.getColumnIndex("calorie2"));
					info.calorie3 = cursor.getInt(cursor
							.getColumnIndex("calorie3"));
					info.calorie4 = cursor.getInt(cursor
							.getColumnIndex("calorie4"));
					arrayList.add(info);
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return arrayList;
	}

	/**
	 * 新增运动记录
	 * 
	 * @param context
	 * @param Exercise
	 *            运动记录
	 * @return true 新增成功 false 新增失败
	 */
	public boolean insert(Context context, ActivityEntity info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("name", info.name);
			values.put("activityId", info.activityId);
			values.put("calorie1", info.calorie1);
			values.put("calorie2", info.calorie2);
			values.put("calorie3", info.calorie3);
			values.put("calorie4", info.calorie4);
			Uri uri = cr.insert(Constants.ActivityUri, values);
			if (uri != null) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	/**
	 * 批量新增运动记录
	 * 
	 * @param context
	 * @param arrayList
	 *            运动记录集合
	 * @return true 新增成功 false 新增失败
	 */
	public boolean bulkInsert(Context context, ArrayList<ActivityEntity> arrayList) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[arrayList.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], arrayList.get(i));
			}
			int count = cr.bulkInsert(Constants.ActivityUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, ActivityEntity info) {
		values.put("name", info.name);
		values.put("activityId", info.activityId);
		values.put("calorie1", info.calorie1);
		values.put("calorie2", info.calorie2);
		values.put("calorie3", info.calorie3);
		values.put("calorie4", info.calorie4);
	}

	/**
	 * 修改运动记录
	 * 
	 * @param context
	 * @param info
	 *            运动记录信息
	 * @return
	 */
	public int updateActivity(Context context, ActivityEntity info) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("name", info.name);
			values.put("activityId", info.activityId);
			values.put("calorie1", info.calorie1);
			values.put("calorie2", info.calorie2);
			values.put("calorie3", info.calorie3);
			values.put("calorie4", info.calorie4);
			result = cr.update(Constants.ActivityUri, values, "_id=?",
					new String[] { String.valueOf(info.id) });
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}

		return result;
	}

	/**
	 * 删除运动记录
	 * 
	 * @param context
	 * @param teamId
	 *            根据运动记录ID
	 * @return
	 */
	public int delActivity(Context context, String id) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			result = cr.delete(Constants.ActivityUri, "_id=?",
					new String[] { String.valueOf(id) });
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}
}
