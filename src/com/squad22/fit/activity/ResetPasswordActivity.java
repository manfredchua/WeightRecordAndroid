package com.squad22.fit.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.squad22.fit.R;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

public class ResetPasswordActivity extends Activity {

	ActionBar actionBar;
	EditText etOldPwd;
	EditText etNewPwd;
	EditText etComfirmPwd;
	Button btnReset;
	ProgressDialog proDialog;
	String oldPassword;
	String newPassword;
	String confirmPassword;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		
		setActionBar();
		setContentView(R.layout.reset_password_layout);
		
		initView();
		
	}
	
	private void initView(){
		etOldPwd = (EditText) findViewById(R.id.et_old_pwd);
		etNewPwd = (EditText) findViewById(R.id.et_new_pwd);
		etComfirmPwd = (EditText) findViewById(R.id.et_comfirm_pwd);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	class LoginTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			
			SharedPreferences sp = getSharedPreferences(Constants.KIIUSER, MODE_PRIVATE);
			String loginName = sp.getString(Constants.TOKEN, "");
			
			try{
				KiiUser user = KiiUser.loginWithToken(loginName);
				if(user != null){
					Editor editor = sp.edit();
					editor.putString(Constants.TOKEN, user.getAccessToken());
					editor.commit();
				}
			}catch(Exception e){
				Log.e("Login", "error---" + e.getMessage());
			}
			
			
			return null;
		}
		
	}

	
	private void setActionBar(){
		 final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
	                .getSystemService(LAYOUT_INFLATER_SERVICE);
	        final View customActionBarView = inflater.inflate(
	                R.layout.actionbar_custom_view_comfrim_cancel, null);
	        customActionBarView.findViewById(R.id.actionbar_comfrim).setOnClickListener(
	                new View.OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                    	 oldPassword = CommUtils.getEditTextValue(etOldPwd);
	            			 newPassword =  CommUtils.getEditTextValue(etNewPwd);
	            			 confirmPassword =  CommUtils.getEditTextValue(etComfirmPwd);
	            			
	            			if(!KiiUser.isValidPassword(oldPassword)){
	            				etOldPwd.requestFocus();
	            				etOldPwd.setError(getString(R.string.password_error));
	            			}else if(!KiiUser.isValidPassword(newPassword)){
	            				etNewPwd.requestFocus();
	            				etNewPwd.setError(getString(R.string.password_error));
	            			}else if(!confirmPassword.equals(newPassword)){
	            				etComfirmPwd.requestFocus();
	            				etComfirmPwd.setError(getString(R.string.confirm_password_error));
	            			}else{
	            				proDialog = CommUtils.createProgressDialog(ResetPasswordActivity.this, proDialog, "");
	            			
	            				final KiiUser user = KiiUser.getCurrentUser();
	            				user.changePassword(new KiiUserCallBack() {
	            					@Override
	            					public void onChangePasswordCompleted(int token,
	            							Exception exception) {
	            						super.onChangePasswordCompleted(token, exception);
	            						if(proDialog != null && proDialog.isShowing()){
	            							proDialog.dismiss();
	            						}
	            						if(exception == null){
	            							new LoginTask().execute();
	            							CommUtils.showToast(ResetPasswordActivity.this, "重设密码成功");
	            							ResetPasswordActivity.this.finish();
	            						}else{
	            							if(exception.getMessage() == null){
	            								CommUtils.showToast(ResetPasswordActivity.this, "重设密码失败");
	            							}else{
	            								CommUtils.showToast(ResetPasswordActivity.this, exception.getMessage());
	            							}
	            						}
	            					}
	            				}, newPassword, oldPassword);
	            			}

	                    }
	                });
	        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
	                new View.OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                    	finish();
	                    }
	                });

	        // Show the custom action bar view and hide the normal Home icon and title.
	        final ActionBar actionBar = getActionBar();
	        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.barColor));
	        actionBar.setDisplayOptions(
	                ActionBar.DISPLAY_SHOW_CUSTOM,
	                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
	                        | ActionBar.DISPLAY_SHOW_TITLE);
	        actionBar.setCustomView(customActionBarView,
	                new ActionBar.LayoutParams(
	                        ViewGroup.LayoutParams.MATCH_PARENT,
	                        ViewGroup.LayoutParams.MATCH_PARENT));
	}

	
}
