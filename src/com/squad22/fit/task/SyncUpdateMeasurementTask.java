package com.squad22.fit.task;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.query.KiiClause;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.entity.Measurement;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.FileCache;

public class SyncUpdateMeasurementTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "SyncUpdateMeasurementTask";
	Context mContext;
	Measurement measurement;

	public SyncUpdateMeasurementTask(Context mContext, Measurement measurement) {
		this.mContext = mContext;
		this.measurement = measurement;
	}

	@Override
	protected Void doInBackground(Void... params) {
		KiiUser currentUser = KiiUser.getCurrentUser();
		try {
			KiiQuery query = new KiiQuery(KiiClause.equals(
					Constants.measurementID, measurement.measurementId));
			KiiQueryResult<KiiObject> result = currentUser.bucket(
					Constants.MyMeasurement).query(query);
			List<KiiObject> listObj = result.getResult();
			for (KiiObject kiiObject : listObj) {
				setMeasurementObject(kiiObject, measurement);
				kiiObject.save();

				measurement.syncId = 1;
				MeasurementDao.getInstance().updateMeasurement(mContext,
						measurement);
			}
		} catch (Exception e) {
			Log.e(TAG, "--" + e.getMessage());
		}
		return null;
	}

	private void setMeasurementObject(KiiObject object, Measurement measurement) {
		try {
			object.set(Constants.measurementID, measurement.measurementId);
			object.set(Constants.measurementWeight, measurement.weight);
			object.set(Constants.measurementReportDate, measurement.createDate);
			object.set(Constants.measurementBodyFat, measurement.bodyFat);
			object.set(Constants.measurementLeanMuscle, measurement.leanMusde);
			object.set(Constants.measurementVisceralFat,
					measurement.visceralFat);
			object.set(Constants.measurementBodyAge, measurement.bodyAge);
			object.set(Constants.measurementBMI, measurement.BMI);
			object.set(Constants.measurementBMR, measurement.BMR);
			object.set(Constants.measurementArm, measurement.arm);
			object.set(Constants.measurementWaist, measurement.waist);
			object.set(Constants.measurementAbd, measurement.abd);
			object.set(Constants.measurementHip, measurement.hip);
			object.set(Constants.measurementThigh, measurement.thigh);
			object.set(Constants.measurementCalf, measurement.calf);
			object.set(Constants.measurementRemark, measurement.remark);
			object.set(Constants.measurementSyncID, 1);

			FileCache.CreateText();

			if (measurement.image1 != null && measurement.image1.length() > 0) {
				try {
					object.set(Constants.measurementImage1,
							object.getString(Constants.measurementImage1));
				} catch (Exception e) {
				}

				try {
					Uri uri = object.getUri(Constants.imageUri1);
					object.set(Constants.imageUri1, uri);
				} catch (Exception e) {
				}
			} else {
				object.set(Constants.measurementImage1, "");
				object.set(Constants.imageUri1, "");
			}

			if (measurement.image2 != null && measurement.image2.length() > 0) {

				try {
					object.set(Constants.measurementImage2,
							object.getString(Constants.measurementImage2));
				} catch (Exception e) {
				}

				try {
					Uri uri = object.getUri(Constants.imageUri2);
					object.set(Constants.imageUri2, uri);
				} catch (Exception e) {
				}
			} else {
				object.set(Constants.measurementImage2, "");
				object.set(Constants.imageUri2, "");
			}

			if (measurement.image3 != null && measurement.image3.length() > 0) {

				try {
					object.set(Constants.measurementImage3,
							object.getString(Constants.measurementImage3));
				} catch (Exception e) {
				}

				try {
					Uri uri = object.getUri(Constants.imageUri3);
					object.set(Constants.imageUri3, uri);
				} catch (Exception e) {
				}
			} else {
				object.set(Constants.mealImageUri, "");
				object.set(Constants.measurementImage3, "");
			}

		} catch (Exception e) {
			Log.i(TAG, "--" + e.getMessage());

		}
	}
}
