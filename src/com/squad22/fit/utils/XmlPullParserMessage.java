package com.squad22.fit.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Xml;

import com.squad22.fit.R;
import com.squad22.fit.entity.ActivityEntity;
import com.squad22.fit.entity.Food;
import com.squad22.fit.entity.Summary;
import com.squad22.fit.entity.Units;

public class XmlPullParserMessage {

	public static List<Food> getFoods(InputStream inputStream) throws Exception {
		List<Food> foods = null;
		Food food = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");

		int event = parser.getEventType();// 产生第一个事件
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
				foods = new ArrayList<Food>();// 初始化集合
				break;
			case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
				if ("item".equals(parser.getName())) {// 判断开始标签元素是否是item
					food = new Food();
				}
				if (food != null) {
					if ("name".equals(parser.getName())) {// 判断开始标签元素是否是name
						food.name = parser.nextText();
					} else if ("quantity".equals(parser.getName())) {// 判断开始标签元素是否是price
						food.qty = parser.nextText();
					} else if ("unit".equals(parser.getName())) {
						food.unit = parser.nextText();
					} else if ("calorie".equals(parser.getName())) {
						food.kcal = parser.nextText();
					} else if ("tag".equals(parser.getName())) {
						food.category = parser.nextText();
					} else if ("id".equals(parser.getName())) {
						food.foodId = parser.nextText();
					}
				}
				break;
			case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
				if ("item".equals(parser.getName())) {// 判断结束标签元素是否是food
					food.syncId = 4;
					foods.add(food);// 将food添加到foods集合
					food = null;
				}
				break;
			}
			event = parser.next();// 进入下一个元素并触发相应事件
		}// end while
		return foods;
	}

	public static List<ActivityEntity> getActivityEntity(InputStream inputStream)
			throws Exception {
		List<ActivityEntity> list = null;
		ActivityEntity entity = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");

		int event = parser.getEventType();// 产生第一个事件
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
				list = new ArrayList<ActivityEntity>();// 初始化集合
				break;
			case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
				if ("item".equals(parser.getName())) {// 判断开始标签元素是否是item
					entity = new ActivityEntity();
				}
				if (entity != null) {
					if ("name".equals(parser.getName())) {// 判断开始标签元素是否是name
						entity.name = parser.nextText();
					} else if ("calorie1".equals(parser.getName())) {
						entity.calorie1 = Integer.parseInt(parser.nextText());
					} else if ("calorie2".equals(parser.getName())) {
						entity.calorie2 = Integer.parseInt(parser.nextText());
					} else if ("calorie3".equals(parser.getName())) {
						entity.calorie3 = Integer.parseInt(parser.nextText());
					} else if ("calorie4".equals(parser.getName())) {
						entity.calorie4 = Integer.parseInt(parser.nextText());
					} else if ("id".equals(parser.getName())) {
						entity.activityId = parser.nextText();
					}
				}
				break;
			case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
				if ("item".equals(parser.getName())) {// 判断结束标签元素是否是food
					list.add(entity);// 将food添加到foods集合
					entity = null;
				}
				break;
			}
			event = parser.next();// 进入下一个元素并触发相应事件
		}// end while
		return list;
	}

	public static List<Summary> getSummary(InputStream inputStream)
			throws Exception {
		List<Summary> list = null;
		Summary entity = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");

		int event = parser.getEventType();// 产生第一个事件
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
				list = new ArrayList<Summary>();// 初始化集合
				break;
			case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
				if ("item".equals(parser.getName())) {// 判断开始标签元素是否是item
					entity = new Summary();
				}
				if (entity != null) {
					if ("quote".equals(parser.getName())) {// 判断开始标签元素是否是name
						entity.quote = parser.nextText();
					} else if ("id".equals(parser.getName())) {
						entity.summaryId = Integer.parseInt(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
				if ("item".equals(parser.getName())) {// 判断结束标签元素是否是food
					list.add(entity);// 将food添加到foods集合
					entity = null;
				}
				break;
			}
			event = parser.next();// 进入下一个元素并触发相应事件
		}// end while
		return list;
	}

	public static ArrayList<HashMap<String, String>> getYearsXmlResource(
			Context context) {
		Resources res = context.getResources();
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		try {
			XmlResourceParser xrp = (XmlResourceParser) res.getXml(R.xml.years);

			// 判断是否到了文件的结尾
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				// 文件的内容的起始标签开始，注意这里的起始标签是test.xml文件里面<resources>标签下面的第一个标签
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String tagname = xrp.getName();
					if (tagname.endsWith("year")) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(xrp.getAttributeValue(0),
								xrp.getAttributeValue(1));
						arrayList.add(map);
					}
				}
				// 下面的两个else if什么作用呢？
				else if (xrp.getEventType() == XmlResourceParser.END_TAG) {
				} else if (xrp.getEventType() == XmlResourceParser.TEXT) {
				}
				xrp.next();
			}
		} catch (XmlPullParserException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}
		return arrayList;
	}

	public static List<Units> getUnit(InputStream inputStream) throws Exception {
		List<Units> list = null;
		Units entity = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");

		int event = parser.getEventType();// 产生第一个事件
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
				list = new ArrayList<Units>();// 初始化集合
				break;
			case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
				if ("item".equals(parser.getName())) {// 判断开始标签元素是否是item
					entity = new Units();
				}
				if (entity != null) {
					if ("unit".equals(parser.getName())) {// 判断开始标签元素是否是name
						entity.unit = parser.nextText();
					} else if ("id".equals(parser.getName())) {
						entity.unitId = parser.nextText();
					}
				}
				break;
			case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
				if ("item".equals(parser.getName())) {// 判断结束标签元素是否是food
					list.add(entity);// 将food添加到foods集合
					entity = null;
				}
				break;
			}
			event = parser.next();// 进入下一个元素并触发相应事件
		}// end while
		return list;
	}

	public static List<HashMap<String, String>> getKiiError(
			InputStream inputStream) throws Exception {
		List<HashMap<String, String>> list = null;
		HashMap<String, String> map = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");

		// 产生第一个事件
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			// 判断当前事件是否是文档开始事件
			case XmlPullParser.START_DOCUMENT:
				// 初始化集合
				list = new ArrayList<HashMap<String, String>>();
				break;
			// 判断当前事件是否是标签元素开始事件
			case XmlPullParser.START_TAG:
				// 判断开始标签元素是否是item
				if ("item".equals(parser.getName())) {
					map = new HashMap<String, String>();
				}
				if (map != null) {
					// 判断开始标签元素是否是code
					if ("code".equals(parser.getName())) {
						map.put("code", parser.nextText());
					} else if ("msg".equals(parser.getName())) {
						map.put("msg", parser.nextText());
					}
				}
				break;
			// 判断当前事件是否是标签元素结束事件
			case XmlPullParser.END_TAG:
				// 判断结束标签元素是否是item
				if ("item".equals(parser.getName())) {
					// 将map添加到list集合
					list.add(map);
					map = null;
				}
				break;
			}
			// 进入下一个元素并触发相应事件
			event = parser.next();
		}
		return list;
	}
}
