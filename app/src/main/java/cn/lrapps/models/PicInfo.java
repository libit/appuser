/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by libit on 16/4/13.
 */
public class PicInfo
{
	@SerializedName("id")
	private Integer id;
	@SerializedName("picId")
	private String picId;
	@SerializedName("sortId")
	private String sortId;
	@SerializedName("picUrl")
	private String picUrl;
	@SerializedName("picSizeInfo")
	private String picSizeInfo;

	public PicInfo()
	{
	}

	public PicInfo(Integer id, String picId, String sortId, String picUrl, String picSizeInfo)
	{
		this.id = id;
		this.picId = picId;
		this.sortId = sortId;
		this.picUrl = picUrl;
		this.picSizeInfo = picSizeInfo;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getPicId()
	{
		return picId;
	}

	public void setPicId(String picId)
	{
		this.picId = picId;
	}

	public String getSortId()
	{
		return sortId;
	}

	public void setSortId(String sortId)
	{
		this.sortId = sortId;
	}

	public String getPicUrl()
	{
		return picUrl;
	}

	public void setPicUrl(String picUrl)
	{
		this.picUrl = picUrl;
	}

	public String getPicSizeInfo()
	{
		return picSizeInfo;
	}

	public void setPicSizeInfo(String picSizeInfo)
	{
		this.picSizeInfo = picSizeInfo;
	}
}
