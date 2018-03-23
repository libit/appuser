/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by libit on 15/8/31.
 */
public class BackupInfo
{
	@SerializedName("name")
	private String name;// 名字
	@SerializedName("version")
	private String version;//版本号
	@SerializedName("comment")
	private String comment;// 备注
	@SerializedName("time")
	private String time;// 备份时间
	@SerializedName("apps")
	private String apps;// 应用列表
	@SerializedName("configs")
	private String configs;//  配置
	@SerializedName("fileName")
	private String fileName;//文件名字

	public BackupInfo()
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getApps()
	{
		return apps;
	}

	public void setApps(String apps)
	{
		this.apps = apps;
	}

	public String getConfigs()
	{
		return configs;
	}

	public void setConfigs(String configs)
	{
		this.configs = configs;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	@Override
	public String toString()
	{
		return String.format("name:%s,version:%s,comment:%s,time:%s.", name, version, comment, time);
	}
}
