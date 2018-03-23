/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.utils.filetools;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.lrapps.android.ui.MyApplication;
import cn.lrapps.utils.AppConfig;
import cn.lrapps.utils.StringTools;

/**
 * Created by libit on 15/9/1.
 */
public class FileTools
{
	/**
	 * 获取文件<br/>
	 * 从根目录开始，需要有ROOT权限才能读取
	 *
	 * @param dir
	 * @param fileName
	 *
	 * @return
	 */
	public static File getRootFile(String dir, String fileName)
	{
		if (StringTools.isNull(fileName))
		{
			return null;
		}
		if (StringTools.isNull(dir))
		{
			dir = "";
		}
		if (!dir.endsWith(File.separator))
		{
			dir += File.separator;
		}
		if (!createDir(dir))// 创建文件夹失败
		{
			return null;
		}
		return new File(dir, fileName);
	}

	/**
	 * 获取文件
	 *
	 * @param dir
	 * @param fileName
	 *
	 * @return
	 */
	public static File getFile(String dir, String fileName)
	{
		if (StringTools.isNull(fileName))
		{
			return null;
		}
		if (StringTools.isNull(dir))
		{
			dir = "";
		}
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			// 优先保存到SD卡中
			dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AppConfig.getSDCardFolder() + File.separator + dir;
			if (!dir.endsWith(File.separator))
			{
				dir += File.separator;
			}
		}
		else
		{
			// 如果SD卡不存在，就保存到本应用的目录下
			dir = MyApplication.getContext().getFilesDir().getAbsolutePath() + File.separator + AppConfig.getSDCardFolder() + File.separator + dir;
			if (!dir.endsWith(File.separator))
			{
				dir += File.separator;
			}
		}
		if (!createDir(dir))// 创建文件夹失败
		{
			return null;
		}
		return new File(dir, fileName);
	}

	/**
	 * 取得文件夹路径
	 *
	 * @param dir
	 *
	 * @return
	 */
	public static String getAppDir(String dir)
	{
		if (StringTools.isNull(dir))
		{
			dir = "";
		}
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			// 优先保存到SD卡中
			dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AppConfig.getSDCardFolder() + File.separator + dir;
			if (!dir.endsWith(File.separator))
			{
				dir += File.separator;
			}
		}
		else
		{
			// 如果SD卡不存在，就保存到本应用的目录下
			dir = MyApplication.getContext().getFilesDir().getAbsolutePath() + File.separator + AppConfig.getSDCardFolder() + File.separator + dir;
			if (!dir.endsWith(File.separator))
			{
				dir += File.separator;
			}
		}
		if (!createDir(dir))// 创建文件夹失败
		{
			return null;
		}
		return dir;
	}

	/**
	 * 取得文件夹路径
	 *
	 * @param dir
	 *
	 * @return
	 */
	public static String getUserDir(String dir)
	{
		if (StringTools.isNull(dir))
		{
			dir = "";
		}
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			// 优先保存到SD卡中
			dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + dir;
			if (!dir.endsWith(File.separator))
			{
				dir += File.separator;
			}
		}
		else
		{
			// 如果SD卡不存在，就保存到本应用的目录下
			dir = MyApplication.getContext().getFilesDir().getAbsolutePath() + File.separator + dir;
			if (!dir.endsWith(File.separator))
			{
				dir += File.separator;
			}
		}
		if (!createDir(dir))// 创建文件夹失败
		{
			return null;
		}
		return dir;
	}

	/**
	 * 创建路径
	 *
	 * @param path 要创建的路径
	 *
	 * @return 成功true，失败false
	 */
	private static boolean createDir(String path)
	{
		if (StringTools.isNull(path))
		{
			return false;
		}
		if (new File(path).exists())
		{
			return true;
		}
		if (path.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath()))// 如果是存储卡
		{
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			{
				int index = path.indexOf(File.separator, Environment.getExternalStorageDirectory().getAbsolutePath().length() + 1);
				while (index > 0)
				{
					String subPath = path.substring(0, index);
					File dir = new File(subPath);
					if (!dir.exists())
					{
						if (!dir.mkdir())
						{
							return false;
						}
					}
					index = path.indexOf(File.separator, index + 1);
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			int index = path.indexOf(File.separator, 1);
			while (index > 0)
			{
				String subPath = path.substring(0, index);
				File dir = new File(subPath);
				if (!dir.exists())
				{
					if (!dir.mkdir())
					{
						return false;
					}
				}
				index = path.indexOf(File.separator, index + 1);
			}
		}
		return true;
	}

	/**
	 * 读文件内容
	 *
	 * @param fileName 文件名
	 *
	 * @return 文件内容
	 */
	public static String readFile(String dir, String fileName)
	{
		if (StringTools.isNull(fileName))
		{
			return null;
		}
		File file = getFile(dir, fileName);
		return readFile(file);
	}

	/**
	 * 读文件内容
	 *
	 * @param file
	 *
	 * @return
	 */
	public static String readFile(File file)
	{
		if (file != null && file.exists() && file.canRead())
		{
			BufferedReader input = null;
			try
			{
				input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line;
				StringBuilder strBuffer = new StringBuilder("");
				while ((line = input.readLine()) != null)
				{
					strBuffer.append(line).append("\n");
				}
				return strBuffer.toString();
			}
			catch (IOException e)
			{
			}
			finally
			{
				if (input != null)
				{
					try
					{
						input.close();
					}
					catch (IOException e)
					{
					}
				}
			}
		}
		return null;
	}

	/**
	 * 写文件
	 *
	 * @param file    文件
	 * @param content 文件内容
	 *
	 * @return 写入成功true，失败false
	 */
	public static boolean writeFile(File file, String content)
	{
		try
		{
			if (file != null)
			{
				if (!file.getParentFile().exists())
				{
					file.getParentFile().mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(content.getBytes());
				fos.close();
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 写文件
	 *
	 * @param fileName 文件名
	 * @param content  文件内容
	 *
	 * @return 写入成功true，失败false
	 */
	public static boolean writeFile(String dir, String fileName, String content)
	{
		try
		{
			File file = getFile(dir, fileName);
			return writeFile(file, content);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
