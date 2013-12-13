package com.squad22.fit.entity;

import java.io.Serializable;

public class Measurement implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String id;
	
	public String userId;
	
	public String measurementId;
	
	//创建时间
	public String createDate;
	
	//备注
	public String remark;
	//体重
	public double weight;
	
	//体脂率
	public double bodyFat;
	
	//肌肉率
	public double leanMusde;
	
	//身体年龄
	public double bodyAge;
	
	//内脏脂肪
	public double visceralFat;
	
	//身体质量指数
	public double BMI;
	
	//基础代谢
	public double BMR;
	
	//胳膊
	public double arm;
	
	//腰
	public double waist;
	
	//臀部
	public double hip;
	
	//腹部
	public double abd;
	
	//大腿
	public double thigh;
	
	//小腿
	public double calf;
	
	//测量体重图片1
	public String image1;
	
	//测量体重图片2
	public String image2;
	
	//测量体重图片3
	public String image3;
	
	//同步Id
	public int syncId;
}
