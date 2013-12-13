package com.squad22.fit.dialogfragment;

import com.squad22.fit.activity.AccountActivity;
import com.squad22.fit.activity.CompleteProfileActivity;
import com.squad22.fit.activity.LoginActivity;
import com.squad22.fit.activity.MainActivity;
import com.squad22.fit.activity.SettingsActivity;
import com.squad22.fit.utils.Constants;

import com.kii.cloud.storage.KiiUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

@SuppressLint("ValidFragment")
public class SettingsDialogFragment extends DialogFragment {
	Activity activity;
	
	public SettingsDialogFragment(MainActivity activity){
		this.activity = activity;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.setStyle(STYLE_NO_TITLE, 0);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("选项");
		builder.setItems(Constants.items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					Intent intent = new Intent(activity,
							CompleteProfileActivity.class);
					intent.putExtra("isEdit", true);
					startActivity(intent);
					dialog.dismiss();
					break;
				case 1:
					try {
						KiiUser user = KiiUser.getCurrentUser();
						if (user == null) {
							intent = new Intent(activity,
									LoginActivity.class);
							startActivity(intent);
						} else {
							intent = new Intent(activity,
									AccountActivity.class);
							startActivity(intent);
						}
					} catch (Exception e) {
						intent = new Intent(activity,
								LoginActivity.class);
						startActivity(intent);
					}
					dialog.dismiss();

					break;
				case 2:
					intent = new Intent(activity,
							SettingsActivity.class);
					startActivity(intent);
					dialog.dismiss();
					break;
				case 3:
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
