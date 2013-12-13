package com.squad22.fit.dialogfragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TimePicker;

@SuppressLint("ValidFragment")
public class TimeDialogFragment extends DialogFragment implements OnTimeSetListener {
	int hourOfDay;
	int minute;
	Handler handler;

	public TimeDialogFragment(int hourOfDay, int minute, Handler handler) {
		this.hourOfDay = hourOfDay;
		this.minute = minute;
		this.handler = handler;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new TimePickerDialog(getActivity(), this, hourOfDay, minute, true);
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		
		String hour = "";
		if(hourOfDay < 10){
			hour = "0" + hourOfDay;
		}else{
			hour = String.valueOf(hourOfDay);
		}
		
		String minuteStr = "";
		if(minute < 10){
			minuteStr = "0" + minute;
		}else{
			minuteStr = String.valueOf(minute);
		}
		
		String date = hour + ":" + minuteStr;
		Message msg = new Message();
		msg.what = 2;
		msg.obj = date;
		handler.sendMessage(msg);
	}
	
}
