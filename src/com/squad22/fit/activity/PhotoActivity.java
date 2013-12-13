package com.squad22.fit.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Gallery;

import com.squad22.fit.R;
import com.squad22.fit.adapter.ImageAdapter;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

@SuppressWarnings("deprecation")
public class PhotoActivity extends Activity {
	Gallery gallery;
	ArrayList<byte[]> photos;
	byte[] resoucre;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		WindowManager.LayoutParams windowParams = getWindow().getAttributes();
		windowParams.width = LayoutParams.MATCH_PARENT; // 设置宽度
		windowParams.height = LayoutParams.MATCH_PARENT; // 设置高度
		getWindow().setAttributes(windowParams);
		setContentView(R.layout.photo_detail_layout);
		
		resoucre = getIntent().getByteArrayExtra(Constants.RECORD_PHOTO);
		

		
		new PhotoTask().execute();
		

		
		
		gallery = (Gallery) findViewById(R.id.gallery);
	
		
		gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PhotoActivity.this.finish();
			}
		});
	
	}
	
	private class PhotoTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			String userId = CommUtils.getUserId(PhotoActivity.this);
			ArrayList<MyRecords> arrList = MyRecordsDao.getInstance()
					.getRecordByDate(PhotoActivity.this, userId);
			
			photos = new ArrayList<byte[]>();
			for (MyRecords myRecords : arrList) {
				CommUtils.getPhoto(PhotoActivity.this,myRecords, photos);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			gallery.setAdapter(new ImageAdapter(PhotoActivity.this, photos));
			int index = 0;
			String selectPhoto = new String(resoucre);
			for (byte[] bs : photos) {
				String photo = new String(bs);
				if(photo.equals(selectPhoto)){
					gallery.setSelection(index);
				}
				index++;
			}
		}
		
	}
	
}
