/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * Created by libit on 2017/10/14.
 */
public enum UserEvent
{
	CHANGE_HEADER("change_header"), CHANGED_HEADER("changed_header"), LOGOUT("logout");
	private String event;
	private String data;

	UserEvent(String event)
	{
		this.event = event;
	}

	public String getEvent()
	{
		return event;
	}

	public void setEvent(String event)
	{
		this.event = event;
	}

	public String getData()
	{
		return data;
	}

	public UserEvent setData(String data)
	{
		this.data = data;
		return this;
	}
}
