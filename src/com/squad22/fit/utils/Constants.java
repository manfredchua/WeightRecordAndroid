package com.squad22.fit.utils;

import java.io.File;

import android.net.Uri;
import android.os.Environment;

public class Constants {

	/**
	 * 正式版
	 */
	 public static final String APP_ID = "21f7401d";
	 public static final String APP_KEY = "492ea35c20e977d17b27a348ba11f562";

	/**
	 * 测试版
	 */
//	public static final String APP_ID = "0d21af14";
//	public static final String APP_KEY = "31c6f881c05d27f7d2b41d14d486f645";

	// 成功响应结果
	public static final int SUCCESS = 3;

	// 失败响应结果
	public static final int FAILURE = 4;

	// 登录的信息
	public static final String KIIUSER = "KiiUser";
	public static final String USERNAME = "userName";
	public static final String TOKEN = "Token";
	public static final String LOGIN_STATUS = "loginStatus";
	
	public static final String FullName = "fullName";
	
	// 性别
	public static final String Gender = "gender";

	// 生日
	public static final String Birthday = "birthday";

	// 身高
	public static final String Height = "height";

	// 是否是会员
	public static final String IsMember = "isMember";

	// 头像
	public static final String Avatar = "avatar";
	
	// 头像Uri
	public static final String AvatarUrl = "imageUrl";

	// 到期日会员
	public static final String ExpiryDateMember = "expiryDateMember";
	
	// 免费到期日会员
	public static final String FreeDateMember = "freeDateMember";

	// 当前体重
	public static final String CurrentWeight = "startWeight";
	
	// 当前体重
	public static final String BACKUPSTATE = "backupState";

	// 理想体重
	public static final String IdealWeight = "idealWeight";

	// 身体体脂率
	public static final String IdealBodyFat = "idealBodyFat";

	// 肌肉率
	public static final String IdealLeanMuscle = "idealLeanMuscle";

	// 内脂肪指数
	public static final String IdealVisceralFat = "idealVisceralFat";

	// 身体年龄
	public static final String IdealBodyAge = "idealBodyAge";

	// BMI
	public static final String IdealBMI = "idealBMI";

	// 基础代谢
	public static final String IdealBMR = "idealBMR";

	// 热量需求
	public static final String IdealCalorieNeeds = "idealCalorieNeeds";

	// 食物记录
	public static final String MyMeal = "my_meal";

	// 食物详情
	public static final String mealFood = "my_mealFood";

	// 食物标题
	public static final String mealTitle = "my_mealTitle";

	// 备注
	public static final String mealRemark = "my_mealRemark";

	// 图片
	public static final String mealImage = "my_mealImage";
	
	// 图片的uri
	public static final String mealImageUri = "my_imageUri";

	// 赞
	public static final String mealLike = "my_mealLike";

	// 评论
	public static final String mealComment = "my_mealComment";

	// 同步ID
	public static final String mealSyncID = "my_mealSyncID";

	// 记录时间
	public static final String mealRecordDate = "my_mealRecordDate";

	// 食物记录ID
	public static final String mealID = "my_mealID";

	// 运动记录
	public static final String MyActivity = "my_activity";

	// 运动详情
	public static final String activityExercise = "my_activityExercise";

	// 运动标题
	public static final String activityTitle = "my_activityTitle";

	// 备注
	public static final String activityRemark = "my_activityRemark";

	// 图片
	public static final String activityImage = "my_activityImage";

	// 赞
	public static final String activityLike = "my_activityLike";

	// 评论
	public static final String activityComment = "my_activityComment";

	// 同步ID
	public static final String activitySyncID = "my_activitySyncID";

	// 记录时间
	public static final String activityRecordDate = "my_activityRecordDate";

	// 运动记录ID
	public static final String activityID = "my_activityID";
	
	
	//测量记录
	public static final String  MyMeasurement = "my_measurement";
	
	public static final String measurementID = "my_measurementID";
	
	//体重
	public static final String  measurementWeight = "my_measurementWeight";
	
	//体脂率
	public static final String  measurementBodyFat = "my_measurementBodyFat";
	
	//肌肉率
	public static final String  measurementLeanMuscle = "my_measurementLeanMuscle";
	
	//身体年龄
	public static final String  measurementBodyAge = "my_measurementBodyAge";
	
	//内脂肪指数
	public static final String  measurementVisceralFat = "my_measurementVisceralFat";
	
	//BMI
	public static final String  measurementBMI = "my_measurementBMI";
	
	//基础代谢
	public static final String  measurementBMR = "my_measurementBMR";
	
	//臂围
	public static final String  measurementArm = "my_measurementArm";
	
	//腰围
	public static final String  measurementWaist = "my_measurementWaist";
	
	//腹围
	public static final String  measurementHip = "my_measurementHip";
	
	//臀围
	public static final String  measurementAbd = "my_measurementAbd";
	
	//大腿围
	public static final String  measurementThigh = "my_measurementThigh";
	
	//小腿围
	public static final String  measurementCalf = "my_measurementCalf";
	
	//同步ID
	public static final String  measurementSyncID = "my_measurementSyncID";
	
	//测量图1
	public static final String  measurementImage1 = "my_measurementImage1";
	
	// 图片的uri
	public static final String imageUri1 = "my_imageUri1";
	
	//测量图2
	public static final String  measurementImage2 = "my_measurementImage2";
	
	// 图片的uri
	public static final String imageUri2 = "my_imageUri2";
	
	//测量图3
	public static final String  measurementImage3 = "my_measurementImage3";
	
	// 图片的uri
	public static final String imageUri3 = "my_imageUri3";
	
	//测量报告日期
	public static final String  measurementReportDate = "my_measurementReportDate";
	
	//测量备注
	public static final String  measurementRemark = "my_measurementRemark";
	
	//睡眠记录
	public static final String MySleep = "my_sleep";
	
	public static final String sleepID = "my_sleepID";
	
	public static final String sleepStart = "my_sleepStart";
	
	public static final String sleepEnd = "my_sleepEnd";
	
	public static final String sleepSyncID = "my_sleepSyncID";
	
	public static final String sleepReportDate = "my_sleepReportDate";
	
	//喝水和排泄记录
	public static final String MyDailycount = "my_dailycount";
	
	public static final String dailycountWater = "my_dailycountWater";
	
	public static final String dailycountBowel = "my_dailycountBowel";
	
	public static final String dailycountRecordDate = "my_dailycountRecordDate";
	
	public static final String waterCreateDate = "waterCreateDate";
	
	public static final String waterSyncID = "waterSyncID";
	
	public static final String bowelCreateDate = "bowelCreateDate";
	
	public static final String bowelSyncID = "bowelSyncID";
	

	// 记录是否自动备份
	public static final String BACKUP_RECORDS = "BackupRecords";
	public static final String IS_RECORD = "IsRecord";
	public static final String WIFI = "Wifi";
	public static final String WIFI_3G = "Wifi3G";

	// 单位换算
	public static final String UNITS = "Units";
	public static final String UNIT = "Unit";
	public static final String METRIC = "Metric";
	public static final String IMPERIAL = "Imperial";

	// 食物
	public static final String FOOD_NAME = "FoodName";
	public static final String FOOD = "Food";
	public static final String ID_FOOD = "idFood";
	public static final String FOOD_ID = "foodID";
	public static final String NAME = "name";
	public static final String CATEGORY = "category";
	public static final String QTY = "qty";
	public static final String FOOD_UNIT = "unit";
	public static final String KCAL = "kcal";
	public static final String PORTION = "portion";
	public static final String PORTION_UNIT = "portionUnit";
	public static final String ISEDIT = "isEdit";

	// 运动
	public static final String EXERCISE_ID = "exerciseID";
	public static final String TIME = "time";
	public static final String CALORIE = "calorie";

	public static final String CURRENT_DATE = "currentDate";
	public static final String RECORD_ID = "recordId";
	public static final String SYNC_DATE = "syncDate";

	public static final String MEAL = "meal";
	public static final String ACTIVITY = "activity";
	public static final String MEASUREMENT = "measurement";
	public static final String SLEEP = "sleep";
	public static final String WATER = "water";
	public static final String BOWEL = "bowel";
	public static final String SEARCH = "search";

	public static final double kcal1 = 58.9;
	public static final double kcal2 = 70.3;
	public static final double kcal3 = 81.6;
	public static final double kcal4 = 92.9;

	public static final String[] sleep = { "睡觉", "取消" };
	public static final String[] water = { "喝水  + 1", "排泄  + 1", "取消" };
	public static final String[] items = { "编辑", "账户", "设置", "取消" };
	public static final String[] foodType = { "主食-五谷类", "肉类蛋白质", "非肉类蛋白质",
			"蔬菜水果类", "保健品", "饮料类", "其他" };
	public static final String[] units = { "斤", "克", "毫升", "升", "茶匙", "汤匙",
			"杯", "品脱", "个", "粒", "盘", "碟", "根", "盒", "块", "片", "公斤", "英镑", "镑",
			"盎司", "份", "条", "瓶", "只", "碗", "件", "包" };

	public static final String[] number = { "0", "1", "2", "3", "4", "5", "6",
			"7", "8", "9" };

	// 公斤转换磅
	public static final double lb = 2.2046226218488;

	// 米转换英尺
	public static final double ft = 3.2808398950131;

	// 磅转换公斤
	public static final double kg = 0.45359237;

	// 英尺转换米
	public static final double m = 0.3048;

	// 照相之后存放的位置
	public static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/");
	public static final int TAKE_A_PICTURE = 0;
	// 图库
	public static final int GALLERY = 1;
	public static final int PHOTO_PICKED_WITH_DATA = 2;
	public static final String PHOTO_PATH = Environment
			.getDownloadCacheDirectory() + "/error/";

	public static final String DOWNLOAD_PHOTO_PATH = Environment
			.getExternalStorageDirectory() + "/download/";
	public static String[] Photo = { "拍照", "图库" };
	public static final String RECORD_PHOTO = "record_photo";
	public static final String RECORD_PHOTO_LIST = "record_photo_list";

	// provider URI
	public static Uri ProfileUri = Uri
			.parse("content://com.squad22.fit.provider.profile");
	public static Uri FoodUri = Uri
			.parse("content://com.squad22.fit.provider.food");
	public static Uri ActivityUri = Uri
			.parse("content://com.squad22.fit.provider.activity");
	public static Uri MealUri = Uri
			.parse("content://com.squad22.fit.provider.meal");
	public static Uri MyActivityUri = Uri
			.parse("content://com.squad22.fit.provider.myactivity");
	public static Uri MealDetailUri = Uri
			.parse("content://com.squad22.fit.provider.mealdetail");
	public static Uri ActivityDetailUri = Uri
			.parse("content://com.squad22.fit.provider.activitydetail");
	public static Uri WaterCountUri = Uri
			.parse("content://com.squad22.fit.provider.watercount");
	public static Uri BowelCountUri = Uri
			.parse("content://com.squad22.fit.provider.bowelcount");
	public static Uri MeasurementUri = Uri
			.parse("content://com.squad22.fit.provider.measurement");
	public static Uri SleepUri = Uri
			.parse("content://com.squad22.fit.provider.sleep");
	public static Uri MyRecordUri = Uri
			.parse("content://com.squad22.fit.provider.myrecords");
	public static Uri SummaryUri = Uri
			.parse("content://com.squad22.fit.provider.summary");
	public static Uri UnitsUri = Uri
			.parse("content://com.squad22.fit.provider.units");
}
