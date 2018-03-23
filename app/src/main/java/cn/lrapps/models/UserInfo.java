/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.models;

import com.google.gson.annotations.SerializedName;

public class UserInfo
{
	@SerializedName("userId")
	private String userId;
	@SerializedName("sessionId")
	private String sessionId;
	@SerializedName("number")
	private String number;
	@SerializedName("nickname")
	private String nickname;
	@SerializedName("sex")
	private Byte sex;
	@SerializedName("picUrl")
	private String picUrl;
	@SerializedName("birthday")
	private Long birthday;
	@SerializedName("country")
	private String country;
	@SerializedName("province")
	private String province;
	@SerializedName("city")
	private String city;
	@SerializedName("address")
	private String address;
	@SerializedName("addDateLong")
	private Long addDateLong;

	public UserInfo()
	{
	}

	public UserInfo(String userId, String sessionId, String number, String nickname, Byte sex, String picUrl, Long birthday, String country, String province, String city, String address, Long addDateLong)
	{
		this.userId = userId;
		this.sessionId = sessionId;
		this.number = number;
		this.nickname = nickname;
		this.sex = sex;
		this.picUrl = picUrl;
		this.birthday = birthday;
		this.country = country;
		this.province = province;
		this.city = city;
		this.address = address;
		this.addDateLong = addDateLong;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public Byte getSex()
	{
		return sex;
	}

	public void setSex(Byte sex)
	{
		this.sex = sex;
	}

	public String getPicUrl()
	{
		return picUrl;
	}

	public void setPicUrl(String picUrl)
	{
		this.picUrl = picUrl;
	}

	public Long getBirthday()
	{
		return birthday;
	}

	public void setBirthday(Long birthday)
	{
		this.birthday = birthday;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getProvince()
	{
		return province;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
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
