package com.squad22.fit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squad22.fit.R;

public class FragmentPage3 extends Fragment implements OnClickListener {
	TextView txtFoodType;
	TextView txtServingReference;
	TextView txtFollowUs;
	TextView txtDisclaimerNote;
//	TextView txtSetting;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("FragmentPage3.onCreateView()");
		View view = inflater.inflate(R.layout.more_layout, null);

		initView(view);

		return view;
	}

	private void initView(View view) {
		txtFoodType = (TextView) view.findViewById(R.id.txt_food_type);
		txtServingReference = (TextView) view
				.findViewById(R.id.txt_serving_reference);
		txtFollowUs = (TextView) view.findViewById(R.id.txt_follow_us);
		txtDisclaimerNote = (TextView) view
				.findViewById(R.id.txt_disclaimer_note);
//		txtSetting = (TextView) view.findViewById(R.id.txt_setting);

		txtFoodType.setOnClickListener(this);
		txtServingReference.setOnClickListener(this);
		txtFollowUs.setOnClickListener(this);
		txtDisclaimerNote.setOnClickListener(this);
//		txtSetting.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_food_type:
			Intent intent = new Intent(getActivity(), FoodTypeActivity.class);
			getActivity().startActivity(intent);
			break;
		case R.id.txt_serving_reference:
			intent = new Intent(getActivity(), ServingReferenceActivity.class);
			getActivity().startActivity(intent);
			break;
		case R.id.txt_follow_us:
			// intent = new Intent();
			// intent.setAction("android.intent.action.VIEW");
			// Uri CONTENT_URI_BROWSERS = Uri.parse("http://weibo.com/22fit");
			// intent.setData(CONTENT_URI_BROWSERS);
			// intent.setClassName("com.android.browser",
			// "com.android.browser.BrowserActivity");
			intent = new Intent(getActivity(), FollowUsActivity.class);
			startActivity(intent);

			break;
		case R.id.txt_disclaimer_note:
			intent = new Intent(getActivity(), DisclaimerNoteActivity.class);
			getActivity().startActivity(intent);
			break;
//		case R.id.txt_setting:
//			intent = new Intent(getActivity(), SettingsActivity.class);
//			getActivity().startActivity(intent);
//			break;
		default:
			break;
		}
	}

}
