package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.Meal;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MyMealDao {

	private static final String TAG = "MyMealDao";

	private static MyMealDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private MyMealDao() {

	}

	public static synchronized MyMealDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new MyMealDao();
		}
		return mSingleton;
	}

	/**
	 * 查询所以食物记录
	 * 
	 * @param context
	 * @param date
	 *            时间
	 * @return 食物记录集合
	 */
	public ArrayList<Meal> getAllMealByDate(Context context, String date,String userId) {
		ArrayList<Meal> arrayList = new ArrayList<Meal>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MealUri, null,
					"createDate like ? and syncId != 3 and userId=?", new String[] { date
							+ "%", userId }, "createDate desc");
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Meal info = new Meal();
					info.mealId = cursor.getString(cursor.getColumnIndex("mealId"));
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.remark = cursor.getString(cursor.getColumnIndex("remark"));
					info.comment = cursor.getString(cursor
							.getColumnIndex("comment"));
					info.like = cursor.getInt(cursor.getColumnIndex("like"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.title = cursor.getString(cursor
							.getColumnIndex("title"));

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
	
	
	public ArrayList<Meal> getAllMeal(Context context) {
		ArrayList<Meal> arrayList = new ArrayList<Meal>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MealUri, null,
					"syncId != 3", null, "createDate desc limit 10");
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Meal info = new Meal();
					info.mealId = cursor.getString(cursor.getColumnIndex("mealId"));
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.remark = cursor.getString(cursor.getColumnIndex("remark"));
					info.comment = cursor.getString(cursor
							.getColumnIndex("comment"));
					info.like = cursor.getInt(cursor.getColumnIndex("like"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.title = cursor.getString(cursor
							.getColumnIndex("title"));

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
	
	
	
	public ArrayList<Meal> getAllMeal(Context context,String userId, String syncId) {
		ArrayList<Meal> arrayList = new ArrayList<Meal>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MealUri, null,
					"userId=? and syncId =? ", new String[]{userId,syncId}, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Meal info = new Meal();
					info.mealId = cursor.getString(cursor.getColumnIndex("mealId"));
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.remark = cursor.getString(cursor.getColumnIndex("remark"));
					info.comment = cursor.getString(cursor
							.getColumnIndex("comment"));
					info.like = cursor.getInt(cursor.getColumnIndex("like"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.title = cursor.getString(cursor
							.getColumnIndex("title"));

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
	
	
	public ArrayList<Meal> getSyncDelMeal(Context context,String userId) {
		ArrayList<Meal> arrayList = new ArrayList<Meal>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MealUri, null,
					"userId=? and syncId =1 or syncId=2 ", new String[]{userId}, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Meal info = new Meal();
					info.mealId = cursor.getString(cursor.getColumnIndex("mealId"));
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.remark = cursor.getString(cursor.getColumnIndex("remark"));
					info.comment = cursor.getString(cursor
							.getColumnIndex("comment"));
					info.like = cursor.getInt(cursor.getColumnIndex("like"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.title = cursor.getString(cursor
							.getColumnIndex("title"));

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
	 * 查询所以食物记录的详情
	 * 
	 * @param context
	 * @param date
	 *            时间
	 * @return 食物记录的详情集合
	 */
	public Meal getMealById(Context context, String id) {
		Meal info = new Meal();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MealUri, null, "_id = ?",
					new String[] { id }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {

					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.mealId = cursor.getString(cursor.getColumnIndex("mealId"));
					info.userId = cursor.getString(cursor.getColumnIndex("userId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.remark = cursor.getString(cursor.getColumnIndex("remark"));
					info.comment = cursor.getString(cursor
							.getColumnIndex("comment"));
					info.like = cursor.getInt(cursor.getColumnIndex("like"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.title = cursor.getString(cursor
							.getColumnIndex("title"));

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
	 * 查询所以食物记录的详情
	 * 
	 * @param context
	 * @param date
	 *            时间
	 * @return 食物记录的详情集合
	 */
	public String getMealId(Context context, String date,String userId) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		String[] projection = { "_id" };
		String recordId = null;
		try {
			cursor = cr.query(Constants.MealUri, projection,
					"mealId = ? and userId=?",
					new String[] { date,userId }, null);
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
	 * 新增运动记录
	 * 
	 * @param context
	 * @param Activity
	 *            运动记录
	 * @return true 新增成功 false 新增失败
	 */
	public boolean insert(Context context, Meal info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("createDate", info.createDate);
			values.put("remark", info.remark);
			values.put("comment", info.comment);
			values.put("image", info.image);
			values.put("like", info.like);
			values.put("syncId", info.syncId);
			values.put("title", info.title);
			values.put("mealId", info.mealId);
			values.put("userId", info.userId);
			Uri uri = cr.insert(Constants.MealUri, values);
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
	public boolean bulkInsert(Context context, ArrayList<Meal> arrayList) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[arrayList.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], arrayList.get(i));
			}
			int count = cr.bulkInsert(Constants.MealUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, Meal info) {
		values.put("createDate", info.createDate);
		values.put("remark", info.remark);
		values.put("comment", info.comment);
		values.put("image", info.image);
		values.put("like", info.like);
		values.put("syncId", info.syncId);
		values.put("userId", info.userId);
		values.put("mealId", info.mealId);
		
	}

	/**
	 * 修改运动记录
	 * 
	 * @param context
	 * @param info
	 *            运动记录信息
	 * @return
	 */
	public int updateMeal(Context context, Meal info) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("createDate", info.createDate);
			values.put("remark", info.remark);
			values.put("comment", info.comment);
			values.put("image", info.image);
			values.put("like", info.like);
			values.put("syncId", info.syncId);
			values.put("title", info.title);
			values.put("mealId", info.mealId);
			result = cr.update(Constants.MealUri, values, "mealId=?",
					new String[] { String.valueOf(info.mealId) });
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
	public int delMeal(Context context, Meal meal) {
		int result = 0;
		try {
			
			ContentResolver cr = context.getContentResolver();
			
			if(meal.syncId == 1 || meal.syncId == 2){
				ContentValues values = new ContentValues();
				values.put("syncId", 3);
				result = cr.update(Constants.MealUri, values, "_id=?",
						new String[] { String.valueOf(meal.id) });
			}else{
				result = cr.delete(Constants.MealUri, "_id=?",
						new String[] { String.valueOf(meal.id) });
				MealDetailDao.getInstance().delMealDetail(context, meal.id);
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}
}
