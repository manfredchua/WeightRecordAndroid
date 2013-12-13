package com.squad22.fit.adapter;

import com.squad22.fit.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PhotoAdapter extends BaseAdapter{
	private Context context;
	private String [] data;
	
	public PhotoAdapter(Context context, String[] data){
		this.context = context;
		this.data = data;
	}
	
	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if(convertView == null){
			view = LayoutInflater.from(context).inflate(R.layout.pw_item_layout, null);
		}else{
			view = convertView;
		}
		
		TextView txtValue = (TextView) view.findViewById(R.id.txt_position_value);
		txtValue.setText(data[position]);
		
		return view;
	}

}
