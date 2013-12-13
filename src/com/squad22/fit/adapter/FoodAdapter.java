package com.squad22.fit.adapter;

import java.util.ArrayList;

import com.squad22.fit.R;
import com.squad22.fit.entity.Food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FoodAdapter extends BaseAdapter {

	Context mContext;
	ArrayList<Food> arrayList;

	public FoodAdapter(Context mContext, ArrayList<Food> arrayList) {
		this.mContext = mContext;
		this.arrayList = arrayList;
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.food_item_layout, null);
		} else {
			view = convertView;
		}

		TextView txtText = (TextView) view.findViewById(R.id.txt_title);
		TextView txtDescription = (TextView) view.findViewById(R.id.txt_description);

		Food entity = arrayList.get(position);
		txtText.setText(entity.name);
		txtDescription.setText(entity.qty + entity.unit + "-" + entity.kcal + "卡路里");
		
		return view;
	}

}
