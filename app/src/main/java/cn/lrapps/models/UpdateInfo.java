/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.models;

import com.google.gson.annotations.SerializedName;

public class UpdateInfo
{
	@SerializedName("platform")
	private String platform;
	@SerializedName("versionName")
	private String versionName;
	@SerializedName("versionCode")
	private Integer versionCode;
	@SerializedName("url")
	private String url;
	@SerializedName("description")
	private String description;
	@SerializedName("updateDateLong")
	private Long updateDateLong;

	public UpdateInfo()
	{
		super();
	}

	public String getPlatform()
	{
		return platform;
	}

	public void setPlatform(String platform)
	{
		this.platform = platform;
	}

	public String getVersionName()
	{
		return versionName;
	}

	public void setVersionName(String versionName)
	{
		this.versionName = versionName;
	}

	public Integer getVersionCode()
	{
		return versionCode;
	}

	public void setVersionCode(Integer versionCode)
	{
		this.versionCode = versionCode;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Long getUpdateDateLong()
	{
		return updateDateLong;
	}

	public void setUpdateDateLong(Long updateDateLong)
	{
		this.updateDateLong = updateDateLong;
	}
}
