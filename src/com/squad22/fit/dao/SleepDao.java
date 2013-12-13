package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.Sleep;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SleepDao {
	private static final String TAG = "SleepDao";

	private static SleepDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private SleepDao() {

	}

	public static synchronized SleepDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new SleepDao();
		}
		return mSingleton;
	}

	/**
	 * 查询睡眠记录的详情
	 * 
	 * @param context
	 * @param date
	 *            时间
	 * @return 排泄记录的详情
	 */
	public ArrayList<Sleep> getSleepByDate(Context context, String date,
			String userId) {
		ArrayList<Sleep> arrayList = new ArrayList<Sleep>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.SleepUri, null,
					"createDate like ? and userId=?", new String[] {
							(date + "%"), userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Sleep info = new Sleep();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.sleepId = cursor.getString(cursor
							.getColumnIndex("sleepId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.sleepStart = cursor.getString(cursor
							.getColumnIndex("sleepStart"));
					info.sleepEnd = cursor.getString(cursor
							.getColumnIndex("sleepEnd"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));

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

	public ArrayList<Sleep> getSleepBySyncId(Context context, String syncId,
			String userId) {
		ArrayList<Sleep> arrayList = new ArrayList<Sleep>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.SleepUri, null,
					"syncId=? and userId=?", new String[] { syncId, userId },
					null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Sleep info = new Sleep();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.sleepId = cursor.getString(cursor
							.getColumnIndex("sleepId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.sleepStart = cursor.getString(cursor
							.getColumnIndex("sleepStart"));
					info.sleepEnd = cursor.getString(cursor
							.getColumnIndex("sleepEnd"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));

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

	public ArrayList<Sleep> getSyncDelSleep(Context context, String userId) {
		ArrayList<Sleep> arrayList = new ArrayList<Sleep>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.SleepUri, null,
					"syncId=1 or syncId=2 and userId=?",
					new String[] { userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Sleep info = new Sleep();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.sleepId = cursor.getString(cursor
							.getColumnIndex("sleepId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.sleepStart = cursor.getString(cursor
							.getColumnIndex("sleepStart"));
					info.sleepEnd = cursor.getString(cursor
							.getColumnIndex("sleepEnd"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));

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

	public Sleep getSleepById(Context context, String recordId) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		Sleep info = new Sleep();
		try {
			cursor = cr.query(Constants.SleepUri, null, "_id=?",
					new String[] { recordId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {

					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.sleepId = cursor.getString(cursor
							.getColumnIndex("sleepId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.sleepStart = cursor.getString(cursor
							.getColumnIndex("sleepStart"));
					info.sleepEnd = cursor.getString(cursor
							.getColumnIndex("sleepEnd"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));

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

	public String getSleepId(Context context, String sleepId, String userId) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		String recordId = null;
		String[] projection = { "_id" };
		try {
			cursor = cr.query(Constants.SleepUri, projection,
					"sleepId=? and userId=?", new String[] { sleepId, userId },
					null);
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
	 * 新增睡眠记录
	 * 
	 * @param context
	 * @param info
	 *            睡眠记录
	 * @return true 新增成功 false 新增失败
	 */
	public boolean insert(Context context, Sleep info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("sleepStart", info.sleepStart);
			values.put("sleepEnd", info.sleepEnd);
			values.put("syncId", info.syncId);
			values.put("createDate", info.createDate);
			values.put("sleepId", info.sleepId);
			values.put("userId", info.userId);
			Uri uri = cr.insert(Constants.SleepUri, values);
			if (uri != null) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	/**
	 * 批量新增睡眠记录
	 * 
	 * @param context
	 * @param arrayList
	 *            睡眠记录集合
	 * @return true 新增成功 false 新增失败
	 */
	public boolean bulkInsert(Context context, ArrayList<Sleep> arrayList) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[arrayList.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], arrayList.get(i));
			}
			int count = cr.bulkInsert(Constants.SleepUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, Sleep info) {
		values.put("sleepStart", info.sleepStart);
		values.put("sleepEnd", info.sleepEnd);
		values.put("syncId", info.syncId);
		values.put("createDate", info.createDate);
		values.put("sleepId", info.sleepId);
		values.put("userId", info.userId);
	}

	public int updateSleep(Context context, Sleep sleep) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();

			ContentValues values = new ContentValues();
			values.put("syncId", sleep.syncId);
			result = cr.update(Constants.SleepUri, values, "_id=?",
					new String[] { String.valueOf(sleep.id) });
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}

	public int delSleep(Context context, Sleep sleep) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();

			if (sleep.syncId == 1 || sleep.syncId == 2) {
				ContentValues values = new ContentValues();
				values.put("syncId", 3);
				result = cr.update(Constants.SleepUri, values, "_id=?",
						new String[] { String.valueOf(sleep.id) });
			} else {
				result = cr.delete(Constants.SleepUri, "_id=?",
						new String[] { String.valueOf(sleep.id) });
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}
}
