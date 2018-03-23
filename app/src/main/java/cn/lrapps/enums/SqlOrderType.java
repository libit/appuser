/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * 平台类型
 *
 * @author libit
 */
public enum SqlOrderType
{
	ASC("asc"), DESC("desc");
	private String type;

	SqlOrderType(String type)
	{
		this.setType(type);
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
}
