package com.squad22.fit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.Kii.Site;
import com.kii.cloud.storage.KiiUser;
import com.squad22.fit.activity.LoginActivity;
import com.squad22.fit.activity.MainActivity;
import com.squad22.fit.activity.WeightRecordApplication;
import com.squad22.fit.dao.ProfileDao;
import com.squad22.fit.entity.Profile;
import com.squad22.fit.utils.Constants;

@SuppressLint("HandlerLeak")
public class WelcomeActivity extends Activity {

	static {
		Kii.initialize(Constants.APP_ID, Constants.APP_KEY, Site.CN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.welcome);
		new LoginTask().execute();
	}

	
	private class LoginTask extends AsyncTask<Void, Void, Void> {
		boolean resultCode = false;
		
		@Override
		protected Void doInBackground(Void... params) {

			SharedPreferences sp = getSharedPreferences(Constants.KIIUSER,
					MODE_PRIVATE);
			String token = sp.getString(Constants.TOKEN, "");
			try {
				KiiUser.loginWithToken(token);
			} catch (Exception e) {
				resultCode = true;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			if(resultCode){
				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				startActivity(intent);
			}else{
				SharedPreferences sp = getSharedPreferences(Constants.KIIUSER,
						MODE_PRIVATE);
				boolean status = sp.getBoolean(Constants.LOGIN_STATUS, false);
				String userName = sp.getString(Constants.USERNAME, "");
				if(status){
					Profile info = ProfileDao.getInstance().getProfile(
							WelcomeActivity.this, userName);
					if (info.name == null) {
						Intent intent = new Intent(WelcomeActivity.this,
								LoginActivity.class);
						startActivity(intent);
		
					} else {
						Intent intent = new Intent(WelcomeActivity.this,
								MainActivity.class);
						startActivity(intent);
					}
				}else{
					Intent intent = new Intent(WelcomeActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
			}
		}

	}


}
