/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * App显示状态
 * Created by libit on 16/7/12.
 */
public enum AppShowStatus
{
	HIDE(1), SHOW(2);
	private int status;

	AppShowStatus(int status)
	{
		this.status = status;
	}

	public int getStatus()
	{
		return status;
	}

	public static boolean isHide(int status)
	{
		return status == HIDE.getStatus();
	}

	public static int getHideStatus(boolean isHide)
	{
		if (isHide)
		{
			return HIDE.getStatus();
		}
		else
		{
			return SHOW.getStatus();
		}
	}
}
