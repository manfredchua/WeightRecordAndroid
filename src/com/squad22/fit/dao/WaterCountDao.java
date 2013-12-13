package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.WaterCount;
import com.squad22.fit.provider.WeightRecordBaseHelper;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class WaterCountDao {
	private static final String TAG = "WaterCountDao";

	private static WaterCountDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private WaterCountDao() {

	}

	public static synchronized WaterCountDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new WaterCountDao();
		}
		return mSingleton;
	}

	/**
	 * 查询排泄记录的详情
	 * 
	 * @param context
	 * @param date
	 *            时间
	 * @return 排泄记录的详情
	 */
	public ArrayList<WaterCount> getWaterCountByDate(Context context,
			String date, String userId) {
		ArrayList<WaterCount> arrayList = new ArrayList<WaterCount>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.WaterCountUri, null,
					"waterDate like ? and userId=?", new String[] {
							(date + "%"), userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					WaterCount info = new WaterCount();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.waterDate = cursor.getString(cursor
							.getColumnIndex("waterDate"));
					info.yearMonth = cursor.getString(cursor
							.getColumnIndex("yearMoth"));
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

	public ArrayList<WaterCount> getWaterCount(Context context, String date,
			String userId) {
		ArrayList<WaterCount> arrayList = new ArrayList<WaterCount>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.WaterCountUri, null,
					"waterDate like ? and syncId=0 and userId=?", new String[] {
							(date + "%"), userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					WaterCount info = new WaterCount();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.waterDate = cursor.getString(cursor
							.getColumnIndex("waterDate"));
					info.yearMonth = cursor.getString(cursor
							.getColumnIndex("yearMoth"));
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

	public ArrayList<String> getGroupDate(Context context, String userId) {
		ArrayList<String> groupDate = new ArrayList<String>();
		Cursor cursor = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = WeightRecordBaseHelper.getInstance(context)
				.getReadableDatabase();
		qb.setTables(WeightRecordBaseHelper.MY_WATER_COUNT_TABLE_NAME);

		try {
			cursor = qb.query(db, new String[] { "yearMoth" }, "userId=?",
					new String[] { userId }, "yearMoth", null, "yearMoth desc");
			if (null != cursor) {
				while (cursor.moveToNext()) {
					String date = cursor.getString(cursor
							.getColumnIndex("yearMoth"));
					groupDate.add(date);
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return groupDate;
	}

	public ArrayList<WaterCount> getWaterCountBySyncId(Context context,
			String syncId, String userId) {
		ArrayList<WaterCount> arrayList = new ArrayList<WaterCount>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.WaterCountUri, null,
					"syncId = ? and userId=?", new String[] { syncId, userId },
					null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					WaterCount info = new WaterCount();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.waterDate = cursor.getString(cursor
							.getColumnIndex("waterDate"));
					info.yearMonth = cursor.getString(cursor
							.getColumnIndex("yearMoth"));
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

	public ArrayList<WaterCount> getSyncDelWaterCount(Context context,
			String userId) {
		ArrayList<WaterCount> arrayList = new ArrayList<WaterCount>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.WaterCountUri, null,
					"syncId=1 or syncId=2 and userId=?",
					new String[] { userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					WaterCount info = new WaterCount();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.waterDate = cursor.getString(cursor
							.getColumnIndex("waterDate"));
					info.yearMonth = cursor.getString(cursor
							.getColumnIndex("yearMoth"));
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

	public String getWaterCountId(Context context, String waterDate,
			String userId) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		String recordId = null;
		String[] projection = { "_id" };
		try {
			cursor = cr.query(Constants.WaterCountUri, projection,
					"waterDate=? and userId=?", new String[] { waterDate,
							userId }, null);
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

	public WaterCount getWaterCountById(Context context, String recordId) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		WaterCount info = new WaterCount();
		try {
			cursor = cr.query(Constants.WaterCountUri, null, "_id=?",
					new String[] { recordId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.waterDate = cursor.getString(cursor
							.getColumnIndex("waterDate"));
					info.yearMonth = cursor.getString(cursor
							.getColumnIndex("yearMoth"));

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
	 * 新增排泄记录
	 * 
	 * @param context
	 * @param info
	 *            排泄记录
	 * @return true 新增成功 false 新增失败
	 */
	public boolean insert(Context context, WaterCount info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("syncId", info.syncId);
			values.put("waterDate", info.waterDate);
			values.put("yearMoth", info.yearMonth);
			values.put("userId", info.userId);
			Uri uri = cr.insert(Constants.WaterCountUri, values);
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
	public boolean bulkInsert(Context context, ArrayList<WaterCount> arrayList) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[arrayList.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], arrayList.get(i));
			}
			int count = cr.bulkInsert(Constants.WaterCountUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, WaterCount info) {
		values.put("syncId", info.syncId);
		values.put("waterDate", info.waterDate);
		values.put("yearMoth", info.yearMonth);
		values.put("userId", info.userId);
	}

	public int updateWater(Context context, WaterCount water) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("syncId", water.syncId);
			result = cr.update(Constants.WaterCountUri, values, "_id=?",
					new String[] { String.valueOf(water.id) });

		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}

	public int delWater(Context context, WaterCount water) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			if (water.syncId == 1) {
				ContentValues values = new ContentValues();
				values.put("syncId", 3);
				result = cr.update(Constants.WaterCountUri, values, "_id=?",
						new String[] { String.valueOf(water.id) });

			} else {
				result = cr.delete(Constants.WaterCountUri, "_id=?",
						new String[] { String.valueOf(water.id) });
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}

}
