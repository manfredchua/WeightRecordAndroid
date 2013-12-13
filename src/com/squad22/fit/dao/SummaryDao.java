package com.squad22.fit.dao;

import java.util.ArrayList;

import com.squad22.fit.entity.Summary;
import com.squad22.fit.utils.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SummaryDao {

	private static final String TAG = "SummaryDao";

	private static SummaryDao mSingleton;

	/**
	 * 外部不能实例化对象
	 */
	private SummaryDao() {

	}

	public static synchronized SummaryDao getInstance() {
		if (mSingleton == null) {
			mSingleton = new SummaryDao();
		}
		return mSingleton;
	}

	public String getQuote(Context context, String summaryId) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		String quote = "";
		try {
			cursor = cr.query(Constants.SummaryUri, null, "summaryId=?", new String[] { summaryId }, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					quote = cursor.getString(cursor.getColumnIndex("quote"));
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e + "------" + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return quote;
	}

	public boolean insert(Context context, Summary info) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("summaryId", info.summaryId);
			values.put("quote", info.quote);
			Uri uri = cr.insert(Constants.SummaryUri, values);
			if (uri != null) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}


	public boolean bulkInsert(Context context, ArrayList<Summary> arrayList) {
		boolean flags = false;
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues[] cvs = new ContentValues[arrayList.size()];
			for (int i = 0; i < cvs.length; i++) {
				cvs[i] = new ContentValues();
				setContentValues(cvs[i], arrayList.get(i));
			}
			int count = cr.bulkInsert(Constants.SummaryUri, cvs);
			if (count > 0) {
				flags = true;
			}
		} catch (Exception e) {
			Log.e(TAG, e + "---------" + e.getMessage());
		}
		return flags;
	}

	private void setContentValues(ContentValues values, Summary info) {
		values.put("summaryId", info.summaryId);
		values.put("quote", info.quote);
		
	}

}
