package com.squad22.fit.dao;

import com.squad22.fit.entity.Profile;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ProfileDao {

	private static final String TAG = "ProfileDao";

	private static ProfileDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private ProfileDao() {

	}

	public static synchronized ProfileDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new ProfileDao();
		}
		return mSingleton;
	}

	/**
	 * 获取用户信息
	 * 
	 * @param context
	 * @return
	 */
	public Profile getProfile(Context context, String userName) {
		Profile info = new Profile();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(Constants.ProfileUri, null, "userName=?",
					new String[] { userName }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					info.id = cursor.getString(cursor.getColumnIndex("_id"));
					info.name = cursor.getString(cursor.getColumnIndex("name"));
					info.userName = cursor.getString(cursor
							.getColumnIndex("userName"));
					info.birthday = cursor.getString(cursor
							.getColumnIndex("birthday"));
					info.height = cursor.getDouble(cursor
							.getColumnIndex("height"));
					info.sex = cursor.getString(cursor.getColumnIndex("sex"));
					info.image = cursor.getString(cursor.getColumnIndex("image"));
					info.weight = cursor.getDouble(cursor
							.getColumnIndex("weight"));
					info.unit = cursor.getString(cursor.getColumnIndex("unit"));

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
	 * 新增用户信息
	 * 
	 * @param context
	 * @param Profile
	 *            用户信息
	 * @return true 新增成功 false 新增失败
	 */
	public boolean insert(Context context, Profile info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("name", info.name);
			values.put("birthday", info.birthday);
			values.put("height", info.height);
			values.put("sex", info.sex);
			values.put("image", info.image);
			values.put("weight", info.weight);
			values.put("unit", info.unit);
			values.put("userName", info.userName);
			Uri uri = cr.insert(Constants.ProfileUri, values);
			if (uri != null) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	/**
	 * 修改用户信息
	 * 
	 * @param context
	 * @param info
	 *            用户信息
	 * @return
	 */
	public int updateProfile(Context context, Profile info) {
		int result = 0;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("name", info.name);
			values.put("birthday", info.birthday);
			values.put("height", info.height);
			values.put("sex", info.sex);
			values.put("image", info.image);
			values.put("weight", info.weight);
			values.put("unit", info.unit);
			values.put("userName", info.userName);
			result = cr.update(Constants.ProfileUri, values, "_id=?",
					new String[] { String.valueOf(info.id) });
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		}

		return result;
	}

}
