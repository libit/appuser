/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.enums;

/**
 * Created by libit on 16/8/18.
 */
public enum FloatType
{
	FLOAT_BALL("float_ball", "以小圆点显示"), FLOAT_RIGHT("float_right", "右侧显示，需从右侧向左滑调出"), FLOAT_BOTTOM("float_bottom", "底部显示，需从底部向上滑调出");
	private String type;
	private String desc;

	FloatType(String type, String desc)
	{
		this.type = type;
		this.desc = desc;
	}

	public String getType()
	{
		return type;
	}

	public String getDesc()
	{
		return desc;
	}
}
