/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.events;

import java.util.List;

/**
 * Created by libit on 16/7/3.
 */
public class AppEvent
{
	//	public static final String APP_STATUS_ENABLED = "app_status_enabled";
	//	public static final String APP_STATUS_DISABLED = "app_status_disabled";
	public static final String APP_STATUS_CHANGED = "app_status_changed";
	public static final String GET_RUNNING_APP = "get_running_app";
	public static final String GET_ENABLED_BLACK_APP = "get_enabled_black_app";
	public static final String GET_SYSTEM_APP = "get_system_app";
	public static final String CLOSE_BLACK_APP = "close_black_app";
	public static final String CLEAR_AND_ENABLE_BLACK_APP = "clear_and_enable_black_app";
	public static final String CLEAR_HIDE_APP = "clear_hide_app";
	public static final String CLEAR_APP = "clear_app";
	private String type;
	private String msg;
	private List<String> packageNameList;

	public AppEvent()
	{
	}

	public AppEvent(String type, List<String> packageNameList)
	{
		this.type = type;
		this.packageNameList = packageNameList;
	}

	public AppEvent(String type, String msg, List<String> packageNameList)
	{
		this.type = type;
		this.msg = msg;
		this.packageNameList = packageNameList;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public List<String> getPackageNameList()
	{
		return packageNameList;
	}

	public void setPackageNameList(List<String> packageNameList)
	{
		this.packageNameList = packageNameList;
	}
}
