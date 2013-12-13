package com.squad22.fit.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<byte[]> photo;

	public ImageAdapter(Context c, ArrayList<byte[]> photo) {
		mContext = c;
		this.photo = photo;
	}

	public int getCount() {
		return photo.size();
	}

	/* 利用getItem方法，取得目前容器中图像的数组ID */
	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	
	

	/* 取得目前欲显示的图像View，传入数组ID值使之读取与成像 */
	public View getView(int position, View convertView, ViewGroup parent) {
		/* 创建一个ImageView对象 */
		ImageView i = new ImageView(mContext);
		try {
			i.setImageBitmap(BitmapFactory.decodeByteArray(photo.get(position),
					0, photo.get(position).length));
			/* 设置这个ImageView对象的宽高，单位为dip */
			Display display = ((Activity) mContext).getWindowManager()
					.getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();

			i.setLayoutParams(new Gallery.LayoutParams(width, height));
		} catch (Exception e) {
		}
		return i;
	}

}