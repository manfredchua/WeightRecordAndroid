package com.squad22.fit.entity;

import java.io.Serializable;

public class WaterCount implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String id;
	
	public String userId;
	
	// 同步
	public int syncId;

	// 当前排泄时间
	public String waterDate;

	// 年月日用于搜索
	public String yearMonth;
}
