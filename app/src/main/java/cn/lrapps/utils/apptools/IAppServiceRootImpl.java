/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.utils.apptools;

import android.content.pm.PackageManager;

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
public class IAppServiceRootImpl extends IAppService
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
			String cmd = String.format("pm enable %s", packageName);
			cmds.add(cmd);
		}
		ShellUtils.CommandResult result = ShellUtils.execCommand(cmds, true, true);
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
			String cmd = String.format("pm disable %s", packageName);
			cmds.add(cmd);
		}
		ShellUtils.CommandResult result = ShellUtils.execCommand(cmds, true, true);
		sendAppChangedBroadcast();
		EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_CHANGED, packageNames));
		return convertResultToReturnInfo(result, "冻结应用成功。", "冻结应用失败");
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
		String cmd = String.format("am force-stop %s", packageName);
		ShellUtils.CommandResult result = ShellUtils.execCommand(cmd, true, true);
		LogcatTools.debug("ShellUtils", String.format("result:%s,%s,%s.", result.result, result.successMsg, result.errorMsg));
		return convertResultToReturnInfo(result, "关闭应用成功。", "关闭应用失败");
	}

	/**
	 * 安装App,静默安装模式
	 *
	 * @param file 安装包
	 */
	public ReturnInfo installApp(File file)
	{
		if (file == null)
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "安装文件不能为空");
		}
		String cmd = String.format("pm install %s", file.getAbsolutePath());
		ShellUtils.CommandResult result = ShellUtils.execCommand(cmd, true, true);
		sendAppChangedBroadcast();
		EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_CHANGED, null));
		return convertResultToReturnInfo(result, "安装应用成功。", "安装应用失败");
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
		String cmd = String.format("pm uninstall %s", packageName);
		ShellUtils.CommandResult result = ShellUtils.execCommand(cmd, true, true);
		sendAppChangedBroadcast();
		List<String> packageNameList = new ArrayList<>();
		packageNameList.add(packageName);
		EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_CHANGED, packageNameList));
		return convertResultToReturnInfo(result, "卸载应用成功。", "卸载应用失败");
	}
}
