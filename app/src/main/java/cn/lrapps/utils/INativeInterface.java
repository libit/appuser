/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.utils;

import java.util.HashMap;

public class INativeInterface
{
	// 加载so
	static
	{
		System.loadLibrary("lrsdk");
	}

	// so调用的HTTP GET方法
	public static String doGet(String url)
	{
		return HttpTools.doGet(url, "utf-8");
	}

	// so调用的HTTP POST方法
	public static String doPost(String url, String params)
	{
		// [key,value];[key,value];...
		HashMap<String, String> map = new HashMap<>();
		String[] param = params.split(";");
		if (param.length > 0)
		{
			for (String str : param)
			{
				if (str.indexOf("[") > -1 && str.indexOf(",") > str.indexOf("[") && str.indexOf("]") > str.indexOf(","))
				{
					String key = str.substring(str.indexOf("[") + 1, str.indexOf(","));
					String value = str.substring(str.indexOf(",") + 1, str.indexOf("]"));
					map.put(key, value);
				}
			}
		}
		return HttpTools.doPost(url, map);
	}

	public native String getLocal(String user, String pwd, String number, String agentId, String signKey, String key);
}
