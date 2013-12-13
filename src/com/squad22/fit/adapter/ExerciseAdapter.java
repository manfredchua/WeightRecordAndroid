package com.squad22.fit.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.dao.ProfileDao;
import com.squad22.fit.entity.ActivityEntity;
import com.squad22.fit.entity.Profile;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;

public class ExerciseAdapter extends BaseAdapter {

	Context mContext;
	ArrayList<ActivityEntity> arrayList;
	Profile profile;

	@SuppressWarnings("static-access")
	public ExerciseAdapter(Context mContext, ArrayList<ActivityEntity> arrayList) {
		this.mContext = mContext;
		this.arrayList = arrayList;
		SharedPreferences sp = mContext.getSharedPreferences(Constants.KIIUSER, mContext.MODE_PRIVATE);
		String userName = sp.getString(Constants.USERNAME, "");
		profile = ProfileDao.getInstance().getProfile(mContext, userName);
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

		TextView txtDescription = (TextView) view
				.findViewById(R.id.txt_description);

		ActivityEntity entity = arrayList.get(position);
		txtText.setText(entity.name);

		double weight = MeasurementDao.getInstance().getWeight(mContext, profile.id);
		if(weight != 0.0){
			profile.weight = weight;
		}
		double calorie = 0;
		if (profile.weight <= Constants.kcal1 ) {
			calorie = profile.weight / Constants.kcal1 * entity.calorie1;
		} else if (profile.weight <= Constants.kcal2) {
			calorie = profile.weight / Constants.kcal2 * entity.calorie2;
		} else if (profile.weight <= Constants.kcal3) {
			calorie = profile.weight / Constants.kcal3 * entity.calorie3;
		} else if (profile.weight <= Constants.kcal4) {
			calorie = profile.weight / Constants.kcal4 * entity.calorie4;
		}
		String kcal = CommUtils.getString(calorie);
		entity.targetKcal = kcal;
		txtDescription.setText(kcal + "卡路里");
		return view;
	}

}
