package com.squad22.fit.task;

import java.io.File;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiACL;
import com.kii.cloud.storage.KiiACL.FileAction;
import com.kii.cloud.storage.KiiACLEntry;
import com.kii.cloud.storage.KiiAnonymousUser;
import com.kii.cloud.storage.KiiFile;
import com.kii.cloud.storage.KiiFileBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.entity.Measurement;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.FileCache;

public class SyncMeasurementTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "SyncMeasurementTask";
	Context mContext;
	Measurement measurement;

	public SyncMeasurementTask(Context mContext, Measurement measurement) {
		this.measurement = measurement;
	}

	@Override
	protected Void doInBackground(Void... params) {
		KiiUser currentUser = KiiUser.getCurrentUser();
		try {
			KiiObject object = currentUser.bucket(Constants.MyMeasurement)
					.object();
			setMeasurementObject(object, measurement);
			object.save();

			measurement.syncId = 1;
			MeasurementDao.getInstance().updateMeasurement(
					mContext, measurement);
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

			File localFile = new File(measurement.image1);

			if (localFile != null) {
				KiiFileBucket fileBucket = Kii
						.fileBucket(Constants.MyMeasurement);

				KiiFile kiiFile = fileBucket.file(localFile);
				kiiFile.save();

				String url = kiiFile.publish();

				// 设置用户可读取图片
				KiiACL acl = kiiFile.acl();
				acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser.create(),
						FileAction.READ_EXISTING_OBJECT, true));
				acl.save();
				object.set(Constants.measurementImage1, url);
				object.set(Constants.imageUri1, kiiFile.toUri());
			}
		} else {
			object.set(Constants.measurementImage1, "");
			object.set(Constants.imageUri1, "");
		}

		if (measurement.image2 != null && measurement.image2.length() > 0) {

			File localFile = new File(measurement.image2);


			if (localFile != null) {
				KiiFileBucket fileBucket = Kii
						.fileBucket(Constants.MyMeasurement);

				KiiFile kiiFile = fileBucket.file(localFile);
				kiiFile.save();

				String url = kiiFile.publish();

				// 设置用户可读取图片
				KiiACL acl = kiiFile.acl();
				acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser.create(),
						FileAction.READ_EXISTING_OBJECT, true));
				acl.save();
				object.set(Constants.measurementImage2, url);
				object.set(Constants.imageUri2, kiiFile.toUri());
			}
		} else {
			object.set(Constants.measurementImage2, "");
			object.set(Constants.imageUri2, "");
		}

		if (measurement.image3 != null && measurement.image3.length() > 0) {

			File localFile = new File(measurement.image3);

			if (localFile != null) {
				KiiFileBucket fileBucket = Kii
						.fileBucket(Constants.MyMeasurement);

				KiiFile kiiFile = fileBucket.file(localFile);
				kiiFile.save();

				String url = kiiFile.publish();

				// 设置用户可读取图片
				KiiACL acl = kiiFile.acl();
				acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser.create(),
						FileAction.READ_EXISTING_OBJECT, true));
				acl.save();
				object.set(Constants.measurementImage3, url);
				object.set(Constants.imageUri3, kiiFile.toUri());
			}
		} else {
			object.set(Constants.mealImageUri, "");
			object.set(Constants.measurementImage3, "");
		}

	} catch (Exception e) {
		Log.i(TAG, "AccountActivity.setObject()" + e.getMessage());

	}
}
}

