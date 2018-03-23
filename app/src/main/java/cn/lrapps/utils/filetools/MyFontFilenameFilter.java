/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.utils.filetools;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by libit on 2017/9/22.
 */
public class MyFontFilenameFilter implements FilenameFilter
{
	/**
	 * Tests if a specified file should be included in a file list.
	 *
	 * @param dir  the directory in which the file was found.
	 * @param name the name of the file.
	 *
	 * @return <code>true</code> if and only if the name should be
	 * included in the file list; <code>false</code> otherwise.
	 */
	@Override
	public boolean accept(File dir, String name)
	{
		File file = new File(dir, name);
		if (file.isFile() && (file.getName().endsWith("ttf") || file.getName().endsWith("ttc") || file.getName().endsWith("otf")))
		{
			return true;
		}
		return false;
	}
}
