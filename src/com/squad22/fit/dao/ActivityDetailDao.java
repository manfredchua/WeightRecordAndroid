package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.ExerciseDetail;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ActivityDetailDao {

	private static final String TAG = "ActivityDetailDao";

	private static ActivityDetailDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private ActivityDetailDao() {

	}

	public static synchronized ActivityDetailDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new ActivityDetailDao();
		}
		return mSingleton;
	}
	

	/**
	 * 查询运动记录类型的详情
	 * 
	 * @param context
	 * @param recordId
	 *            根据运动记录类型的Id
	 * @return 运动记录类型的详情
	 */
	public ArrayList<ExerciseDetail> getActivityDetailById(Context context, String recordId) {
		
		 ArrayList<ExerciseDetail> arrayList = new ArrayList<ExerciseDetail>();
	
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.ActivityDetailUri, null, "recordId = ?", new String[] { recordId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					ExerciseDetail info = new ExerciseDetail();
					
					info.id = cursor.getString(cursor
							.getColumnIndex("_id"));
					info.activityId = cursor.getString(cursor
							.getColumnIndex("activityId"));
					info.name = cursor.getString(cursor
							.getColumnIndex("name"));
					info.time = cursor.getString(cursor
							.getColumnIndex("time"));
					info.recordId = cursor.getString(cursor
							.getColumnIndex("recordId"));
					info.calorie = cursor.getString(cursor
							.getColumnIndex("calorie"));
					info.unit = cursor.getString(cursor
							.getColumnIndex("unit"));
					
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
	
	
	public ArrayList<String> getActivityId(Context context) {
		
		 ArrayList<String> arrayList = new ArrayList<String>();
	
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.ActivityDetailUri, null, null, null, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					
					String activityId = cursor.getString(cursor
							.getColumnIndex("activityId"));
					
					
					arrayList.add(activityId);
					
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
	 * 新增运动记录类型的详情
	 * 
	 * @param context
	 * @param info 类型的详情
	 * @return true 新增成功 false 新增失败
	 */
	public boolean insert(Context context, ExerciseDetail info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("name", info.name);
			values.put("recordId", info.recordId);
			values.put("time", info.time);
			values.put("calorie", info.calorie);
			values.put("unit", info.unit);
			values.put("activityId", info.activityId);
			Uri uri = cr.insert(Constants.ActivityDetailUri, values);
			if (uri != null) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	/**
	 * 批量新增运动记录类型的详情
	 * 
	 * @param context
	 * @param detail
	 *           类型的详情
	 * @return true 新增成功 false 新增失败
	 */
	public boolean bulkInsert(Context context, ArrayList<ExerciseDetail> detail) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[detail.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], detail.get(i));
			}
			int count = cr.bulkInsert(Constants.ActivityDetailUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, ExerciseDetail info) {
		values.put("name", info.name);
		values.put("recordId", info.recordId);
		values.put("time", info.time);
		values.put("calorie", info.calorie);
		values.put("unit", info.unit);
		values.put("activityId", info.activityId);
	}
	
	

	/**
	 * 修改运动记录类型的详情
	 * 
	 * @param context
	 * @param info
	 *            类型的详情
	 * @return
	 */
	public int updateActivityDetail(Context context, ExerciseDetail info) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("time", info.time);
			values.put("calorie", info.calorie);
			values.put("name", info.name);
			values.put("unit", info.unit);
			result = cr.update(Constants.ActivityDetailUri, values,
					 "_id=?",
					new String[] { String.valueOf(info.id) });
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}

		return result;
	}
	
	/**
	 * 删除类型的详情
	 * @param context
	 * @param Id 
	 * @return
	 */
	  public int delActivity(Context context, String id) {
		    int result = 0;
		    try {
		      ContentResolver cr = context.getContentResolver();
		   
				result = cr.delete(Constants.ActivityDetailUri, "_id=?", new String[]{id});
		    } catch (Exception e) {
		    	Log.i(TAG, e + "------" + e.getMessage());
		    }
		    return result;
	}
	  
	  public int delRecordActivity(Context context, String id) {
		    int result = 0;
		    try {
		      ContentResolver cr = context.getContentResolver();
		   
				result = cr.delete(Constants.ActivityDetailUri, "recordId=?", new String[]{id});
		    } catch (Exception e) {
		    	Log.i(TAG, e + "------" + e.getMessage());
		    }
		    return result;
	}
}
