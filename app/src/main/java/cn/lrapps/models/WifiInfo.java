/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by libit on 2017/9/22.
 */
public class WifiInfo
{
	@SerializedName("ssid")
	private String ssid;
	@SerializedName("scan_ssid")
	private String scanSsid;
	@SerializedName("bssid")
	private String bssid;
	@SerializedName("psk")
	private String psk;
	@SerializedName("key_mgmt")
	private String keyMgmt;
	@SerializedName("priority")
	private String priority;
	@SerializedName("disabled")
	private String disabled;
	@SerializedName("id_str")
	private String idStr;
	@SerializedName("add_date")
	private Long addDateLong;

	public WifiInfo()
	{
	}

	public WifiInfo(String ssid, String scanSsid, String bssid, String psk, String keyMgmt, String priority, String disabled, String idStr, Long addDateLong)
	{
		this.ssid = ssid;
		this.scanSsid = scanSsid;
		this.bssid = bssid;
		this.psk = psk;
		this.keyMgmt = keyMgmt;
		this.priority = priority;
		this.disabled = disabled;
		this.idStr = idStr;
		this.addDateLong = addDateLong;
	}

	public String getSsid()
	{
		return ssid;
	}

	public void setSsid(String ssid)
	{
		this.ssid = ssid;
	}

	public String getScanSsid()
	{
		return scanSsid;
	}

	public void setScanSsid(String scanSsid)
	{
		this.scanSsid = scanSsid;
	}

	public String getBssid()
	{
		return bssid;
	}

	public void setBssid(String bssid)
	{
		this.bssid = bssid;
	}

	public String getPsk()
	{
		return psk;
	}

	public void setPsk(String psk)
	{
		this.psk = psk;
	}

	public String getKeyMgmt()
	{
		return keyMgmt;
	}

	public void setKeyMgmt(String keyMgmt)
	{
		this.keyMgmt = keyMgmt;
	}

	public String getPriority()
	{
		return priority;
	}

	public void setPriority(String priority)
	{
		this.priority = priority;
	}

	public String getDisabled()
	{
		return disabled;
	}

	public void setDisabled(String disabled)
	{
		this.disabled = disabled;
	}

	public String getIdStr()
	{
		return idStr;
	}

	public void setIdStr(String idStr)
	{
		this.idStr = idStr;
	}

	public Long getAddDateLong()
	{
		return addDateLong;
	}

	public void setAddDateLong(Long addDateLong)
	{
		this.addDateLong = addDateLong;
	}
}
