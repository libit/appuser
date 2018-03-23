/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * App类型
 * Created by libit on 16/7/12.
 */
public enum AppType
{
	SYSTEM(1), USER(2);
	private int type;

	AppType(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}
}
