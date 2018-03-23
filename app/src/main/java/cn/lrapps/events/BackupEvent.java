/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.events;

/**
 * Created by libit on 16/7/3.
 */
public class BackupEvent
{
	public static final int BACKUP = 1001;
	public static final int RESTORE = 1002;
	private int type;
	private String msg;

	public BackupEvent(int type, String msg)
	{
		this.type = type;
		this.msg = msg;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
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
}
