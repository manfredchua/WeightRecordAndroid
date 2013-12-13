package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.Measurement;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MeasurementDao {

	private static final String TAG = "MeasurementDao";

	private static MeasurementDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private MeasurementDao() {

	}

	public static synchronized MeasurementDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new MeasurementDao();
		}
		return mSingleton;
	}

	/**
	 * 查询新测量记录
	 * 
	 * @param context
	 * @param date
	 *            时间
	 * @return 新测量记录集合
	 */
	public ArrayList<Measurement> getMeasurementByDate(Context context,
			String date,String userId) {

		ArrayList<Measurement> arrayList = new ArrayList<Measurement>();

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MeasurementUri, null,
					"createDate like ? and userId=?", new String[] { (date + "%"),userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Measurement info = new Measurement();

					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.measurementId = cursor.getString(cursor.getColumnIndex("measurementId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.weight = cursor.getDouble(cursor
							.getColumnIndex("weight"));
					info.bodyFat = cursor.getDouble(cursor
							.getColumnIndex("bodyFat"));
					info.bodyAge = cursor.getDouble(cursor
							.getColumnIndex("bodyAge"));
					info.BMI = cursor.getDouble(cursor
							.getColumnIndex("BMI"));
					info.BMR = cursor.getDouble(cursor
							.getColumnIndex("BMR"));
					info.abd = cursor.getDouble(cursor
							.getColumnIndex("abd"));
					info.arm = cursor.getDouble(cursor
							.getColumnIndex("arm"));
					info.calf = cursor.getDouble(cursor
							.getColumnIndex("calf"));
					info.hip = cursor.getDouble(cursor
							.getColumnIndex("hip"));
					info.image1 = cursor.getString(cursor
							.getColumnIndex("image1"));
					info.image2 = cursor.getString(cursor
							.getColumnIndex("image2"));
					info.image3 = cursor.getString(cursor
							.getColumnIndex("image3"));
					info.leanMusde = cursor.getDouble(cursor
							.getColumnIndex("leanMusde"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.thigh = cursor.getDouble(cursor
							.getColumnIndex("thigh"));
					info.visceralFat = cursor.getDouble(cursor
							.getColumnIndex("visceralFat"));
					info.waist = cursor.getDouble(cursor
							.getColumnIndex("waist"));
					info.remark = cursor.getString(cursor
							.getColumnIndex("remark"));
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
	
	public ArrayList<Measurement> getMeasurementBySyncId(Context context, String syncId,String userId) {

		ArrayList<Measurement> arrayList = new ArrayList<Measurement>();

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MeasurementUri, null,
					"syncId=? and userId=?", new String[] { syncId, userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Measurement info = new Measurement();

					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.measurementId = cursor.getString(cursor.getColumnIndex("measurementId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.weight = cursor.getDouble(cursor
							.getColumnIndex("weight"));
					info.bodyFat = cursor.getDouble(cursor
							.getColumnIndex("bodyFat"));
					info.bodyAge = cursor.getDouble(cursor
							.getColumnIndex("bodyAge"));
					info.BMI = cursor.getDouble(cursor
							.getColumnIndex("BMI"));
					info.BMR = cursor.getDouble(cursor
							.getColumnIndex("BMR"));
					info.abd = cursor.getDouble(cursor
							.getColumnIndex("abd"));
					info.arm = cursor.getDouble(cursor
							.getColumnIndex("arm"));
					info.calf = cursor.getDouble(cursor
							.getColumnIndex("calf"));
					info.hip = cursor.getDouble(cursor
							.getColumnIndex("hip"));
					info.image1 = cursor.getString(cursor
							.getColumnIndex("image1"));
					info.image2 = cursor.getString(cursor
							.getColumnIndex("image2"));
					info.image3 = cursor.getString(cursor
							.getColumnIndex("image3"));
					info.leanMusde = cursor.getDouble(cursor
							.getColumnIndex("leanMusde"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.thigh = cursor.getDouble(cursor
							.getColumnIndex("thigh"));
					info.visceralFat = cursor.getDouble(cursor
							.getColumnIndex("visceralFat"));
					info.waist = cursor.getDouble(cursor
							.getColumnIndex("waist"));
					info.remark = cursor.getString(cursor
							.getColumnIndex("remark"));
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
	
	public ArrayList<Measurement> getSyncDelMeasurement(Context context,String userId) {

		ArrayList<Measurement> arrayList = new ArrayList<Measurement>();

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MeasurementUri, null,
					"syncId=1 or syncId=2 and userId=?", new String[] { userId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Measurement info = new Measurement();

					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.measurementId = cursor.getString(cursor.getColumnIndex("measurementId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.weight = cursor.getDouble(cursor
							.getColumnIndex("weight"));
					info.bodyFat = cursor.getDouble(cursor
							.getColumnIndex("bodyFat"));
					info.bodyAge = cursor.getDouble(cursor
							.getColumnIndex("bodyAge"));
					info.BMI = cursor.getDouble(cursor
							.getColumnIndex("BMI"));
					info.BMR = cursor.getDouble(cursor
							.getColumnIndex("BMR"));
					info.abd = cursor.getDouble(cursor
							.getColumnIndex("abd"));
					info.arm = cursor.getDouble(cursor
							.getColumnIndex("arm"));
					info.calf = cursor.getDouble(cursor
							.getColumnIndex("calf"));
					info.hip = cursor.getDouble(cursor
							.getColumnIndex("hip"));
					info.image1 = cursor.getString(cursor
							.getColumnIndex("image1"));
					info.image2 = cursor.getString(cursor
							.getColumnIndex("image2"));
					info.image3 = cursor.getString(cursor
							.getColumnIndex("image3"));
					info.leanMusde = cursor.getDouble(cursor
							.getColumnIndex("leanMusde"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.thigh = cursor.getDouble(cursor
							.getColumnIndex("thigh"));
					info.visceralFat = cursor.getDouble(cursor
							.getColumnIndex("visceralFat"));
					info.waist = cursor.getDouble(cursor
							.getColumnIndex("waist"));
					info.remark = cursor.getString(cursor
							.getColumnIndex("remark"));
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

	public Measurement getMeasurementById(Context context, String recordId) {

		Measurement info = new Measurement();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MeasurementUri, null, "_id=?",
					new String[] { recordId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.measurementId = cursor.getString(cursor.getColumnIndex("measurementId"));
					info.userId = cursor.getString(cursor.getColumnIndex("userId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.weight = cursor.getDouble(cursor
							.getColumnIndex("weight"));
					info.bodyFat = cursor.getDouble(cursor
							.getColumnIndex("bodyFat"));
					info.bodyAge = cursor.getDouble(cursor
							.getColumnIndex("bodyAge"));
					info.BMI = cursor.getDouble(cursor
							.getColumnIndex("BMI"));
					info.BMR = cursor.getDouble(cursor
							.getColumnIndex("BMR"));
					info.abd = cursor.getDouble(cursor
							.getColumnIndex("abd"));
					info.arm = cursor.getDouble(cursor
							.getColumnIndex("arm"));
					info.calf = cursor.getDouble(cursor
							.getColumnIndex("calf"));
					info.hip = cursor.getDouble(cursor
							.getColumnIndex("hip"));
					info.image1 = cursor.getString(cursor
							.getColumnIndex("image1"));
					info.image2 = cursor.getString(cursor
							.getColumnIndex("image2"));
					info.image3 = cursor.getString(cursor
							.getColumnIndex("image3"));
					info.leanMusde = cursor.getDouble(cursor
							.getColumnIndex("leanMusde"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.thigh = cursor.getDouble(cursor
							.getColumnIndex("thigh"));
					info.visceralFat = cursor.getDouble(cursor
							.getColumnIndex("visceralFat"));
					info.waist = cursor.getDouble(cursor
							.getColumnIndex("waist"));
					info.remark = cursor.getString(cursor
							.getColumnIndex("remark"));

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

	public Measurement getMeasurement(Context context, String date, String userId) {

		Measurement info = new Measurement();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.MeasurementUri, null, "createDate < ? and userId=?",
					new String[]{date, userId}, "createDate desc limit 1");
			if (null != cursor) {
				while (cursor.moveToNext()) {
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.measurementId = cursor.getString(cursor.getColumnIndex("measurementId"));
					info.createDate = cursor.getString(cursor
							.getColumnIndex("createDate"));
					info.weight = cursor.getDouble(cursor
							.getColumnIndex("weight"));
					info.bodyFat = cursor.getDouble(cursor
							.getColumnIndex("bodyFat"));
					info.bodyAge = cursor.getDouble(cursor
							.getColumnIndex("bodyAge"));
					info.BMI = cursor.getDouble(cursor
							.getColumnIndex("BMI"));
					info.BMR = cursor.getDouble(cursor
							.getColumnIndex("BMR"));
					info.abd = cursor.getDouble(cursor
							.getColumnIndex("abd"));
					info.arm = cursor.getDouble(cursor
							.getColumnIndex("arm"));
					info.calf = cursor.getDouble(cursor
							.getColumnIndex("calf"));
					info.hip = cursor.getDouble(cursor
							.getColumnIndex("hip"));
					info.image1 = cursor.getString(cursor
							.getColumnIndex("image1"));
					info.image2 = cursor.getString(cursor
							.getColumnIndex("image2"));
					info.image3 = cursor.getString(cursor
							.getColumnIndex("image3"));
					info.leanMusde = cursor.getDouble(cursor
							.getColumnIndex("leanMusde"));
					info.syncId = cursor
							.getInt(cursor.getColumnIndex("syncId"));
					info.thigh = cursor.getDouble(cursor
							.getColumnIndex("thigh"));
					info.visceralFat = cursor.getDouble(cursor
							.getColumnIndex("visceralFat"));
					info.waist = cursor.getDouble(cursor
							.getColumnIndex("waist"));

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

	
	public String getMeasurementId(Context context, String measurementId, String userId) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		String recordId = null;
		String[] projection = { "_id" };
		try {
			cursor = cr.query(Constants.MeasurementUri, projection,
					"measurementId = ? and userId=?", new String[] { measurementId, userId }, null);
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
	
	public double getWeight(Context context, String userId) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		double weight = 0;
		String[] projection = { "weight" };
		try {
			cursor = cr.query(Constants.MeasurementUri, projection,
					"userId=?", new String[] { userId }, "createDate desc limit 1");
			if (null != cursor) {
				while (cursor.moveToNext()) {

					weight = cursor.getDouble(cursor.getColumnIndex("weight"));

				}
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return weight;
	}

	/**
	 * 新增新测量记录
	 * 
	 * @param context
	 * @param info
	 *            新测量记录
	 * @return true 新增成功 false 新增失败
	 */
	public boolean insert(Context context, Measurement info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("measurementId", info.measurementId);
			values.put("abd", info.abd);
			values.put("arm", info.arm);
			values.put("BMI", info.BMI);
			values.put("BMR", info.BMR);
			values.put("bodyAge", info.bodyAge);
			values.put("bodyFat", info.bodyFat);
			values.put("calf", info.calf);
			values.put("hip", info.hip);
			values.put("image1", info.image1);
			values.put("image2", info.image2);
			values.put("image3", info.image3);
			values.put("leanMusde", info.leanMusde);
			values.put("syncId", info.syncId);
			values.put("thigh", info.thigh);
			values.put("visceralFat", info.visceralFat);
			values.put("waist", info.waist);
			values.put("weight", info.weight);
			values.put("createDate", info.createDate);
			values.put("remark", info.remark);
			values.put("userId", info.userId);
			Uri uri = cr.insert(Constants.MeasurementUri, values);
			if (uri != null) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	/**
	 * 批量新增新测量记录
	 * 
	 * @param context
	 * @param detail
	 *            新测量记录集合
	 * @return true 新增成功 false 新增失败
	 */
	public boolean bulkInsert(Context context, ArrayList<Measurement> detail) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[detail.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], detail.get(i));
			}
			int count = cr.bulkInsert(Constants.MeasurementUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, Measurement info) {
		values.put("measurementId", info.measurementId);
		values.put("abd", info.abd);
		values.put("arm", info.arm);
		values.put("BMI", info.BMI);
		values.put("BMR", info.BMR);
		values.put("bodyAge", info.bodyAge);
		values.put("bodyFat", info.bodyFat);
		values.put("calf", info.calf);
		values.put("hip", info.hip);
		values.put("image1", info.image1);
		values.put("image2", info.image2);
		values.put("image3", info.image3);
		values.put("leanMusde", info.leanMusde);
		values.put("syncId", info.syncId);
		values.put("thigh", info.thigh);
		values.put("visceralFat", info.visceralFat);
		values.put("waist", info.waist);
		values.put("weight", info.weight);
		values.put("createDate", info.createDate);
		values.put("remark", info.remark);
		values.put("userId", info.userId);
	}
	
	public int updateMeasurement(Context context, Measurement info) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("abd", info.abd);
			values.put("arm", info.arm);
			values.put("BMI", info.BMI);
			values.put("BMR", info.BMR);
			values.put("bodyAge", info.bodyAge);
			values.put("bodyFat", info.bodyFat);
			values.put("calf", info.calf);
			values.put("hip", info.hip);
			values.put("leanMusde", info.leanMusde);
			values.put("syncId", info.syncId);
			values.put("thigh", info.thigh);
			values.put("visceralFat", info.visceralFat);
			values.put("waist", info.waist);
			values.put("weight", info.weight);
			values.put("remark", info.remark);
			values.put("image1", info.image1);
			values.put("image2", info.image2);
			values.put("image3", info.image3);
			
			result = cr.update(Constants.MeasurementUri,values, "_id=?",
					new String[] { info.id });
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}
	
	
	public int updateMeasurementSyncId(Context context, Measurement info) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("syncId", info.syncId);
			
			result = cr.update(Constants.MeasurementUri,values, "_id=?",
					new String[] { info.id });
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}

	/**
	 * 删除新测量记录
	 * 
	 * @param context
	 * @param Id
	 * @return
	 */
	public int delMeasurement(Context context, Measurement measurement) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			
			if(measurement.syncId == 1 || measurement.syncId == 2){
				ContentValues values = new ContentValues();
				values.put("syncId", 3);
				result = cr.update(Constants.MeasurementUri, values, "_id=?",
						new String[] { String.valueOf(measurement.id) });
			}else{
				result = cr.delete(Constants.MeasurementUri, "_id=?",
						new String[] { String.valueOf(measurement.id) });
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}
		return result;
	}
}
