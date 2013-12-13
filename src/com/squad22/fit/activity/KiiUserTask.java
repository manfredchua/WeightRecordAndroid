package com.squad22.fit.activity;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiACL;
import com.kii.cloud.storage.KiiACL.FileAction;
import com.kii.cloud.storage.KiiACLEntry;
import com.kii.cloud.storage.KiiAnonymousUser;
import com.kii.cloud.storage.KiiFile;
import com.kii.cloud.storage.KiiFileBucket;
import com.kii.cloud.storage.KiiUser;
import com.squad22.fit.dao.ProfileDao;
import com.squad22.fit.entity.Profile;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

public class KiiUserTask extends AsyncTask<Void, Void, Void> {

	private static final String TAG = "KiiUserTask";
	Uri uri;
	Profile info;
	Context mContext;
	ProgressDialog proDialog;

	public KiiUserTask(Context mContext, Profile info,
			Uri uri, ProgressDialog proDialog) {
		this.mContext = mContext;
		this.uri = uri;
		this.info = info;
		this.proDialog = proDialog;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			KiiUser current = KiiUser.createByUri(uri);
			current.set(Constants.FullName, info.name);
			current.set(Constants.Gender, info.sex);
			current.set(Constants.Birthday, info.birthday);
			current.set(Constants.Height, info.height);
			current.set(Constants.IsMember, "");
			
			current.set(Constants.ExpiryDateMember, "");
			if(info.freeDateMember != null && !info.freeDateMember.equals("")){
				current.set(Constants.FreeDateMember, info.freeDateMember);
			}else{
				current.set(Constants.FreeDateMember, "");
			}
			current.set(Constants.CurrentWeight, info.weight);
			current.set(Constants.UNIT, info.unit);
			current.set(Constants.BACKUPSTATE, info.backupState);

			String target = CommUtils.getTargetWeight(info.height, info.sex);
			current.set(Constants.IdealWeight, target);

			int age = CommUtils.getAge(info.birthday);
			String bodyFat = CommUtils.getBodyFat(info.sex, age);
			current.set(Constants.IdealBodyFat, bodyFat);
			current.set(Constants.IdealBodyAge, String.valueOf(age));

			String LeanMusde = CommUtils.getLeanMusde(info.sex, age);
			current.set(Constants.IdealLeanMuscle, LeanMusde);

			String visceralFat = CommUtils.getVisceralFat(age);
			current.set(Constants.IdealVisceralFat, visceralFat);

			String bmi = "";
			String bmr = "";
			if (info.unit != null && info.unit.equals(Constants.IMPERIAL)) {
				info.height = info.height * 12;
				bmi = CommUtils.getLBBMI(info.weight, info.height);
				bmr = CommUtils.getLBBMR(info.sex, info.weight, info.height,
						age);
			} else {
				bmi = CommUtils.getKGBMI(info.weight, info.height);
				bmr = CommUtils.getKGBMR(info.sex, info.weight, info.height,
						age);
			}
			current.set(Constants.IdealBMI, bmi);
			current.set(Constants.IdealBMR, bmr);

			double kcal = Integer.valueOf(bmr) * 1.2;
			current.set(Constants.IdealCalorieNeeds, kcal);

			if (info.image != null && info.image.length() > 0) {
				
				File localFile = new File(info.image);
				
				Uri uri = null;
				try {
					uri = current.getUri(Constants.Avatar);
				} catch (Exception e) {
				}
				if(uri != null){
					if (localFile != null) {
	
						KiiFile kiiFile = KiiFile.createByUri(uri);
						kiiFile.save(localFile);
	
						String url = kiiFile.publish();
	
						// 设置用户可读取图片
						KiiACL acl = kiiFile.acl();
						acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser.create(),
								FileAction.READ_EXISTING_OBJECT, true));
						acl.save();
						current.set(Constants.AvatarUrl, url);
						current.set(Constants.Avatar, kiiFile.toUri());
					}
				}else{
					if (localFile != null) {
						KiiFileBucket fileBucket = Kii
								.fileBucket(Constants.KIIUSER);
	
						KiiFile kiiFile = fileBucket.file(localFile);
						kiiFile.save();
	
						String url = kiiFile.publish();
	
						// 设置用户可读取图片
						KiiACL acl = kiiFile.acl();
						acl.putACLEntry(new KiiACLEntry(KiiAnonymousUser.create(),
								FileAction.READ_EXISTING_OBJECT, true));
						acl.save();
						current.set(Constants.AvatarUrl, url);
						current.set(Constants.Avatar, kiiFile.toUri());
					}
				}
			} else {
				current.set(Constants.Avatar, "");
				current.set(Constants.AvatarUrl, "");
			}
			current.update();
			
		} catch (Exception e) {
			Log.e(TAG, "--" + e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("static-access")
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		if (proDialog != null && proDialog.isShowing()) {
			proDialog.dismiss();
			
			Intent intent = new Intent(mContext, MainActivity.class);
			mContext.startActivity(intent);
			((Activity) mContext).finish();
		}else{
			
			ProfileDao.getInstance().updateProfile(mContext, info);
			SharedPreferences sp = mContext.getSharedPreferences(Constants.IsMember, mContext.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean(Constants.FreeDateMember, true);
			editor.commit();
		}
		
	}
}
