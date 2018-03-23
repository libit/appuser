/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.models;

import cn.lrapps.utils.StringTools;

import java.util.HashMap;

public class ErrorInfo
{
	public static final int SUCCESS = 0;
	public static final int PARAM_ERROR = -10;
	public static final int EXIST_ERROR = -11;
	public static final int NOT_EXIST_ERROR = -12;
	public static final int HIBERNATE_ERROR = -100;
	public static final int NETWORK_ERROR = -200;
	public static final int FORBIDDEN_ERROR = -300;
	public static final int UNKNOWN_ERROR = -10000;
	public static final int PASSWORD_ERROR = -20000;
	public final static HashMap<Integer, String> ERROR_INFO = new HashMap<Integer, String>()
	{
		private static final long serialVersionUID = 111L;

		{
			put(SUCCESS, "操作成功");
			put(PARAM_ERROR, "参数错误");
			put(EXIST_ERROR, "对象已存在");
			put(NOT_EXIST_ERROR, "对象不存在");
			put(HIBERNATE_ERROR, "数据库错误");
			put(NETWORK_ERROR, "网络错误");
			put(FORBIDDEN_ERROR, "禁止访问");
			put(PASSWORD_ERROR, "密码错误");
			put(UNKNOWN_ERROR, "未知错误");
		}
	};

	public static String getErrorInfo(int errcode)
	{
		String errInfo = ERROR_INFO.get(errcode);
		if (StringTools.isNull(errInfo))
		{
			errInfo = String.format("未知错误代码%d", errcode);
		}
		return errInfo;
	}
}
