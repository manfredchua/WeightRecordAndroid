package com.squad22.fit.dialogfragment;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

@SuppressLint({ "ValidFragment", "SimpleDateFormat" })
public class PhotoDialogFragment extends DialogFragment {
	Activity activity;
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy-MM-dd");
	
	public PhotoDialogFragment(Activity activity){
		this.activity = activity;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("添加图片");
		builder.setItems(Constants.Photo, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if (which == 0) {
					// 调用拍照方法
					CommUtils.doTakePhoto(activity);
				} else if (which == 1) {
					// 图库

					Intent intents = new Intent();
					intents.setType("image/*");
					intents.setAction(Intent.ACTION_GET_CONTENT);
					activity.startActivityForResult(intents, Constants.GALLERY);
				}
				dialog.dismiss();
			}
		});

		return builder.create();
	}
}
