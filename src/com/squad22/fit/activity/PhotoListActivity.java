package com.squad22.fit.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.squad22.fit.R;
import com.squad22.fit.adapter.GridViewAdapter;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

public class PhotoListActivity extends Activity {

	ActionBar actionBar;
	ArrayList<byte[]> photos;
	GridView gridView;
	MyRecordsDao myRecordsDao = MyRecordsDao.getInstance();
	ProgressDialog proDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.grid_view);
		
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("记录的图片");
		proDialog = CommUtils.createProgressDialog(this, proDialog, "加载中...");
		new PhotosTask().execute();
		
		gridView = (GridView) findViewById(R.id.gridview);
		
		
		
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 展示原图
				Intent intent = new Intent(PhotoListActivity.this,
						PhotoActivity.class);
				intent.putExtra(Constants.RECORD_PHOTO, photos.get(position));
				startActivity(intent);
			}
		});
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
	
	private class PhotosTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			photos = new ArrayList<byte[]>();
			ArrayList<MyRecords> arrList = myRecordsDao.getRecordByDate(PhotoListActivity.this, CommUtils.getUserId(PhotoListActivity.this));
			for (MyRecords myRecords : arrList) {
				CommUtils.getPhoto(PhotoListActivity.this, myRecords, photos);
			}
			return null;
		}
		
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(proDialog != null && proDialog.isShowing()){
				proDialog.dismiss();
			}
			gridView.setAdapter(new GridViewAdapter(PhotoListActivity.this, photos));
		}
	}
}
