package com.squad22.fit.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeightRecordBaseHelper extends SQLiteOpenHelper {

	/**
	 * 体重记录数据库名称
	 */
	public static final String DB_NAME = "weightrecord.db";

	/**
	 * 数据版本
	 */
	private static final int NEWS_DATABASE_VERSION = 3;

	/**
	 * 用户信息表名
	 */
	public static final String PROFILE_TABLE_NAME = "profile";

	/**
	 * 食物表名
	 */
	public static final String FOOD_TABLE_NAME = "food";

	/**
	 * 运动表名
	 */
	public static final String ACTIVITY_TABLE_NAME = "activity";

	/**
	 * 运动记录表名
	 */
	public static final String MY_ACTIVITY_TABLE_NAME = "my_activity";

	/**
	 * 喝水记录表名
	 */
	public static final String MY_WATER_COUNT_TABLE_NAME = "my_waterCount";

	/**
	 * 排泄记录表名
	 */
	public static final String MY_BOWEL_COUNT_TABLE_NAME = "my_bowelCount";

	/**
	 * 食物记录表名
	 */
	public static final String MY_MEAL_TABLE_NAME = "my_meal";

	/**
	 * 食物记录详情表名
	 */
	public static final String MEAL_RECORD_DETAIL_TABLE_NAME = "meal_record_detail";

	/**
	 * 运动记录详情表名
	 */
	public static final String ACTIVITY_RECORD_DETAIL_TABLE_NAME = "activity_record_detail";

	/**
	 * 新测量记录表名
	 */
	public static final String MY_MEASUREMENT_TABLE_NAME = "my_measurement";

	/**
	 * 我的记录表名
	 */
	public static final String MY_RECORD_TABLE_NAME = "my_record";

	/**
	 * 睡眠记录表名
	 */
	public static final String MY_SLEEP = "my_sleep";

	/**
	 * 今日总结表名
	 */
	public static final String SUMMARY = "summary";

	/**
	 * 单位表名
	 */
	public static final String UNITS = "units";

	private static WeightRecordBaseHelper sSingleton = null;

	WeightRecordBaseHelper(Context context) {
		super(context, DB_NAME, null, NEWS_DATABASE_VERSION);

	}

	public static synchronized WeightRecordBaseHelper getInstance(
			Context context) {
		if (sSingleton == null) {
			sSingleton = new WeightRecordBaseHelper(context);
		}
		return sSingleton;
	}

	// 用户信息表sql
	public static final String SQL_CREATE_PROFILE_TABLE = new StringBuilder()
			.append("Create table ").append(PROFILE_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" userName text,").append(" name text, ")
			.append(" birthday DATE,").append(" height double,")
			.append(" weight double,").append(" unit text,")
			.append(" image text,").append(" sex text);").toString();

	// 食物表sql
	public static final String SQL_CREATE_FOOD_TABLE = new StringBuilder()
			.append("Create table ").append(FOOD_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" foodId text, ").append(" name text,")
			.append(" category text,").append(" qty text,")
			.append(" unit text,").append(" kcal text,")
			.append("syncId INTEGER);").toString();

	// 运动表sql
	public static final String SQL_CREATE_ACTIVITY_TABLE = new StringBuilder()
			.append("Create table ").append(ACTIVITY_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" activityId text, ").append(" name text,")
			.append(" calorie1 INTEGER,").append(" calorie2 INTEGER,")
			.append("calorie3 INTEGER,").append(" calorie4 INTEGER);")
			.toString();

	// 记录表sql
	public static final String SQL_CREATE_MY_RECORD_TABLE = new StringBuilder()
			.append("Create table ").append(MY_RECORD_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" recordId text, ").append(" createDate DATE, ")
			.append(" userId text, ").append(" type text);").toString();

	// 运动记录表sql
	public static final String SQL_CREATE_ACTIVIY_TABLE = new StringBuilder()
			.append("Create table ").append(MY_ACTIVITY_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" createDate DATE, ").append(" activityId text,")
			.append(" userId text, ").append(" image text,")
			.append(" title text,").append(" remark text,")
			.append(" like INTEGER,").append(" comment text,")
			.append(" syncId INTEGER);").toString();

	// 运动记录详情表sql
	public static final String SQL_CREATE_ACTIVIY_RECORD_DETAIL_TABLE = new StringBuilder()
			.append("Create table ").append(ACTIVITY_RECORD_DETAIL_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" recordId text, ").append(" name text,")
			.append(" unit text,").append(" time text,")
			.append(" activityId text,").append(" calorie text);").toString();

	// 食物记录表sql
	public static final String SQL_CREATE_MEAL_TABLE = new StringBuilder()
			.append("Create table ").append(MY_MEAL_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" createDate DATE, ").append(" image text,")
			.append(" userId text, ").append(" remark text,")
			.append(" title text,").append(" mealId text,")
			.append(" like INTEGER,").append(" comment text,")
			.append(" syncId INTEGER);").toString();

	// 食物记录详情表sql
	public static final String SQL_CREATE_MEAL_RECORD_DETAIL_TABLE = new StringBuilder()
			.append("Create table ").append(MEAL_RECORD_DETAIL_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" recordId text,").append(" name text,")
			.append(" portion text,").append(" Amount text,")
			.append("unit text,").append(" Category text,")
			.append(" foodId text,").append(" calorie text);").toString();

	// 喝水记录表sql
	public static final String SQL_CREATE_WATER_COUNT_TABLE = new StringBuilder()
			.append("Create table ").append(MY_WATER_COUNT_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" userId text, ").append(" syncId text,")
			.append(" waterDate text,").append(" yearMoth text);").toString();

	// 排泄记录表sql
	public static final String SQL_CREATE_BOWEL_COUNT_TABLE = new StringBuilder()
			.append("Create table ").append(MY_BOWEL_COUNT_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" userId text, ").append(" syncId text,")
			.append(" bowelDate text,").append(" yearMoth text);").toString();

	// 新测量记录表sql
	public static final String SQL_CREATE_MEASUREMENT_TABLE = new StringBuilder()
			.append("Create table ").append(MY_MEASUREMENT_TABLE_NAME)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" userId text, ").append(" measurementId text,")
			.append(" weight DOUBLE,").append(" bodyFat DOUBLE,")
			.append(" leanMusde DOUBLE,").append(" bodyAge DOUBLE,")
			.append(" visceralFat DOUBLE,").append(" BMI DOUBLE,")
			.append(" BMR DOUBLE,").append(" arm DOUBLE,")
			.append(" waist DOUBLE,").append(" hip DOUBLE,")
			.append(" abd DOUBLE,").append(" thigh DOUBLE,")
			.append(" calf DOUBLE,").append(" image1 text,")
			.append(" image2 text,").append(" image3 text,")
			.append(" createDate DATE,").append(" remark text,")
			.append(" syncId INTEGER);").toString();

	// 睡眠记录表sql
	public static final String SQL_CREATE_SLEEP_TABLE = new StringBuilder()
			.append("Create table ").append(MY_SLEEP)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" userId text, ").append(" sleepId text,")
			.append(" sleepStart text,").append(" sleepEnd text,")
			.append(" createDate text,").append(" syncId INTEGER);").toString();

	public static final String SQL_CREATE_SUMMARY_TABLE = new StringBuilder()
			.append("Create table ").append(SUMMARY)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" summaryId INTEGER,").append(" quote text);").toString();

	public static final String SQL_CREATE_UNIT_TABLE = new StringBuilder()
			.append("Create table ").append(UNITS)
			.append(" ( _id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(" unitId INTEGER,").append(" unit text);").toString();

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(SQL_CREATE_PROFILE_TABLE);
		db.execSQL(SQL_CREATE_FOOD_TABLE);
		db.execSQL(SQL_CREATE_ACTIVIY_TABLE);
		db.execSQL(SQL_CREATE_ACTIVIY_RECORD_DETAIL_TABLE);
		db.execSQL(SQL_CREATE_MEAL_TABLE);
		db.execSQL(SQL_CREATE_MEAL_RECORD_DETAIL_TABLE);
		db.execSQL(SQL_CREATE_WATER_COUNT_TABLE);
		db.execSQL(SQL_CREATE_BOWEL_COUNT_TABLE);
		db.execSQL(SQL_CREATE_MEASUREMENT_TABLE);
		db.execSQL(SQL_CREATE_SLEEP_TABLE);
		db.execSQL(SQL_CREATE_MY_RECORD_TABLE);
		db.execSQL(SQL_CREATE_ACTIVITY_TABLE);
		db.execSQL(SQL_CREATE_SUMMARY_TABLE);
		db.execSQL(SQL_CREATE_UNIT_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + FOOD_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MY_ACTIVITY_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MY_MEAL_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MY_MEASUREMENT_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MY_WATER_COUNT_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MY_BOWEL_COUNT_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MY_SLEEP);
		db.execSQL("DROP TABLE IF EXISTS " + MEAL_RECORD_DETAIL_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_RECORD_DETAIL_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MY_RECORD_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SUMMARY);
		db.execSQL("DROP TABLE IF EXISTS " + UNITS);
		onCreate(db);
	}

}
