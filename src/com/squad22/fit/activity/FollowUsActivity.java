package com.squad22.fit.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squad22.fit.R;

@SuppressLint("SetJavaScriptEnabled")
public class FollowUsActivity extends Activity {

	ActionBar actionBar;
	ProgressDialog  mProgressDialog ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.more_html_layout);
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.more));
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.show();

		WebView wvHtml = (WebView) findViewById(R.id.wv_html);
		wvHtml.getSettings().setJavaScriptEnabled(true);
		wvHtml.loadUrl("http://weibo.com/22fit");
		wvHtml.setWebViewClient(new Client());

	}

	class Client extends WebViewClient {
		Context context;
		
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mProgressDialog.dismiss();
		}

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
