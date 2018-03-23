/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.utils.filetools;

import cn.lrapps.enums.SqlOrderType;

import java.io.File;
import java.util.Comparator;

/**
 * Created by libit on 2017/9/22.
 */
public class FileSizeSortComparator implements Comparator<File>
{
	private String sortType;

	public FileSizeSortComparator(String sortType)
	{
		this.sortType = sortType;
	}

	@Override
	public int compare(File file1, File file2)
	{
		if (sortType.equals(SqlOrderType.ASC.getType()))
		{
			if (file1.isDirectory() && file2.isFile())
			{
				return -1;
			}
			if (file2.isDirectory() && file1.isFile())
			{
				return 1;
			}
			if (file1.length() < file2.length())
			{
				return -1;
			}
			else if (file1.length() < file2.length())
			{
				return 0;
			}
			return 1;
		}
		else
		{
			if (file2.isDirectory() && file1.isFile())
			{
				return 1;
			}
			if (file1.isDirectory() && file2.isFile())
			{
				return -1;
			}
			if (file1.length() < file2.length())
			{
				return 1;
			}
			else if (file1.length() < file2.length())
			{
				return 0;
			}
			return -1;
		}
	}
}
