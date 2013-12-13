package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.Units;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class UnitsDao {

	private static final String TAG = "UnitsDao";

	private static UnitsDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private UnitsDao() {

	}

	public static synchronized UnitsDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new UnitsDao();
		}
		return mSingleton;
	}

	public ArrayList<Units> getUnit(Context context, String unit) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		ArrayList<Units> units = new ArrayList<Units>();
		try {
			cursor = cr.query(Constants.UnitsUri, null, "unit like ?", new String[] { "%"+unit.substring(unit.length())+"%" }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Units unit1 = new Units();
					unit1.unit = cursor.getString(cursor.getColumnIndex("unit"));
					unit1.unitId = cursor.getString(cursor.getColumnIndex("unitId"));
					units.add(unit1);
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return units;
	}
	
	public ArrayList<Units> getUnit(Context context) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		ArrayList<Units> units = new ArrayList<Units>();
		try {
			cursor = cr.query(Constants.UnitsUri, null, null, null, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					Units unit = new Units();
					unit.unit = cursor.getString(cursor.getColumnIndex("unit"));
					unit.unitId = cursor.getString(cursor.getColumnIndex("unitId"));
					units.add(unit);
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return units;
	}

	public boolean insert(Context context, Units info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("unitId", info.unitId);
			values.put("unit", info.unit);
			Uri uri = cr.insert(Constants.UnitsUri, values);
			if (uri != null) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}


	public boolean bulkInsert(Context context, ArrayList<Units> arrayList) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[arrayList.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], arrayList.get(i));
			}
			int count = cr.bulkInsert(Constants.UnitsUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, Units info) {
		values.put("unitId", info.unitId);
		values.put("unit", info.unit);
		
	}

}
