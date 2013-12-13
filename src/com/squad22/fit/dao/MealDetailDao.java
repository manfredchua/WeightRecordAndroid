package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.MealDetail;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MealDetailDao {

	
	private static final String TAG = "MealDetailDao";

	private static MealDetailDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private MealDetailDao() {

	}

	public static synchronized MealDetailDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new MealDetailDao();
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
	public ArrayList<MealDetail> getMealDetailById(Context context, String recordId) {
		
		 ArrayList<MealDetail> arrayList = new ArrayList<MealDetail>();
	
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MealDetailUri, null, "recordId = ?", new String[] { recordId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					MealDetail info = new MealDetail();
					
					info.id = cursor.getString(cursor
							.getColumnIndex("_id"));
					info.recordId = cursor.getString(cursor
							.getColumnIndex("recordId"));
					info.name = cursor.getString(cursor
							.getColumnIndex("name"));
					info.Category = cursor.getString(cursor
							.getColumnIndex("Category"));
					info.Amount = cursor.getString(cursor
							.getColumnIndex("Amount"));
					info.calorie = cursor.getString(cursor
							.getColumnIndex("calorie"));
					info.portion = cursor.getString(cursor
							.getColumnIndex("portion"));
					info.unit = cursor.getString(cursor
							.getColumnIndex("unit"));
					info.foodId = cursor.getString(cursor
							.getColumnIndex("foodId"));
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
	
	public ArrayList<String> getFoodId(Context context) {
		
		 ArrayList<String> arrayList = new ArrayList<String>();
	
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MealDetailUri, null, null, null, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					String foodId = cursor.getString(cursor
							.getColumnIndex("foodId"));
					arrayList.add(foodId);
					
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
	public boolean insert(Context context, MealDetail info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("name", info.name);
			values.put("recordId", info.recordId);
			values.put("portion", info.portion);
			values.put("calorie", info.calorie);
			values.put("Amount", info.Amount);
			values.put("Category", info.Category);
			values.put("unit", info.unit);
			values.put("foodId", info.foodId);
			Uri uri = cr.insert(Constants.MealDetailUri, values);
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
	public boolean bulkInsert(Context context, ArrayList<MealDetail> detail) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[detail.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], detail.get(i));
			}
			int count = cr.bulkInsert(Constants.MealDetailUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, MealDetail info) {
		values.put("name", info.name);
		values.put("recordId", info.recordId);
		values.put("portion", info.portion);
		values.put("calorie", info.calorie);
		values.put("Amount", info.Amount);
		values.put("Category", info.Category);
		values.put("unit", info.unit);
		values.put("foodId", info.foodId);
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
		   
				result = cr.delete(Constants.MealDetailUri, "_id=?", new String[]{id});
		    } catch (Exception e) {
		    	Log.i(TAG, e + "------" + e.getMessage());
		    }
		    return result;
	}
	  
	  public int delMealDetail(Context context, String recordId) {
		    int result = 0;
		    try {
		      ContentResolver cr = context.getContentResolver();
		   
				result = cr.delete(Constants.MealDetailUri, "recordId=?", new String[]{recordId});
		    } catch (Exception e) {
		    	Log.i(TAG, e + "------" + e.getMessage());
		    }
		    return result;
	}
}
