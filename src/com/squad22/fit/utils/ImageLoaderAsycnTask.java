package com.squad22.fit.utils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class ImageLoaderAsycnTask {

	private static final String TAG = "ImageLoaderAsycnTask";
	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	private View ivPhoto;
	
	public ImageLoaderAsycnTask(View ivPhoto){
		 this.ivPhoto = ivPhoto;
	}

	public Bitmap load(final String imageUrl,final ImageCallBack imageCallBack,final Handler handler) {
		// 如果缓存过就从缓存中取出数据
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			if (softReference.get() != null) {
				return softReference.get();
			}
		}
		
		
		// 缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
		executorService.submit(new Runnable() {
			public void run() {
				try {
					final Bitmap orgBitmap = ImagesUtils.decodeImage(imageUrl);
					imageCache.put(imageUrl, new SoftReference<Bitmap>(orgBitmap));
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							imageCallBack.imageLoad(ivPhoto, orgBitmap); 
						}
					});
				} catch (Exception e) {
					Log.i(TAG, e + "-----" + e.getMessage());
				}
			}
		});
		return null;
	}

	
	/**
	 * 释放缓存中所有的Bitmap对象，并将缓存清空
	 */
	public void releaseBitmapCache() {
		if (imageCache != null) {
			for (Entry<String, SoftReference<Bitmap>> entry : imageCache
					.entrySet()) {
				Bitmap bitmap = entry.getValue().get();
				if (bitmap != null) {
					bitmap.recycle();// 释放bitmap对象
				}
			}
			imageCache.clear();
		}
	}
	
	 public interface ImageCallBack  
	    {  
	        public void imageLoad(View imageView, Bitmap bitmap);  
	    }  
}
