/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.models;

import com.google.gson.annotations.SerializedName;

public class FontInfo
{
	@SerializedName("name")
	private String name;
	@SerializedName("type")
	private String type;
	@SerializedName("url")
	private String url;
	@SerializedName("size")
	private long size;

	public FontInfo()
	{
	}

	public FontInfo(String name, String type, String url, long size)
	{
		this.name = name;
		this.type = type;
		this.url = url;
		this.size = size;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}
}
