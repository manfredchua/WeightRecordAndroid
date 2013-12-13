package com.squad22.fit.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Exercise implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String id;
	
	public String userId;
	
	//创建记录时间
	public String title;
	
	//运动感想
	public String remark;
	
	//创建记录时间
	public String createDate;
	
	//运动id
	public String activityId;
	
	//运动图片
	public String image;
	
	//运动评论
	public String comment;
	
	//同步id
	public int syncId;
	
	//喜欢或者是赞
	public int like;
	
	public ArrayList<ExerciseDetail> exerciseDetail;

}
