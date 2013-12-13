package com.squad22.fit.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.squad22.fit.R;

@SuppressLint("SetJavaScriptEnabled")
public class DisclaimerNoteActivity extends Activity {

	ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.more_html_layout);
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.more));
		
		WebView wvHtml = (WebView) findViewById(R.id.wv_html);
		wvHtml.getSettings().setJavaScriptEnabled(true);  
		wvHtml.loadUrl("file:///android_asset/zh_html/Disclaimer.html");  
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
