package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.Exercise;
import com.squad22.fit.utils.Constants;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MyActivityDao {

	private static final String TAG = "MyActivityDao";

	private static MyActivityDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private MyActivityDao() {

	}

	public static synchronized MyActivityDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new MyActivityDao();
		}
		return mSingleton;
	}

	public ArrayList<Exercise> getAllActivityByDate(Context context, String date,String userId) {
		ArrayList<Exercise> arrayList = new ArrayList<Exercise>();

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MyActivityUri, null,
					"createDate like ? and syncId != 3 and userId=?",
					new String[] { (date + "%"), userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Exercise info = new Exercise();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.activityId = cursor.getString(cursor
							.getColumnIndex("activityId"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.title = cursor.getString(cursor.getColumnIndex("title"));
					info.remark = cursor.getString(cursor.getColumnIndex("remark"));
					info.comment = cursor.getString(cursor
							.getColumnIndex("comment"));
					info.like = cursor.getInt(cursor.getColumnIndex("like"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.userId = cursor
							.getString(cursor.getColumnIndex("userId"));
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
	
	
	public ArrayList<Exercise> getAllActivity(Context context,String userId) {
		ArrayList<Exercise> arrayList = new ArrayList<Exercise>();

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MyActivityUri, null,
					"syncId != 3 and userId=?",new String[]{userId}, "createDate desc limit 10");
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Exercise info = new Exercise();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.activityId = cursor.getString(cursor
							.getColumnIndex("activityId"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.title = cursor.getString(cursor.getColumnIndex("title"));
					info.remark = cursor.getString(cursor.getColumnIndex("remark"));
					info.comment = cursor.getString(cursor
							.getColumnIndex("comment"));
					info.like = cursor.getInt(cursor.getColumnIndex("like"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.userId = cursor
							.getString(cursor.getColumnIndex("userId"));
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
	
	
	public ArrayList<Exercise> getActivity(Context context,String syncId,String userId) {
		ArrayList<Exercise> arrayList = new ArrayList<Exercise>();

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MyActivityUri, null,
					"syncId = ? and userId=?",new String[]{syncId, userId}, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Exercise info = new Exercise();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.activityId = cursor.getString(cursor
							.getColumnIndex("activityId"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.title = cursor.getString(cursor.getColumnIndex("title"));
					info.remark = cursor.getString(cursor.getColumnIndex("remark"));
					info.comment = cursor.getString(cursor
							.getColumnIndex("comment"));
					info.like = cursor.getInt(cursor.getColumnIndex("like"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.userId = cursor
							.getString(cursor.getColumnIndex("userId"));
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
	
	public ArrayList<Exercise> getSyncDelActivity(Context context, String userId) {
		ArrayList<Exercise> arrayList = new ArrayList<Exercise>();

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MyActivityUri, null,
					"syncId=1 or syncId=2 and userId=?",new String[]{userId}, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Exercise info = new Exercise();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.activityId = cursor.getString(cursor
							.getColumnIndex("activityId"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.title = cursor.getString(cursor.getColumnIndex("title"));
					info.remark = cursor.getString(cursor.getColumnIndex("remark"));
					info.comment = cursor.getString(cursor
							.getColumnIndex("comment"));
					info.like = cursor.getInt(cursor.getColumnIndex("like"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.userId = cursor
							.getString(cursor.getColumnIndex("userId"));
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

	public String getActivityId(Context context, String date, String userId) {

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		String recordId = null;
		String[] projection = { "_id" };
		try {
			cursor = cr.query(Constants.MyActivityUri, projection,
					"activityId = ? and userId=?",
					new String[] { date, userId}, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					recordId = cursor.getString(cursor.getColumnIndex("_id"));

				}
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return recordId;
	}

	/**
	 * 查询运动记录的详情
	 * 
	 * @param context
	 * @param date
	 *            时间
	 * @return 运动记录的详情
	 */
	public Exercise getActivityById(Context context, String recordId) {
		Exercise info = new Exercise();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MyActivityUri, null, "_id = ?",
					new String[] { recordId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.userId = cursor.getString(cursor.getColumnIndex("userId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.activityId = cursor.getString(cursor
							.getColumnIndex("activityId"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.title = cursor.getString(cursor.getColumnIndex("title"));
					info.remark = cursor.getString(cursor.getColumnIndex("remark"));
					info.comment = cursor.getString(cursor
							.getColumnIndex("comment"));
					info.like = cursor.getInt(cursor.getColumnIndex("like"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.userId = cursor
							.getString(cursor.getColumnIndex("userId"));

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

	/**
	 * 新增运动记录
	 * 
	 * @param context
	 * @param Exercise
	 *            运动记录
	 * @return true 新增成功 false 新增失败
	 */
	public boolean insert(Context context, Exercise info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("createDate", info.createDate);
			values.put("activityId", info.activityId);
			values.put("title", info.title);
			values.put("remark", info.remark);
			values.put("comment", info.comment);
			values.put("image", info.image);
			values.put("like", info.like);
			values.put("syncId", info.syncId);
			values.put("userId", info.userId);
			Uri uri = cr.insert(Constants.MyActivityUri, values);
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
	public boolean bulkInsert(Context context, ArrayList<Exercise> arrayList) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[arrayList.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], arrayList.get(i));
			}
			int count = cr.bulkInsert(Constants.MyActivityUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, Exercise info) {
		values.put("createDate", info.createDate);
		values.put("activityId", info.activityId);
		values.put("title", info.title);
		values.put("remark", info.remark);
		values.put("comment", info.comment);
		values.put("image", info.image);
		values.put("like", info.like);
		values.put("syncId", info.syncId);
		values.put("userId", info.userId);
	}

	/**
	 * 修改运动记录
	 * 
	 * @param context
	 * @param info
	 *            运动记录信息
	 * @return
	 */
	public int updateActivity(Context context, Exercise info) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("createDate", info.createDate);
			values.put("activityId", info.activityId);
			values.put("title", info.title);
			values.put("remark", info.remark);
			values.put("comment", info.comment);
			values.put("image", info.image);
			values.put("like", info.like);
			values.put("syncId", info.syncId);
			result = cr.update(Constants.MyActivityUri, values, "_id=?",
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
	public int delActivity(Context context, Exercise exercise) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			
			if(exercise.syncId == 1 || exercise.syncId == 2){
				ContentValues values = new ContentValues();
				values.put("syncId", 3);
				result = cr.update(Constants.MyActivityUri, values, "_id=?",
						new String[] { String.valueOf(exercise.id) });
			}else{
				result = cr.delete(Constants.MyActivityUri,"_id=?",
						new String[] { String.valueOf(exercise.id) });
				ActivityDetailDao.getInstance().delActivity(context, exercise.id);
			}
			
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}
}
