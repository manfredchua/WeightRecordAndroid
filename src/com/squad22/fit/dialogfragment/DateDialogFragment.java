package com.squad22.fit.dialogfragment;

import com.squad22.fit.utils.Constants;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.DatePicker;

@SuppressLint("ValidFragment")
public class DateDialogFragment extends DialogFragment implements OnDateSetListener {
	int year;
	int month;
	int day;
	Handler handler;

	public DateDialogFragment(int year, int month, int day, Handler handler) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.handler = handler;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		String date = year + "年" + (month + 1) + "月" + day + "日";
		Message msg = new Message();
		msg.what = 1;
		Bundle data = new Bundle();
		data.putString(Constants.CURRENT_DATE, year+"-"+(month + 1) + "-" + day);
		data.putString("createDate", date);
		msg.setData(data);
		handler.sendMessage(msg);
	}

}
