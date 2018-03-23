/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.models;

/**
 * Created by libit on 2017/9/20.
 */
public class FuncInfo
{
	private Integer resId;
	private String name;

	public FuncInfo(Integer resId, String name)
	{
		this.resId = resId;
		this.name = name;
	}

	public Integer getResId()
	{
		return resId;
	}

	public void setResId(Integer resId)
	{
		this.resId = resId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
