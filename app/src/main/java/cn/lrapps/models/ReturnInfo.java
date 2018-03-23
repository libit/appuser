/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.models;

import com.google.gson.annotations.SerializedName;
import cn.lrapps.utils.GsonTools;

public class ReturnInfo
{
	@SerializedName("code")
	private Integer code;
	@SerializedName("msg")
	private String msg;

	public ReturnInfo()
	{
		super();
	}

	public ReturnInfo(Integer code, String msg)
	{
		super();
		this.code = code;
		this.msg = msg;
	}

	public static boolean isSuccess(ReturnInfo info)
	{
		return (info != null && info.getCode() != null && info.getCode() == 0);
	}

	public Integer getCode()
	{
		return code;
	}

	public void setCode(Integer code)
	{
		this.code = code;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	@Override
	public String toString()
	{
		return GsonTools.toJson(this);
	}
}
