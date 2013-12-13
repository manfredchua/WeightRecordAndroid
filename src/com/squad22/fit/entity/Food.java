package com.squad22.fit.entity;

import java.io.Serializable;

public class Food  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//食物Id
	public String foodId;
	
	//食物名称
	public String name;
	
	//食物类型
	public String category;
	
	//食物数量
	public String qty;
	
	public String unit;
	
	//食物卡路里
	public String kcal;
	
	public String portion;
	
	public int syncId;
}
