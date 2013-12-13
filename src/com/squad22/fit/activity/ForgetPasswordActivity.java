package com.squad22.fit.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.squad22.fit.R;
import com.squad22.fit.utils.CommUtils;

@SuppressLint("HandlerLeak")
public class ForgetPasswordActivity extends Activity{
	
	private static final String TAG = "ForgetPasswordActivity";
	private EditText etEmail;
	private ProgressDialog proDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this); 
		setActionBar();
		setContentView(R.layout.forget_password_layout);
		
		
		etEmail = (EditText)findViewById(R.id.ed_email);
		
		
	}

	private void setActionBar(){
		 final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
	                .getSystemService(LAYOUT_INFLATER_SERVICE);
	        final View customActionBarView = inflater.inflate(
	                R.layout.actionbar_custom_view_cancel_send, null);
	        customActionBarView.findViewById(R.id.actionbar_send).setOnClickListener(
	                new View.OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                    	final String email=  CommUtils.getEditTextValue(etEmail);
	            			if(!KiiUser.isValidEmail(email)){
	            				etEmail.requestFocus();
	            				etEmail.setError(getString(R.string.email_error));
	            			}else{
	            			
	            				KiiUser.resetPassword(new KiiUserCallBack() {
	            	
	            					@Override
	            					public void onResetPasswordCompleted(int token,
	            							Exception exception) {
	            						if(proDialog != null && proDialog.isShowing()){
	            							proDialog.dismiss();
	            						}
	            						if(exception == null){
	            							ForgetPasswordActivity.this.finish();
	            							CommUtils.showToast(ForgetPasswordActivity.this, getString(R.string.send_success));
	            						}else{
	            							Log.i(TAG,  exception + "---" + exception.getMessage());
	            							CommUtils.showToast(ForgetPasswordActivity.this, getString(R.string.send_failure));
	            						}
	            						super.onResetPasswordCompleted(token, exception);
	            					}
	            					
	            				}, email);
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
