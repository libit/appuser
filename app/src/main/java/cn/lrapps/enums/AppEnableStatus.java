/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * App启用状态
 * Created by libit on 16/7/12.
 */
public enum AppEnableStatus
{
	ENABLED(1), DISABLED(2);//STATUS_ALL(0),
	private int status;

	AppEnableStatus(int status)
	{
		this.status = status;
	}

	public int getStatus()
	{
		return status;
	}

	public static boolean isEnabled(int status)
	{
		return status == ENABLED.getStatus();
	}

	public static int getAppStatus(boolean enabled)
	{
		if (enabled)
		{
			return ENABLED.getStatus();
		}
		else
		{
			return DISABLED.getStatus();
		}
	}
}
