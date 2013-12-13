package com.squad22.fit.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.Kii.Site;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.squad22.fit.R;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

public class RegisterActivity extends Activity{
	ActionBar actionBar;
	EditText etNickName;
	EditText etEmail;
	EditText etPassword;
	Button btnLogin;
	Button btnRegister;
	ProgressDialog proDialog;
	
	static {
		Kii.initialize(Constants.APP_ID, Constants.APP_KEY, Site.CN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		
		setActionBar();
		setContentView(R.layout.register_layout);

		initView();
	}

	private void initView() {
		etNickName = (EditText) findViewById(R.id.et_nick_name);
		etEmail = (EditText) findViewById(R.id.et_email);
		etPassword = (EditText) findViewById(R.id.et_password);

	}

	private void setActionBar(){
		 final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
	                .getSystemService(LAYOUT_INFLATER_SERVICE);
	        final View customActionBarView = inflater.inflate(
	                R.layout.actionbar_custom_view_register_cancel, null);
	        customActionBarView.findViewById(R.id.actionbar_register).setOnClickListener(
	                new View.OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	            			String nickName = CommUtils.getEditTextValue(etNickName);
	            			String email = CommUtils.getEditTextValue(etEmail);
	            			String pwd = CommUtils.getEditTextValue(etPassword);
	            			if(!KiiUser.isValidUserName(nickName) && nickName.length() > 4){
	            				etNickName.setError(getString(R.string.nick_name__error));
	            				etNickName.requestFocus();
	            			}else if(!KiiUser.isValidEmail(email)){
	            				etEmail.setError(getString(R.string.email_error));
	            				etEmail.requestFocus();
	            			}else if(!KiiUser.isValidPassword(pwd) && pwd.length() > 6){
	            				etPassword.setError(getString(R.string.password_error));
	            				etPassword.requestFocus();
	            			}else{
	            				
	            				proDialog = CommUtils.createProgressDialog(RegisterActivity.this, proDialog, "");
	            				KiiUser user = KiiUser.createWithEmail(nickName, email);
	            				user.register(new KiiUserCallBack() {
	            					@Override
	            					public void onRegisterCompleted(int token, KiiUser user,
	            							Exception exception) {
	            						if(proDialog != null && proDialog.isShowing()){
	            							proDialog.dismiss();
	            						}
	            						if(exception == null){
	            							SharedPreferences sp = getSharedPreferences(Constants.KIIUSER,
	            									MODE_PRIVATE);
	            							Editor editor = sp.edit();
	            							editor.putString(Constants.TOKEN, user.getAccessToken());
	            							editor.putBoolean(Constants.LOGIN_STATUS, true);
	            							editor.putString(Constants.USERNAME, user.getUsername());
	            							editor.commit();

	            							Intent intent = new Intent(RegisterActivity.this, CompleteProfileActivity.class);
	            							startActivity(intent);
	            							CommUtils.showToast(RegisterActivity.this, getString(R.string.register_success));
	            						}else{
	            							if(exception.getMessage() != null){
	            								CommUtils.showError(RegisterActivity.this, exception.getMessage());
	            							}else{
	            								CommUtils.showToast(RegisterActivity.this, getString(R.string.register_failure));
	            							}
	            						}
	            						super.onRegisterCompleted(token, user, exception);
	            					}
	            				}, pwd);
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
