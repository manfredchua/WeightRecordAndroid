package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MyRecordsDao {

	private static final String TAG = "MealDao";

	private static MyRecordsDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private MyRecordsDao() {

	}

	public static synchronized MyRecordsDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new MyRecordsDao();
		}
		return mSingleton;
	}

	/**
	 * 查询所以记录
	 * 
	 * @param context
	 * @param date
	 *            时间
	 * @return 记录集合
	 */
	public ArrayList<MyRecords> getAllRecordByDate(Context context,
			String date, String userId) {
		ArrayList<MyRecords> arrayList = new ArrayList<MyRecords>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MyRecordUri, null,
					"createDate like ? and type!=7 and userId=?", new String[] {
							date + "%", userId }, "createDate desc");
			if (null != cursor) {
				while (cursor.moveToNext()) {
					MyRecords info = new MyRecords();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.recordId = cursor.getString(cursor
							.getColumnIndex("recordId"));
					info.type = cursor.getInt(cursor.getColumnIndex("type"));

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

	public ArrayList<MyRecords> getRecordByDate(Context context, String userId) {
		ArrayList<MyRecords> arrayList = new ArrayList<MyRecords>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MyRecordUri, null, "userId=?",
					new String[] { userId }, "createDate desc");
			if (null != cursor) {
				while (cursor.moveToNext()) {
					MyRecords info = new MyRecords();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.recordId = cursor.getString(cursor
							.getColumnIndex("recordId"));
					info.type = cursor.getInt(cursor.getColumnIndex("type"));
					info.userId = cursor.getString(cursor.getColumnIndex("userId"));

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

	public ArrayList<MyRecords> getRecord(Context context, String userId) {
		ArrayList<MyRecords> arrayList = new ArrayList<MyRecords>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MyRecordUri, null, "userId=?",
					new String[] { userId }, "createDate desc");
			if (null != cursor) {
				while (cursor.moveToNext()) {
					MyRecords info = new MyRecords();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.recordId = cursor.getString(cursor
							.getColumnIndex("recordId"));
					info.type = cursor.getInt(cursor.getColumnIndex("type"));

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

	public boolean getRecord(Context context, String date, String userId) {
		boolean isRecord = true;
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MyRecordUri, null,
					"createDate=? and type=7 and userId=?", new String[] {
							date, userId }, null);
			if (null != cursor && cursor.getCount() > 0) {
				isRecord = false;
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return isRecord;
	}

	/**
	 * 新增记录
	 * 
	 * @param context
	 * @param MyRecords
	 *            记录
	 * @return true 新增成功 false 新增失败
	 */
	public boolean insert(Context context, MyRecords info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("createDate", info.createDate);
			values.put("recordId", info.recordId);
			values.put("type", info.type);
			values.put("userId", info.userId);
			Uri uri = cr.insert(Constants.MyRecordUri, values);
			if (uri != null) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	/**
	 * 批量新增记录
	 * 
	 * @param context
	 * @param arrayList
	 *            记录集合
	 * @return true 新增成功 false 新增失败
	 */
	public boolean bulkInsert(Context context, ArrayList<MyRecords> arrayList) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[arrayList.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], arrayList.get(i));
			}
			int count = cr.bulkInsert(Constants.MyRecordUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, MyRecords info) {
		values.put("createDate", info.createDate);
		values.put("recordId", info.recordId);
		values.put("type", info.type);
		values.put("userId", info.userId);
	}

	public int updateRecords(Context context, MyRecords info) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("createDate", info.createDate);
			result = cr.update(Constants.MyRecordUri, values,
					"recordId=? and type=?", new String[] { info.recordId,
							String.valueOf(info.type) });
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}

		return result;
	}

	/**
	 * 删除
	 * 
	 * @param context
	 * @param id
	 * @param type
	 *            1代表饮食,2代表运动,3代表测量,4代表睡眠,5代表喝水,6代表排泄,7今日总结
	 * @return
	 */
	public int delRecords(Context context, String id, String type) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			result = cr.delete(Constants.MyRecordUri, "recordId=? and type=?",
					new String[] { id, type });
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}

	public int delSummaryRecords(Context context) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			result = cr.delete(Constants.MyRecordUri, "type=7", null);
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}
}
