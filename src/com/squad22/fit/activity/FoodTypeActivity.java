package com.squad22.fit.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ViewFlipper;

import com.squad22.fit.R;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.WeightRecordWebView;

@SuppressLint("SetJavaScriptEnabled")
public class FoodTypeActivity extends Activity {

	ActionBar actionBar;
	ViewFlipper mViewFlipper;
	WeightRecordWebView myWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.more_food_html_layout);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.more));
		
		CommUtils.showToast(this, "1/3");

		mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

		mViewFlipper
				.addView(addWebView("file:///android_asset/zh_html/GreenFood.html"));

		mViewFlipper
				.addView(addWebView("file:///android_asset/zh_html/YellowFood.html"));

		mViewFlipper
				.addView(addWebView("file:///android_asset/zh_html/RedFood.html"));

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

	private View addWebView(String url) {

		myWebView = new WeightRecordWebView(this, mViewFlipper, false);

		// 设置WebView属性，能够执行Javascript脚本
		myWebView.getSettings().setJavaScriptEnabled(true);

		// 加载需要显示的网页
		myWebView.loadUrl(url);

		// 使WebView的网页跳转在WebView中进行，而非跳到浏览器
		myWebView.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				view.loadUrl(url);

				return true;

			}

		});

		return myWebView;

	}
	
	
}
