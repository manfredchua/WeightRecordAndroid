package com.squad22.fit.entity;

import java.io.Serializable;

public class Records implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public String currentDate;
	
	//小时
	public int hourStr;
	
	//分钟
	public int minuteStr;
	
	// 高度
	public int height;
	
	//标题
	public String title;
	
	//备注
	public String remark;
	
	//记录Id
	public String recordId;
	
	public boolean isPhoto;
	
	//记录类型(1代表饮食,2代表运动,3代表测量,4代表睡眠,5代表喝水,6代表排泄)
	public int type;

}
