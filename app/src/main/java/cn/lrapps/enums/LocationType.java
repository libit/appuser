/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * 日志记录级别，级别越高记录的日志越多
 * Created by libit on 16/7/12.
 */
public enum LocationType
{
	NONE(0), LEFT(1), RIGHT(2), BOTTOM(3), TOP(4);
	private int type;

	LocationType(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}
}
