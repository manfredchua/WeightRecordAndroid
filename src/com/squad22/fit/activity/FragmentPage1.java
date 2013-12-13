package com.squad22.fit.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kii.cloud.storage.KiiUser;
import com.squad22.fit.R;
import com.squad22.fit.dao.MeasurementDao;
import com.squad22.fit.dao.MyRecordsDao;
import com.squad22.fit.dao.ProfileDao;
import com.squad22.fit.entity.MyRecords;
import com.squad22.fit.entity.Profile;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.DensityUtil;
import com.squad22.fit.utils.ImagesUtils;

@SuppressLint({ "ValidFragment", "SimpleDateFormat" })
public class FragmentPage1 extends Fragment implements OnClickListener {

	ImageView ivHeadPortrait;
	TextView txtName;
	TextView txtProfileDetail;
	TextView txtTargetWeight;
	TextView txtCurrentWeight;
	TextView txtTargetCount;
	TextView txtTargetDesc;
	ImageView ivAlarms;
	ImageView ivAccount;
	ImageView ivEditProfile;
	LinearLayout llTargetMeasurement;
	LinearLayout hsvPhoto;
	MyRecordsDao myRecordsDao = MyRecordsDao.getInstance();
	ProfileDao dao = ProfileDao.getInstance();
	Profile info = new Profile();
	ArrayList<MyRecords> arrayList;
	ArrayList<byte[]> photos = new ArrayList<byte[]>();

	@SuppressWarnings("static-access")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("FragmentPage1.onCreateView()");

		View view = inflater.inflate(R.layout.profile_fragment_layout, null);
		initView(view);
		SharedPreferences sp = getActivity().getSharedPreferences(
				Constants.KIIUSER, getActivity().MODE_PRIVATE);
		String displayName = sp.getString(Constants.USERNAME, "");
		info = dao.getProfile(getActivity(), displayName);
		if (info.name != null) {
			setValue(info);
		}

		new PhotosTask().execute();
		return view;
	}

	private void initView(View view) {
		ivHeadPortrait = (ImageView) view.findViewById(R.id.iv_thumbnail);
		txtName = (TextView) view.findViewById(R.id.txt_name);
		txtTargetWeight = (TextView) view.findViewById(R.id.target_weight);
		txtCurrentWeight = (TextView) view.findViewById(R.id.txt_current_weight);
		txtTargetCount = (TextView) view.findViewById(R.id.txt_target_weight);
		txtTargetDesc = (TextView) view.findViewById(R.id.txt_target_desc);
		txtProfileDetail = (TextView) view.findViewById(R.id.txt_profile_detail);

		ivAlarms = (ImageView) view.findViewById(R.id.iv_alarms);
		ivAccount = (ImageView) view.findViewById(R.id.iv_account);
		llTargetMeasurement = (LinearLayout) view.findViewById(R.id.ll_target_measurement);
		hsvPhoto = (LinearLayout) view.findViewById(R.id.hsv_photo);
		ivEditProfile = (ImageView) view.findViewById(R.id.iv_edit_profile);

		llTargetMeasurement.getBackground().setAlpha(100);
		llTargetMeasurement.invalidate();
		
		ivAccount.getBackground().setAlpha(100);
		ivAccount.invalidate();
		
		ivEditProfile.getBackground().setAlpha(100);
		ivEditProfile.invalidate();
		
		ivHeadPortrait.setOnClickListener(this);
		ivAlarms.setOnClickListener(this);
		ivAccount.setOnClickListener(this);
		llTargetMeasurement.setOnClickListener(this);
		ivEditProfile.setOnClickListener(this);
	}

	private void setValue(Profile info) {
		txtName.setText(info.name);

		if (info.image == null || info.image.length() == 0) {
			ivHeadPortrait.setImageResource(R.drawable.default_profile_pic);
		} else {
			Bitmap bitmap = ImagesUtils.locDecodeImage(info.image);
			bitmap = ImagesUtils.toRoundBitmap(bitmap);
			ivHeadPortrait.setImageBitmap(bitmap);
		}

		try {
			if (info.sex != null) {
				int age = CommUtils.getAge(info.birthday);
				String unit = "";
				if (info.unit != null && info.unit.equals(Constants.IMPERIAL)) {
					unit = getString(R.string.imperial_height);
				} else {
					unit = getString(R.string.metric_height);
					
				}
				txtProfileDetail.setText(age + ",  " + info.sex + ",  " + info.height
						+ unit);
				String target = CommUtils
						.getTargetWeight(info.height, info.sex);

				txtTargetWeight.setText(target);

				double weight = MeasurementDao.getInstance().getWeight(
						getActivity(), info.id);
				if (weight == 0.0) {
					txtCurrentWeight.setText(String.valueOf(info.weight));
				} else {
					info.weight = weight;
					txtCurrentWeight.setText(CommUtils.getDouble(info.weight));
				}

				double count = Double.parseDouble(target) - info.weight;

				String currentCount = CommUtils.getDouble(count);
				if (currentCount.contains("-")) {
					txtTargetCount.setText(currentCount.subSequence(1,
							currentCount.length()));
				} else {
					txtTargetCount.setText(currentCount);
				}
				
				if (info.unit != null && info.unit.equals(Constants.IMPERIAL)) {
					unit = getString(R.string.imperial_weight);
				} else {
					unit = getString(R.string.metric_weight);
				}

				txtTargetDesc.setText(getString(R.string.poor) + unit);
			}
		} catch (NumberFormatException e) {
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_edit_profile:
			Intent intent = new Intent(getActivity(),
					CompleteProfileActivity.class);
			intent.putExtra("isEdit", true);
			getActivity().startActivity(intent);
			break;
		case R.id.iv_alarms:
			break;
		case R.id.iv_account:
			try {
				KiiUser currentUser = KiiUser.getCurrentUser();
				if (currentUser == null) {
					intent = new Intent(getActivity(), LoginActivity.class);
					intent.putExtra(Constants.SEARCH, true);
					startActivity(intent);
				} else {
					intent = new Intent(getActivity(), AccountActivity.class);
					startActivity(intent);
					getActivity().finish();
				}
			} catch (Exception e) {
				intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.ll_target_measurement:
			intent = new Intent(getActivity(), TargetMeasurementActivity.class);
			getActivity().startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	private class PhotosTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ArrayList<MyRecords> arrList = myRecordsDao.getRecordByDate(
					getActivity(), info.id);
			photos = new ArrayList<byte[]>();
			for (MyRecords myRecords : arrList) {
				CommUtils.getPhoto(getActivity(), myRecords, photos);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(hsvPhoto != null){
				hsvPhoto.removeAllViews();
			}

			if (photos != null && photos.size() > 0) {
				hsvPhoto.setVisibility(View.VISIBLE);

				int index = 0;
				for (final byte[] image : photos) {
					if(index < 6){
						View view = getActivity().getLayoutInflater().inflate(
								R.layout.photo_layout, null);
						final ImageView ivPhoto = (ImageView) view
								.findViewById(R.id.iv_photo);
						ivPhoto.setImageBitmap(BitmapFactory.decodeByteArray(image, 0,
								image.length));
						ivPhoto.setOnClickListener(new OnClickListener() {
		
							@Override
							public void onClick(View v) {
								// 展示原图
								Intent intent = new Intent(getActivity(),
										PhotoActivity.class);
								intent.putExtra(Constants.RECORD_PHOTO, image);
								getActivity().startActivity(intent);
							}
						});
		
						hsvPhoto.addView(view);
					}
					index++;
				}

				if (photos.size() >= 6) {

					TextView txtMorePhoto = new TextView(getActivity());
					txtMorePhoto.setText(getString(R.string.more_photos));
					txtMorePhoto.setLayoutParams(new LayoutParams(DensityUtil
							.dip2px(getActivity(), 120), DensityUtil.dip2px(
							getActivity(), 160)));
					txtMorePhoto.setGravity(Gravity.CENTER_VERTICAL);
					txtMorePhoto.setPadding(DensityUtil.dip2px(getActivity(), 10),
							0, 0, 0);
					hsvPhoto.addView(txtMorePhoto);
					txtMorePhoto.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),
									PhotoListActivity.class);
							getActivity().startActivity(intent);

						}
					});
				}

			} else {
				hsvPhoto.setVisibility(View.GONE);
			}
		}
		
	}

}
