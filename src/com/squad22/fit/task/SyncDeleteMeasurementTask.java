package com.squad22.fit.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.kii.cloud.storage.KiiFile;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.query.KiiClause;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.entity.Measurement;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.utils.Constants;

@SuppressLint("SimpleDateFormat")
public class SyncDeleteMeasurementTask extends AsyncTask<Void, Void, Void>{
	Context mContext;
	Measurement measurement;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	
	public SyncDeleteMeasurementTask(Context mContext, Measurement measurement){
		this.mContext = mContext;
		this.measurement = measurement;
	}

	@Override
	protected Void doInBackground(Void... params) {
		KiiUser currentUser = KiiUser.getCurrentUser();
		try{
		KiiQuery query = new KiiQuery(KiiClause.equals(
				Constants.measurementID, measurement.measurementId));
		KiiQueryResult<KiiObject> result = currentUser.bucket(
				Constants.MyMeasurement).query(query);
		List<KiiObject> listObj = result.getResult();
		for (KiiObject kiiObject : listObj) {

			try {
				Uri uri = kiiObject.getUri(Constants.imageUri1);
				if (uri != null) {
					KiiFile file = KiiFile.createByUri(uri);
					file.delete();
				}
			} catch (Exception e) {
			}

			try {
				Uri uri = kiiObject.getUri(Constants.imageUri2);
				if (uri != null) {
					KiiFile file = KiiFile.createByUri(uri);
					file.delete();
				}
			} catch (Exception e) {
			}

			try {
				Uri uri = kiiObject.getUri(Constants.imageUri3);
				if (uri != null) {
					KiiFile file = KiiFile.createByUri(uri);
					file.delete();
				}
			} catch (Exception e) {
			}

			kiiObject.delete();
			int count = MeasurementDao.getInstance()
					.delMeasurement(mContext,
							measurement);
			if (count > 0) {
				MyRecordsDao.getInstance().delRecords(mContext,
						measurement.measurementId, "3");
				Date date = dateFormat
						.parse(measurement.createDate);
				String currentDate = dateSF.format(date);
				ArrayList<MyRecords> arrayList = MyRecordsDao
						.getInstance().getAllRecordByDate(mContext, currentDate,
								measurement.userId);
				if (arrayList != null && arrayList.size() == 0) {
					MyRecordsDao.getInstance().delSummaryRecords(mContext);
				}

			}
		}
		}catch(Exception e){
			Log.e("Exception", "--" + e.getMessage());
		}
		return null;
	}
	
}

