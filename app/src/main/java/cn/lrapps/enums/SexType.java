/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 性别类型
 *
 * @author libit
 */
public enum SexType
{
	MALE((byte) 1, "男"), FEMALE((byte) 2, "女"), UNKNOWN((byte) 3, "隐藏");
	private byte sex;
	private String desc;

	private SexType(byte sex, String desc)
	{
		this.sex = sex;
		this.desc = desc;
	}

	public byte getSex()
	{
		return sex;
	}

	public void setSex(byte sex)
	{
		this.sex = sex;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public static Map<Byte, String> getMap()
	{
		Map<Byte, String> map = new HashMap<>();
		map.put(MALE.getSex(), MALE.getDesc());
		map.put(FEMALE.getSex(), FEMALE.getDesc());
		map.put(UNKNOWN.getSex(), UNKNOWN.getDesc());
		return map;
	}
}
