package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.Food;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class FoodDao {
	private static final String TAG = "FoodDao";

	private static FoodDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private FoodDao() {

	}

	public static synchronized FoodDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new FoodDao();
		}
		return mSingleton;
	}
	

	/**
	 * 查询食物的详情
	 * 
	 * @param context
	 * @param name 食物名称
	 * @return 食物的详情集合
	 */
	public ArrayList<Food> getFoodByName(Context context, String name) {
		 ArrayList<Food> arrayList = new ArrayList<Food>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.FoodUri, null, "name like ?", new String[] { "%"+name+"%" }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Food info = new Food();
					info.name = cursor.getString(cursor
							.getColumnIndex("name"));
					info.foodId = cursor.getString(cursor
							.getColumnIndex("foodId"));
					info.kcal = cursor.getString(cursor
							.getColumnIndex("kcal"));
					info.qty = cursor.getString(cursor
							.getColumnIndex("qty"));
					info.category = cursor.getString(cursor
							.getColumnIndex("category"));
					info.unit = cursor.getString(cursor
							.getColumnIndex("unit"));
					info.syncId = cursor.getInt(cursor
							.getColumnIndex("syncId"));
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
	
	
	
	public ArrayList<Food> getFoodByNumber(Context context, String name) {
		 ArrayList<Food> arrayList = new ArrayList<Food>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.FoodUri, null, "qty like ? or unit like ?", new String[] { "%"+name+"%", "%"+name+"%" }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Food info = new Food();
					info.name = cursor.getString(cursor
							.getColumnIndex("name"));
					info.foodId = cursor.getString(cursor
							.getColumnIndex("foodId"));
					info.kcal = cursor.getString(cursor
							.getColumnIndex("kcal"));
					info.qty = cursor.getString(cursor
							.getColumnIndex("qty"));
					info.category = cursor.getString(cursor
							.getColumnIndex("category"));
					info.unit = cursor.getString(cursor
							.getColumnIndex("unit"));
					info.syncId = cursor.getInt(cursor
							.getColumnIndex("syncId"));
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
	
	public Food getFoodById(Context context, String id) {
		Food info = new Food();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.FoodUri, null, "foodId=?", new String[] { id }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					
					info.name = cursor.getString(cursor
							.getColumnIndex("name"));
					info.foodId = cursor.getString(cursor
							.getColumnIndex("foodId"));
					info.kcal = cursor.getString(cursor
							.getColumnIndex("kcal"));
					info.qty = cursor.getString(cursor
							.getColumnIndex("qty"));
					info.category = cursor.getString(cursor
							.getColumnIndex("category"));
					info.unit = cursor.getString(cursor
							.getColumnIndex("unit"));
					info.syncId = cursor.getInt(cursor
							.getColumnIndex("syncId"));
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
	 * 查询所有食物的详情
	 * 
	 * @param context
	 * @return 食物的详情集合
	 */
	public ArrayList<Food> getAllFood(Context context) {
		 ArrayList<Food> arrayList = new ArrayList<Food>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.FoodUri, null, null, null, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Food info = new Food();
					info.name = cursor.getString(cursor
							.getColumnIndex("name"));
					info.foodId = cursor.getString(cursor
							.getColumnIndex("foodId"));
					info.kcal = cursor.getString(cursor
							.getColumnIndex("kcal"));
					info.qty = cursor.getString(cursor
							.getColumnIndex("qty"));
					info.category = cursor.getString(cursor
							.getColumnIndex("category"));
					info.unit = cursor.getString(cursor
							.getColumnIndex("unit"));
					info.syncId = cursor.getInt(cursor
							.getColumnIndex("syncId"));
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
	
	
	public boolean getFood(Context context,String name) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		boolean result = true;
		try {
			cursor = cr.query(Constants.FoodUri, null, "name=?", new String[]{name}, null);
			if (null != cursor && cursor.getCount() > 0) {
				result = false;
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return result;
	}
	
	/**
	 * 新增排泄记录
	 * 
	 * @param context
	 * @param info 排泄记录
	 * @return true 新增成功 false 新增失败
	 */
	public boolean insert(Context context, Food info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("foodId", info.foodId);
			values.put("name", info.name);
			values.put("category", info.category);
			values.put("qty", info.qty);
			values.put("kcal", info.kcal);
			values.put("unit", info.unit);
			values.put("syncId", info.syncId);
			Uri uri = cr.insert(Constants.FoodUri, values);
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
	 * @param arrayList 运动记录集合
	 * @return true 新增成功 false 新增失败
	 */
	public boolean bulkInsert(Context context, ArrayList<Food> arrayList) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[arrayList.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], arrayList.get(i));
			}
			int count = cr.bulkInsert(Constants.FoodUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, Food info) {
		values.put("foodId", info.foodId);
		values.put("name", info.name);
		values.put("category", info.category);
		values.put("qty", info.qty);
		values.put("kcal", info.kcal);
		values.put("unit", info.unit);
		values.put("syncId", info.syncId);
	}
	
	
	
}
