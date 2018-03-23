/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by libit on 16/4/12.
 */
public class ReturnListInfo extends ReturnInfo
{
	@SerializedName("count")
	protected Long count;
	@SerializedName("totalCount")
	protected Long totalCount;

	public ReturnListInfo()
	{
		super();
	}

	public ReturnListInfo(Integer code, String msg)
	{
		super(code, msg);
	}

	public ReturnListInfo(ReturnInfo returnInfo)
	{
		super(returnInfo.getCode(), returnInfo.getMsg());
		this.count = 0L;
		this.totalCount = 0L;
	}

	public ReturnListInfo(ReturnInfo returnInfo, Long count, Long totalCount)
	{
		super(returnInfo.getCode(), returnInfo.getMsg());
		this.count = count;
		this.totalCount = totalCount;
	}

	public ReturnListInfo(Integer code, String msg, Long count, Long totalCount)
	{
		super(code, msg);
		this.count = count;
		this.totalCount = totalCount;
	}

	public Long getCount()
	{
		return count;
	}

	public void setCount(Long count)
	{
		this.count = count;
	}

	public Long getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(Long totalCount)
	{
		this.totalCount = totalCount;
	}
}
