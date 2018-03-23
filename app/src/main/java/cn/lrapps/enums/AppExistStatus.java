/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * App存在状态
 * Created by libit on 16/7/12.
 */
public enum AppExistStatus
{
	EXIST(1), NOT_EXIST(2);
	private int status;

	AppExistStatus(int status)
	{
		this.status = status;
	}

	public int getStatus()
	{
		return status;
	}

	public static boolean isExist(int status)
	{
		return status == EXIST.getStatus();
	}

	public static int getAppStatus(boolean isExist)
	{
		if (isExist)
		{
			return EXIST.getStatus();
		}
		else
		{
			return NOT_EXIST.getStatus();
		}
	}
}
