/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.utils.apptools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import cn.lrapps.android.ui.MyApplication;
import cn.lrapps.events.AppEvent;
import cn.lrapps.models.ErrorInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.ShellUtils;
import cn.lrapps.utils.StringTools;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by libit on 15/9/29.
 */
public class IAppServiceUnRootImpl extends IAppService
{
	/**
	 * 启用Apps
	 *
	 * @param packageNames App包名列表
	 *
	 * @return
	 */
	@Override
	public ReturnInfo enableApps(List<String> packageNames)
	{
		if (packageNames == null || packageNames.size() < 1)
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		List<String> cmds = new ArrayList<>();
		for (String packageName : packageNames)
		{
			if (StringTools.isNull(packageName))
			{
				continue;
			}
			String cmd = String.format("pm unblock %s", packageName);
			cmds.add(cmd);
		}
		ShellUtils.CommandResult result = ShellUtils.execCommand(cmds, false, true);
		sendAppChangedBroadcast();
		EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_CHANGED, packageNames));
		return convertResultToReturnInfo(result, "启用应用成功。", "启用应用失败");
	}

	/**
	 * 禁用Apps
	 *
	 * @param packageNames App包名列表
	 *
	 * @return
	 */
	@Override
	public ReturnInfo disableApps(List<String> packageNames)
	{
		if (packageNames == null || packageNames.size() < 1)
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		List<String> cmds = new ArrayList<>();
		for (String packageName : packageNames)
		{
			if (StringTools.isNull(packageName))
			{
				continue;
			}
			try
			{
				if (packageName.equalsIgnoreCase(AppFactory.getInstance().getSelfPackageInfo().packageName))
				{
					continue;
				}
			}
			catch (PackageManager.NameNotFoundException e)
			{
				LogcatTools.error("disableApps", "本应用包信息获取失败！");
			}
			//			String cmd = String.format("pm block %s", packageName);
			String cmd = String.format("pm disable-user %s", packageName);
			cmds.add(cmd);
		}
		ShellUtils.CommandResult result = ShellUtils.execCommand(cmds, false, true);
		if (result.result != 0)
		{
			LogcatTools.error("disableApps", result.errorMsg);
		}
		sendAppChangedBroadcast();
		EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_CHANGED, packageNames));
		return convertResultToReturnInfo(result, "冻结所有应用成功。", "冻结所有应用失败");
	}

	/**
	 * 关闭正在运行的程序信息
	 *
	 * @param packageName
	 *
	 * @return
	 */
	public ReturnInfo killApp(String packageName)
	{
		if (StringTools.isNull(packageName))
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		ActivityManager activityManager = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.killBackgroundProcesses(packageName);
		return new ReturnInfo(ErrorInfo.SUCCESS, "关闭应用成功");
	}

	/**
	 * 安装App
	 *
	 * @param file 安装包
	 */
	public ReturnInfo installApp(File file)
	{
		if (file == null)
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "安装文件不能为空");
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data;
		if (AppFactory.isCompatible(Build.VERSION_CODES.N))
		{
			// "com.lrcall.fileprovider"即是在清单文件中配置的authorities
			data = FileProvider.getUriForFile(MyApplication.getContext(), "com.lrcall.fileprovider", file);
			// 给目标应用一个临时授权
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		else
		{
			data = Uri.fromFile(file);
		}
		intent.setDataAndType(data, "application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MyApplication.getContext().startActivity(intent);
		return new ReturnInfo(ErrorInfo.SUCCESS, "安装应用成功");
	}

	/**
	 * 卸载App
	 *
	 * @param packageName App包名
	 */
	public ReturnInfo uninstallApp(String packageName)
	{
		if (StringTools.isNull(packageName))
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		Uri uri = Uri.parse("package:" + packageName);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DELETE);
		intent.setData(uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MyApplication.getContext().startActivity(intent);
		return new ReturnInfo(ErrorInfo.SUCCESS, "卸载应用成功");
	}
}
