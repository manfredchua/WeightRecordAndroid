package com.squad22.fit.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.squad22.fit.R;
import com.squad22.fit.adapter.ExerciseAdapter;
import com.squad22.fit.dao.ActivityDao;
import com.squad22.fit.dao.ActivityDetailDao;
import com.squad22.fit.dao.MyActivityDao;
import com.squad22.fit.entity.ActivityEntity;
import com.squad22.fit.utils.Constants;

public class SearchExerciseActivity extends Activity {

	ActionBar actionBar;
	ViewPager pager;
	ListView lvExercise;
	TextView txtTitle;
	MyActivityDao dao = MyActivityDao.getInstance();
	ArrayList<ActivityEntity> activityList = new ArrayList<ActivityEntity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);
		setContentView(R.layout.search_exercise_layout);

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");

		lvExercise = (ListView) findViewById(R.id.lv_exercise);

		View headerView = getLayoutInflater().inflate(R.layout.header_layout,
				null);
		txtTitle = (TextView) headerView.findViewById(R.id.txt_title);
		txtTitle.setText("最近运动");
		lvExercise.addHeaderView(headerView, null, false);

		getActivity();

		lvExercise.setAdapter(new ExerciseAdapter(this, activityList));
		lvExercise
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = getIntent();
						intent.putExtra(Constants.ACTIVITY,
								activityList.get(position - 1));
						setResult(RESULT_OK, intent);
						SearchExerciseActivity.this.finish();
					}
				});
	}

	private void getActivity() {

		activityList = new ArrayList<ActivityEntity>();
		ArrayList<String> activityIds = ActivityDetailDao.getInstance()
				.getActivityId(this);
		for (String activityId : activityIds) {
			ActivityEntity entity = ActivityDao.getInstance().getExerciseById(
					this, activityId);
			activityList.add(entity);
		}

		if (activityList != null && activityList.size() == 0) {
			ArrayList<ActivityEntity> arrayList = ActivityDao.getInstance()
					.getAllExercise(this);
			for (int i = 0; i < 10; i++) {
				int random = (int) (Math.random() * arrayList.size());
				ActivityEntity entity = ActivityDao.getInstance()
						.getExerciseById(this, String.valueOf(random));
				activityList.add(entity);
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
		searchView.setQueryHint(getString(R.string.activity_search_hint));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				View view = SearchExerciseActivity.this.getCurrentFocus();
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
				txtTitle.setText("搜索结果");

				activityList = ActivityDao.getInstance().getExerciseByName(
						SearchExerciseActivity.this, newText.trim());
				ExerciseAdapter adapter = new ExerciseAdapter(
						SearchExerciseActivity.this, activityList);
				lvExercise.setAdapter(adapter);

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

}
