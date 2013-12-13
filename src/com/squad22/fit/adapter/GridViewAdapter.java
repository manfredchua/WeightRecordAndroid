package com.squad22.fit.adapter;

import java.util.ArrayList;

import com.squad22.fit.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<byte[]> photos;

	public GridViewAdapter(Context context,
			ArrayList<byte[]> photos) {
		mInflater = LayoutInflater.from(context);
		this.photos = photos;
	}

	public int getCount() {
		return photos.size();
	}

	public Object getItem(int position) {
		return photos.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.photo_layout, null);

		} else {
			view = convertView;
		}
		
		byte[] photo = photos.get(position);
		ImageView imageView = (ImageView) view.findViewById(R.id.iv_photo);
		Bitmap bm = BitmapFactory.decodeByteArray(photo, 0, photo.length);
		imageView.setImageBitmap(bm);
	        
		return view;
	}
}