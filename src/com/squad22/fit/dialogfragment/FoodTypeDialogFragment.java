package com.squad22.fit.dialogfragment;

import com.squad22.fit.activity.AddFoodEntryActivity;
import com.squad22.fit.utils.Constants;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

@SuppressLint("ValidFragment")
public class FoodTypeDialogFragment extends DialogFragment {
	
	AddFoodEntryActivity activity;
	EditText etType;
	
	public FoodTypeDialogFragment(AddFoodEntryActivity activity, EditText etType){
		this.activity = activity;
		this.etType = etType;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("选项");
		builder.setItems(Constants.foodType, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				etType.setText(Constants.foodType[which]);
			}
		});

		return builder.create();
	}

}
