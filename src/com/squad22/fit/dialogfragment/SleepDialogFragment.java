package com.squad22.fit.dialogfragment;

import java.util.Date;

import com.squad22.fit.activity.MainActivity;
import com.squad22.fit.activity.RecordSleepActivity;
import com.squad22.fit.utils.Constants;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

@SuppressLint("ValidFragment")
public class SleepDialogFragment extends DialogFragment {
	Activity activity;
	
	public SleepDialogFragment(MainActivity activity){
		this.activity = activity;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("选项");
		builder.setItems(Constants.sleep, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					Intent intent = new Intent(activity, RecordSleepActivity.class);
					intent.putExtra(Constants.CURRENT_DATE, new Date());
					startActivity(intent);
					dialog.dismiss();
					break;
				case 1:
					dialog.dismiss();
					break;
				default:
					break;
				}
				
			}
		});

		return builder.create();
	}
}
