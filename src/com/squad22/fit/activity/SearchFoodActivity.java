package com.squad22.fit.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.adapter.FoodAdapter;
import com.squad22.fit.dao.FoodDao;
import com.squad22.fit.dao.MealDetailDao;
import com.squad22.fit.entity.Food;
import com.squad22.fit.utils.Constants;

public class SearchFoodActivity extends FragmentActivity implements
		OnClickListener {

	ActionBar actionBar;
	ViewPager pager;
	ListView lvFoods;
	ListView lvKiiFoods;
	TextView txtAdd;
	TextView txtTitle;
	String foodName;
	ArrayList<Food> arrayList;
	ArrayList<Food> kiiFoods;
	FoodDao dao = FoodDao.getInstance();
	View footerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.search_food_layout);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");

		lvFoods = (ListView) findViewById(R.id.lv_my_foods);
		lvFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(SearchFoodActivity.this,
						AddFoodEntryActivity.class);
				Food food = arrayList.get(position - 1);
				intent.putExtra(Constants.FOOD, food);
				intent.putExtra(Constants.ISEDIT, true);
				startActivityForResult(intent, Constants.SUCCESS);
			}
		});

		View headerView = getLayoutInflater().inflate(R.layout.header_layout,
				null);
		txtTitle = (TextView) headerView.findViewById(R.id.txt_title);
		txtTitle.setText("常吃食物");
		lvFoods.addHeaderView(headerView, null, false);

		footerView = getLayoutInflater().inflate(R.layout.footer_layout, null);
		txtAdd = (TextView) footerView.findViewById(R.id.txt_add);
		txtAdd.setOnClickListener(SearchFoodActivity.this);
		lvFoods.addFooterView(footerView);
		footerView.setVisibility(View.GONE);

		getMeal();

		lvFoods.setAdapter(new FoodAdapter(this, arrayList));
	}

	private void getMeal() {
		ArrayList<String> foodIds = MealDetailDao.getInstance().getFoodId(this);

		arrayList = new ArrayList<Food>();

		for (String foodId : foodIds) {
			Food food = FoodDao.getInstance().getFoodById(this, foodId);
			if (food.name != null && !food.name.equals("")) {
				arrayList.add(food);
			}
		}

		if (arrayList != null && arrayList.size() == 0) {
			ArrayList<Food> foods = FoodDao.getInstance().getAllFood(this);
			for (int i = 0; i < 10; i++) {
				int random = (int) (Math.random() * foods.size());
				Food food = FoodDao.getInstance().getFoodById(this,
						String.valueOf(random));
				arrayList.add(food);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_menu, menu);
		SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setIconified(false);
		searchView.setQueryHint(getString(R.string.search_hint));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				View view = SearchFoodActivity.this.getCurrentFocus();
				IBinder ibinder = view.getWindowToken();
				if (ibinder != null) {
					InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(ibinder,
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				foodName = newText.trim();
				footerView.setVisibility(View.VISIBLE);
				if (foodName == null || foodName.equals("")) {
					txtAdd.setVisibility(View.GONE);
				} else {

					txtAdd.setVisibility(View.VISIBLE);
					txtAdd.setText("添加食物  ”" + foodName + "“");

					arrayList = dao.getFoodByName(SearchFoodActivity.this,
							foodName);
					if (arrayList != null && arrayList.size() > 0) {
						txtTitle.setText("搜索结果");
					} else {
						txtTitle.setText("无结果");
					}
					lvFoods.setAdapter(new FoodAdapter(SearchFoodActivity.this,
							arrayList));
				}

				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
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
		case R.id.txt_add:
			Intent intent = new Intent(this, AddFoodEntryActivity.class);
			intent.putExtra(Constants.FOOD_NAME, foodName);
			startActivityForResult(intent, Constants.SUCCESS);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.SUCCESS:
			if (data != null) {
				Food food = (Food) data.getSerializableExtra(Constants.FOOD);
				Intent intent = getIntent();
				intent.putExtra(Constants.FOOD, food);
				setResult(RESULT_OK, intent);
				this.finish();
			}
			break;

		default:
			break;
		}
	}
	
}
