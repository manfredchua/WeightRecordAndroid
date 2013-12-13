package com.squad22.fit.entity;

import java.io.Serializable;

public class MyRecords implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String id;
	
	public String userId;
	
	//记录Id
	public String recordId;
	
	//记录时间
	public String createDate;
	
	//记录类型(1代表饮食,2代表运动,3代表测量,4代表睡眠,5代表喝水,6代表排泄,7今日总结)
	public int type;

}
