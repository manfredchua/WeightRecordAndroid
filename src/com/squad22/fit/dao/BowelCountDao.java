package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.BowelCount;
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

public class BowelCountDao {
	private static final String TAG = "BowelCountDao";

	private static BowelCountDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private BowelCountDao() {

	}

	public static synchronized BowelCountDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new BowelCountDao();
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
	public ArrayList<BowelCount> getBowelCountByDate(Context context,
			String date, String userId) {
		ArrayList<BowelCount> arrayList = new ArrayList<BowelCount>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.BowelCountUri, null,
					"bowelDate like ? and userId=?", new String[] {
							(date + "%"), userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					BowelCount info = new BowelCount();
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.bowelDate = cursor.getString(cursor
							.getColumnIndex("bowelDate"));
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

	public ArrayList<BowelCount> getBowelCount(Context context, String date,
			String userId) {
		ArrayList<BowelCount> arrayList = new ArrayList<BowelCount>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.BowelCountUri, null,
					"bowelDate like ? and syncId=0 and userId=?", new String[] {
							(date + "%"), userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					BowelCount info = new BowelCount();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.bowelDate = cursor.getString(cursor
							.getColumnIndex("bowelDate"));
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

	public ArrayList<BowelCount> getBowelCountBySyncId(Context context,
			String syncId, String userId) {
		ArrayList<BowelCount> arrayList = new ArrayList<BowelCount>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.BowelCountUri, null,
					"syncId = ? and userId=?", new String[] { syncId, userId },
					null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					BowelCount info = new BowelCount();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.bowelDate = cursor.getString(cursor
							.getColumnIndex("bowelDate"));
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

	public ArrayList<BowelCount> getSyncDelBowelCount(Context context,
			String userId) {
		ArrayList<BowelCount> arrayList = new ArrayList<BowelCount>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.BowelCountUri, null,
					"syncId=1 or syncId=2 and userId=?",
					new String[] { userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					BowelCount info = new BowelCount();
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.bowelDate = cursor.getString(cursor
							.getColumnIndex("bowelDate"));
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
		qb.setTables(WeightRecordBaseHelper.MY_BOWEL_COUNT_TABLE_NAME);

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

	public String getBowelCountId(Context context, String bowelDate,
			String userId) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		String recordId = null;
		String[] projection = { "_id" };
		try {
			cursor = cr.query(Constants.BowelCountUri, projection,
					"bowelDate = ? and userId=?", new String[] { bowelDate,
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

	public BowelCount getBowelCountById(Context context, String id) {
		BowelCount info = new BowelCount();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.BowelCountUri, null, "_id = ?",
					new String[] { id }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.bowelDate = cursor.getString(cursor
							.getColumnIndex("bowelDate"));
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
	public boolean insert(Context context, BowelCount info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("syncId", info.syncId);
			values.put("bowelDate", info.bowelDate);
			values.put("yearMoth", info.yearMonth);
			values.put("userId", info.userId);
			Uri uri = cr.insert(Constants.BowelCountUri, values);
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
	public boolean bulkInsert(Context context, ArrayList<BowelCount> arrayList) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[arrayList.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], arrayList.get(i));
			}
			int count = cr.bulkInsert(Constants.BowelCountUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, BowelCount info) {
		values.put("syncId", info.syncId);
		values.put("bowelDate", info.bowelDate);
		values.put("yearMoth", info.yearMonth);
		values.put("userId", info.userId);
	}

	public int updateBowel(Context context, BowelCount bowel) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("syncId", bowel.syncId);
			result = cr.update(Constants.BowelCountUri, values, "_id=?",
					new String[] { bowel.id });
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}

	public int delBowel(Context context, BowelCount bowel) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			if (bowel.syncId == 1) {
				ContentValues values = new ContentValues();
				values.put("syncId", 3);
				result = cr.update(Constants.BowelCountUri, values, "_id=?",
						new String[] { bowel.id });
			} else {
				result = cr.delete(Constants.BowelCountUri, "_id=?",
						new String[] { bowel.id });
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}
}
