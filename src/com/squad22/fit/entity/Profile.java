package com.squad22.fit.entity;

public class Profile {

	public String id;
	
	public String userName;
	
	//姓名
	public String name;
	
	//生日
	public String birthday;
	
	//身高
	public double height;
	
	//性别
	public String sex;
	
	//体重
	public double weight;
	
	//头像
	public String image;
	
	public String unit;
	
	//备份状态 0 手动备份, 1  自动备份，使用网络为wifi,2  自动备份，使用网络为wifi+3g
	public int backupState;
	
	public String freeDateMember;
}
