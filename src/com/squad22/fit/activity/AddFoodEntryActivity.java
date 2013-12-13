package com.squad22.fit.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.dao.FoodDao;
import com.squad22.fit.dialogfragment.FoodTypeDialogFragment;
import com.squad22.fit.entity.Food;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.DensityUtil;
import com.squad22.fit.wheelview.ArrayWheelAdapter;
import com.squad22.fit.wheelview.WheelView;

public class AddFoodEntryActivity extends Activity implements OnClickListener {

	ActionBar actionBar;
	EditText etFoodName;
	EditText etPortion;
	EditText etNumber;
	EditText etKcal;
	EditText etType;
	Spinner spinner;
	boolean isEdit;
	Food food = new Food();
	Food foodNumber = new Food();
	FoodDao dao = FoodDao.getInstance();
	ArrayAdapter<String> adapter;
	ArrayList<String> units = new ArrayList<String>();
	ArrayList<String> weight = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setActionBar();

		setContentView(R.layout.add_food_entry_layout);
		
		isEdit = getIntent().getBooleanExtra(Constants.ISEDIT, false);

		if (isEdit) {
			food = (Food) getIntent().getSerializableExtra(Constants.FOOD);
		}else{
			food.name = getIntent().getStringExtra(Constants.FOOD_NAME);
		}
		
		initView();

		etFoodName.setText(food.name);
		etPortion.setText("1拳头");
		
		if(isEdit){
			etNumber.setText(food.qty);
			etKcal.setText(food.kcal);
			etType.setText(food.category);
		}
	}

	private void initView() {
		etFoodName = (EditText) findViewById(R.id.et_name);
		etPortion = (EditText) findViewById(R.id.et_portion);
		etNumber = (EditText) findViewById(R.id.et_number);
		etKcal = (EditText) findViewById(R.id.et_kcal);
		etType = (EditText) findViewById(R.id.et_type);
		spinner = (Spinner) findViewById(R.id.spinner);

		etType.setFocusable(false);
		etType.setOnClickListener(this);

		etPortion.setFocusable(false);
		etPortion.setOnClickListener(this);

		units.add("毫升");
		units.add("升");
		units.add("杯");
		units.add("汤匙");
		units.add("茶匙");
		units.add("品脱");

		weight.add("公斤");
		weight.add("英镑");
		weight.add("盎司");
		weight.add("克");
		weight.add("斤");

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Constants.units);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		int index = 0;
		for (String unit : Constants.units) {

			if(food.unit != null && !food.unit.equals("")){
				if (unit.equals(food.unit)) {
					spinner.setSelection(index);
					foodNumber.unit = unit;
				}
			}
			index++;
		}

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				spinner.setSelection(arg2);
				foodNumber.unit = Constants.units[arg2];

				String number = etNumber.getText().toString().trim();
				if (number != null && !number.equals("")) {
					foodNumber.qty = number;
					if(food.unit != null && !food.unit.equals("") ){
						double qty = formatUnit(food, foodNumber);
						try {
							if (food.kcal != null && !food.kcal.equals("")) {
								foodNumber.kcal = CommUtils.getString(Double
										.parseDouble(food.kcal) * qty);
								etKcal.setText(foodNumber.kcal);
							}
	
						} catch (NumberFormatException e) {
						}
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		etNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String number = s.toString().trim();
				if (number != null && !number.equals("")) {
					foodNumber.qty = number;
					
					if(food.qty != null && !food.qty.equals("")){
						double qty = formatUnit(food, foodNumber);
						try {
							if (food.kcal != null && !food.kcal.equals("")) {
								foodNumber.kcal = CommUtils.getString(Double
										.parseDouble(food.kcal) * qty);
								etKcal.setText(foodNumber.kcal);
							}
	
						} catch (NumberFormatException e) {
						}
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.et_type:
			FoodTypeDialogFragment fragment = new FoodTypeDialogFragment(this,
					etType);
			fragment.show(getFragmentManager(), "type");
			break;
		case R.id.et_portion:
			setPortion(etPortion);
			break;
		case R.id.et_number:
			showNumber();
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void setPortion(final EditText etPortion) {
		View view = getLayoutInflater().inflate(R.layout.portion_layout, null);
		final String[] items = { "0.0", "0.5", "1.0", "1.5", "2.0", "2.5",
				"3.0" };
		int textSize = 0;
		textSize = CommUtils.adjustFontSize(getWindow().getWindowManager());
		final WheelView wvNumber = (WheelView) view
				.findViewById(R.id.wv_number);

		final TextView txtfist = (TextView) view.findViewById(R.id.txt_fist);
		final TextView txtPalm = (TextView) view.findViewById(R.id.txt_palm);
		Button btnConfirm = (Button) view.findViewById(R.id.confirm);

		wvNumber.setAdapter(new ArrayWheelAdapter<String>(items, items.length));//
		wvNumber.setCyclic(true);
		wvNumber.setLabel("拳头");
		wvNumber.TEXT_SIZE = textSize;

		final PopupWindow pw = new PopupWindow(view, DensityUtil.dip2px(this,
				220), DensityUtil.dip2px(this, 320), true);
		pw.setBackgroundDrawable(new BitmapDrawable());
		pw.showAsDropDown(etPortion, 0, 0);

		final OnPortionSetListener mCallBack = new OnPortionSetListener() {

			@Override
			public void onPortionSet(String number) {
				etPortion.setText(number + wvNumber.getLabel());
			}
		};

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCallBack != null) {
					int item = wvNumber.getCurrentItem();

					mCallBack.onPortionSet(items[item]);
				}
				if (pw != null && pw.isShowing()) {
					pw.dismiss();
				}
			}
		});

		txtfist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				wvNumber.setLabel("拳头");
				txtfist.setBackgroundColor(getResources()
						.getColor(R.color.bule));
				txtPalm.setBackgroundColor(Color.WHITE);
			}
		});

		txtPalm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				wvNumber.setLabel("掌心");
				txtfist.setBackgroundColor(Color.WHITE);
				txtPalm.setBackgroundColor(getResources()
						.getColor(R.color.bule));
			}
		});

	}

	public interface OnPortionSetListener {
		void onPortionSet(String number);
	}

	@SuppressWarnings("deprecation")
	private void showNumber() {
		View view = getLayoutInflater().inflate(R.layout.number_layout, null);
		final String[] items = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
				"9" };
		int textSize = 0;
		textSize = CommUtils.adjustFontSize(getWindow().getWindowManager());
		final WheelView wvNumber = (WheelView) view
				.findViewById(R.id.wv_number);
		final WheelView wvUnit = (WheelView) view.findViewById(R.id.wv_unit);

		Button btnConfirm = (Button) view.findViewById(R.id.confirm);

		wvNumber.setAdapter(new ArrayWheelAdapter<String>(items, items.length));//
		wvNumber.setCyclic(true);
		wvNumber.TEXT_SIZE = textSize;

		wvUnit.setAdapter(new ArrayWheelAdapter<String>(Constants.units,
				Constants.units.length));
		wvUnit.setCyclic(true);
		wvUnit.TEXT_SIZE = textSize;

		final PopupWindow pw = new PopupWindow(view, DensityUtil.dip2px(this,
				220), DensityUtil.dip2px(this, 320), true);
		pw.setBackgroundDrawable(new BitmapDrawable());
		pw.showAsDropDown(etPortion, 0, 0);

		final OnNumberSetListener mCallBack = new OnNumberSetListener() {

			@Override
			public void onNumberSet(String number) {
				etNumber.setText(number);
			}
		};

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCallBack != null) {
					int item = wvNumber.getCurrentItem();
					int unit = wvUnit.getCurrentItem();

					food.qty = items[item];
					food.unit = Constants.units[unit];
					mCallBack.onNumberSet(items[item] + Constants.units[unit]);
				}
				if (pw != null && pw.isShowing()) {
					pw.dismiss();
				}
			}
		});
	}

	public interface OnNumberSetListener {
		void onNumberSet(String number);
	}

	private double formatUnit(Food food, Food foodNum) {
		double result = 0;
		try {
			if (units.contains(food.unit) && units.contains(foodNum.unit)) {
				double firstMl = CommUtils.formatML(food.unit);
				double secondMl = CommUtils.formatML(foodNum.unit);

				double qty = Double.parseDouble(food.qty) * firstMl;
				double qty2 = Double.parseDouble(foodNum.qty) * secondMl;

				result = qty2 / qty;
			} else if (weight.contains(food.unit)
					&& weight.contains(foodNum.unit)) {
				double firstG = CommUtils.formatG(food.unit);
				double secondG = CommUtils.formatG(foodNum.unit);

				double qty = Double.parseDouble(food.qty) * firstG;
				double qty2 = Double.parseDouble(foodNum.qty) * secondG;

				result = qty2 / qty;
			}
			
			if(food.unit.equals(foodNum.unit)){
				double qty = Double.parseDouble(food.qty);
				double qty2 = Double.parseDouble(foodNum.qty);
				result = qty2 / qty;
			}
		} catch (NumberFormatException e) {
		}

		return result;
	}

	private void setActionBar() {
		final LayoutInflater inflater = (LayoutInflater) getActionBar()
				.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = inflater.inflate(
				R.layout.actionbar_custom_view_done_cancel, null);
		customActionBarView.findViewById(R.id.actionbar_done)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Food food = new Food();
						food.foodId = AddFoodEntryActivity.this.food.foodId;
						food.qty = etNumber.getText().toString().trim();
						food.unit = foodNumber.unit;
						food.name = etFoodName.getText().toString().trim();
						food.category = etType.getText().toString().trim();
						food.kcal = etKcal.getText().toString().trim();
						food.portion = etPortion.getText().toString().trim();
						if (food.name.equals("")) {
							etFoodName
									.setError(getString(R.string.food_name_error));
							etFoodName.requestFocus();
						} else if (food.qty.equals("")) {
							etNumber.setError(getString(R.string.number_error));
							etNumber.requestFocus();
						} else if (food.category.equals("")) {
							CommUtils.showToast(AddFoodEntryActivity.this,
									getString(R.string.type_error));
						} else {
							if (isEdit) {
								Intent data = getIntent();
								data.putExtra(Constants.FOOD, food);
								setResult(RESULT_OK, data);
								finish();
							} else {
								if (dao.getFood(AddFoodEntryActivity.this,
										food.name)) {
									SharedPreferences sp = getSharedPreferences(Constants.KIIUSER, MODE_PRIVATE);
									String userName = sp.getString(Constants.USERNAME, "a");
									food.foodId = String.valueOf(userName + dao
											.getAllFood(
													AddFoodEntryActivity.this)
											.size() + 1);
									if (dao.insert(AddFoodEntryActivity.this,
											food)) {
										CommUtils.showToast(
												AddFoodEntryActivity.this,
												"添加食物成功");
										Intent data = getIntent();
										data.putExtra(Constants.FOOD, food);
										setResult(RESULT_OK, data);
										finish();
									} else {
										CommUtils.showToast(
												AddFoodEntryActivity.this,
												"添加食物失败");
									}
								} else {
									Intent data = getIntent();
									data.putExtra(Constants.FOOD, food);
									setResult(RESULT_OK, data);
									finish();
								}
							}
						}

					}
				});
		customActionBarView.findViewById(R.id.actionbar_cancel)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						 finish();
					}
				});

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView,
				new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.barColor));
	}
	
}
