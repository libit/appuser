/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * 状态
 * Created by libit on 16/7/12.
 */
public enum StatusType
{
	ENABLE(0), DISABLE(1), START(2);
	private int status;

	StatusType(int status)
	{
		this.status = status;
	}

	public int getStatus()
	{
		return status;
	}

	public static boolean isEnabled(int status)
	{
		return status == DISABLE.getStatus();
	}

	public static int getStatusType(boolean enable)
	{
		if (enable)
		{
			return ENABLE.getStatus();
		}
		else
		{
			return DISABLE.getStatus();
		}
	}
}
