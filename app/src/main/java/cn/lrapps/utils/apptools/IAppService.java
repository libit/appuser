/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.utils.apptools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import cn.lrapps.android.ui.MyApplication;
import cn.lrapps.android.ui.widget.BlackAppWidgetProvider;
import cn.lrapps.models.ErrorInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.ShellUtils;
import cn.lrapps.utils.StringTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by libit on 15/9/29.
 */
public abstract class IAppService
{
	/**
	 * 启用Apps
	 *
	 * @param packageNames App包名列表
	 *
	 * @return
	 */
	abstract ReturnInfo enableApps(List<String> packageNames);

	/**
	 * 禁用Apps
	 *
	 * @param packageNames App包名列表
	 *
	 * @return
	 */
	abstract ReturnInfo disableApps(List<String> packageNames);

	/**
	 * 关闭正在运行的程序信息
	 *
	 * @param packageName
	 *
	 * @return
	 */
	abstract ReturnInfo killApp(String packageName);

	/**
	 * 安装App
	 *
	 * @param file 安装包
	 */
	abstract ReturnInfo installApp(File file);

	/**
	 * 卸载App
	 *
	 * @param packageName App包名
	 */
	abstract ReturnInfo uninstallApp(String packageName);

	/**
	 * 发送APP状态变化事件
	 */
	public void sendAppChangedBroadcast()
	{
		Intent intent = new Intent().setAction(BlackAppWidgetProvider.BTN_REFRESH_ACTION);
		MyApplication.getContext().sendBroadcast(intent);
	}

	/**
	 * 将命令结果转换成ReturnInfo
	 *
	 * @param result     命令执行结果
	 * @param successMsg 执行成功的提示
	 *
	 * @return
	 */
	public static ReturnInfo convertResultToReturnInfo(ShellUtils.CommandResult result, String successMsg, String failMsg)
	{
		if (result == null)
		{
			return new ReturnInfo(ErrorInfo.NOT_EXIST_ERROR, "没有执行结果。");
		}
		if (result.result == 0)
		{
			return new ReturnInfo(ErrorInfo.SUCCESS, successMsg);
		}
		else
		{
			return new ReturnInfo(result.result, failMsg + "：" + result.errorMsg);
		}
	}

	/**
	 * 启用App
	 *
	 * @param packageName App包名
	 *
	 * @return
	 */
	public ReturnInfo enableApp(String packageName)
	{
		if (StringTools.isNull(packageName))
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		List<String> packageNameList = new ArrayList<>();
		packageNameList.add(packageName);
		return enableApps(packageNameList);
	}

	/**
	 * 禁用App
	 *
	 * @param packageName App包名
	 *
	 * @return
	 */
	public ReturnInfo disableApp(String packageName)
	{
		if (StringTools.isNull(packageName))
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		List<String> packageNameList = new ArrayList<>();
		packageNameList.add(packageName);
		return disableApps(packageNameList);
	}

	/**
	 * 启动应用程序
	 *
	 * @param packageName 应用包名
	 *
	 * @return
	 */
	public ReturnInfo startApp(Context context, String packageName)
	{
		if (StringTools.isNull(packageName))
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		boolean isFind = false;
		//		if (context == null)
		{
			context = MyApplication.getInstance();
		}
		LogcatTools.debug("startApp", packageName + "启动context：" + context);
		PackageManager packageManager = MyApplication.getContext().getPackageManager();
		//		Intent startIntent = packageManager.getLaunchIntentForPackage(packageName);
		//		if (startIntent != null)
		//		{
		//			isFind = true;
		//			context.startActivity(startIntent);
		//			LogcatTools.debug("startApp", packageName + "启动startIntent：" + GsonTools.toJson(startIntent));
		//		}
		//		else
		{
			PackageInfo packageInfo = null;
			try
			{
				packageInfo = packageManager.getPackageInfo(packageName, 0);
			}
			catch (PackageManager.NameNotFoundException e)
			{
				e.printStackTrace();
			}
			if (packageInfo == null)
			{
				return new ReturnInfo(ErrorInfo.NOT_EXIST_ERROR, "包名不存在");
			}
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(packageName);
			List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(resolveIntent, 0);
			while (resolveInfoList.iterator().hasNext())
			{
				ResolveInfo resolveInfo = resolveInfoList.iterator().next();
				if (resolveInfo != null)
				{
					String className = resolveInfo.activityInfo.name;
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setComponent(new ComponentName(packageName, className));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					isFind = true;
					LogcatTools.debug("startApp", packageName + "启动class：" + className);
					break;
				}
			}
		}
		if (isFind)
		{
			return new ReturnInfo(ErrorInfo.SUCCESS, "启动成功");
		}
		else
		{
			return new ReturnInfo(ErrorInfo.UNKNOWN_ERROR, "启动失败");
		}
	}
}
