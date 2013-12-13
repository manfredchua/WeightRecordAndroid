package com.squad22.fit.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Meal implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String id;
	
	public String mealId;
	
	public String userId;

	//标题
	public String title;
	
	// 创建食物记录时间
	public String createDate;
	
	// 食物图片
	public String image;

	// 食物感想
	public String remark;

	// 食物评论
	public String comment;

	// 同步id
	public int syncId;

	// 喜欢或者是赞
	public int like;
	
	public ArrayList<MealDetail> mealDetail;

}
