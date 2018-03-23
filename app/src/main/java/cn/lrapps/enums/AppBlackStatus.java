/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * App显示状态
 * Created by libit on 16/7/12.
 */
public enum AppBlackStatus
{
	BLACK(1), WHITE(2);
	private int status;

	AppBlackStatus(int status)
	{
		this.status = status;
	}

	public int getStatus()
	{
		return status;
	}

	public static boolean isBlack(int status)
	{
		return status == BLACK.getStatus();
	}

	public static int getAppStatus(boolean isBlack)
	{
		if (isBlack)
		{
			return BLACK.getStatus();
		}
		else
		{
			return WHITE.getStatus();
		}
	}
}
