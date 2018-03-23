/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.events;

import cn.lrapps.models.AppInfo;

import java.util.List;

/**
 * Created by libit on 16/7/3.
 */
public class AppListEvent
{
	public static final String GET_RUNNING_APP = "get_running_app";
	public static final String GET_COMMON_USE_APP = "get_common_use_app";
	private String type;
	private List<AppInfo> appInfoList;

	public AppListEvent()
	{
	}

	public AppListEvent(String type, List<AppInfo> appInfoList)
	{
		this.type = type;
		this.appInfoList = appInfoList;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public List<AppInfo> getAppInfoList()
	{
		return appInfoList;
	}

	public void setAppInfoList(List<AppInfo> appInfoList)
	{
		this.appInfoList = appInfoList;
	}
}
