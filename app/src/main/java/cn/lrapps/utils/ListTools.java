/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.utils;

import java.util.List;

/**
 * Created by libit on 16/10/3.
 */
public class ListTools
{
	/**
	 * 判断List是否有元素
	 *
	 * @param list
	 *
	 * @return
	 */
	public static final boolean isNull(List list)
	{
		return (list == null || list.size() < 1);
	}
}
