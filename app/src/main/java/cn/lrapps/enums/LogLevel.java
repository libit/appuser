/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * 日志记录级别，级别越高记录的日志越多
 * Created by libit on 16/7/12.
 */
public enum LogLevel
{
	LEVEL_0(0), LEVEL_1(1), LEVEL_2(2), LEVEL_3(3), LEVEL_4(4), LEVEL_5(5);
	private int level;

	LogLevel(int level)
	{
		this.level = level;
	}

	public int getLevel()
	{
		return level;
	}
}
