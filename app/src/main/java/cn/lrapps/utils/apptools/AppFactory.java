/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.utils.apptools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import cn.lrapps.models.AppInfo;
import cn.lrapps.models.ErrorInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ShellUtils;

import java.io.File;
import java.util.List;

/**
 * Created by libit on 15/8/19.
 */
public abstract class AppFactory
{
	private static AppFactory appInstance;
	protected final IAppService appService;

	protected AppFactory()
	{
		boolean isRoot = true;// PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_ROOT);
		//		if (!isRoot)
		//		{
		//			isRoot = ShellUtils.checkRootPermission();
		//			PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_ROOT, isRoot);
		//		}
		if (isRoot)
		{
			appService = new IAppServiceRootImpl();
		}
		else
		{
			appService = new IAppServiceUnRootImpl();
		}
	}

	synchronized public static AppFactory getInstance()
	{
		if (appInstance == null)
		{
			if (AppFactory.isCompatible(22))
			{
				appInstance = new AppUtil22();
			}
			else
			{
				appInstance = new AppUtil8();
			}
		}
		return appInstance;
	}

	/**
	 * 判断设备API是否兼容指定的版本号
	 *
	 * @param apiLevel 指定的版本号
	 *
	 * @return 兼容返回true，否则返回false
	 */
	public static boolean isCompatible(int apiLevel)
	{
		return android.os.Build.VERSION.SDK_INT >= apiLevel;
	}

	public static ReturnInfo loadSystemPermissions()
	{
		//先挂载为可读写
		ShellUtils.CommandResult commandResult = ShellUtils.execCommand("mount -o rw,remount /system", true, true);
		//		LogcatTools.debug(TAG, "文件修改权限结果：" + GsonTools.toJson(commandResult));
		ReturnInfo returnInfo = IAppService.convertResultToReturnInfo(commandResult, "挂载权限成功。", "挂载系统分区为可读写失败");
		if (returnInfo == null)
		{
			returnInfo = new ReturnInfo(ErrorInfo.UNKNOWN_ERROR, "挂载系统分区为可读写失败。");
		}
		return returnInfo;
	}

	public static ReturnInfo closeSystemPermissions()
	{
		//完事后改回来
		ShellUtils.CommandResult commandResult = ShellUtils.execCommand("mount -o ro,remount /system", true, true);
		//		LogcatTools.debug(TAG, "文件修改权限结果：" + GsonTools.toJson(commandResult));
		ReturnInfo returnInfo = IAppService.convertResultToReturnInfo(commandResult, "挂载权限成功。", "挂载系统分区为只读失败");
		if (returnInfo == null)
		{
			returnInfo = new ReturnInfo(ErrorInfo.UNKNOWN_ERROR, "挂载系统分区为只读失败。");
		}
		return returnInfo;
	}

	/**
	 * usage: input ...
	 * input text <string>
	 * input keyevent <key code number or name>
	 * input tap <x> <y>
	 * input swipe <x1> <y1> <x2> <y2>
	 *
	 * @param x
	 * @param y
	 *
	 * @return
	 */
	public static ReturnInfo simulateClick(int x, int y)
	{
		String cmd = String.format("input tap %d %d", x, y);
		ShellUtils.CommandResult commandResult = ShellUtils.execCommand(cmd, true, true);
		ReturnInfo returnInfo = IAppService.convertResultToReturnInfo(commandResult, "模拟点击成功。", "模拟点击失败");
		if (returnInfo == null)
		{
			returnInfo = new ReturnInfo(ErrorInfo.UNKNOWN_ERROR, "模拟点击失败。");
		}
		return returnInfo;
	}

	/**
	 * 启用App
	 *
	 * @param packageName    App包名
	 * @param deleteBlackApp 是否在黑名单中删除
	 *
	 * @return
	 */
	public abstract ReturnInfo enableApp(String packageName, boolean deleteBlackApp);

	/**
	 * 启用App列表
	 *
	 * @param packageNames   App包名列表
	 * @param deleteBlackApp 是否在黑名单中删除
	 *
	 * @return
	 */
	public abstract ReturnInfo enableApps(List<String> packageNames, boolean deleteBlackApp);

	/**
	 * 禁用App
	 *
	 * @param packageName App包名
	 *
	 * @return
	 */
	public abstract ReturnInfo disableApp(String packageName);

	/**
	 * 禁用App列表
	 *
	 * @param packageNames App包名列表
	 *
	 * @return
	 */
	public abstract ReturnInfo disableApps(List<String> packageNames);

	/**
	 * 安装App
	 *
	 * @param file 安装包
	 * @param root 是否以root执行
	 */
	public abstract ReturnInfo installApp(File file, boolean root);

	/**
	 * 卸载App
	 *
	 * @param packageName App包名
	 * @param root        是否以root执行
	 */
	public abstract ReturnInfo uninstallApp(String packageName, boolean root);

	/**
	 * 关闭正在运行的程序信息
	 *
	 * @param packageName
	 *
	 * @return
	 */
	public abstract ReturnInfo killApp(String packageName);

	/**
	 * 同步系统的App信息和数据库的App信息
	 *
	 * @return App列表
	 */
	public abstract List<AppInfo> syncDBApps(boolean isFirstRun);

	/**
	 * 更改App状态
	 *
	 * @param packageName    App包名
	 * @param enable         是否启用
	 * @param deleteBlackApp 是否从黑名单删除
	 */
	protected abstract void updateAppStatus(String packageName, boolean enable, boolean deleteBlackApp);

	/**
	 * 获取App列表
	 *
	 * @param type   App类型
	 * @param status App状态
	 *
	 * @return App列表
	 */
	public abstract List<AppInfo> getApps(int type, Integer status);

	/**
	 * 获取App信息
	 *
	 * @param packageName App的packageName
	 * @param showPhoto   是否显示图片
	 *
	 * @return App对象
	 */
	public abstract AppInfo getAppInfoByPackageName(String packageName, boolean showPhoto);

	/**
	 * 获取APP的图标
	 *
	 * @param packageName
	 *
	 * @return
	 */
	public abstract Drawable getAppIconByPackageName(String packageName);

	/**
	 * 获取自身App包信息
	 *
	 * @return App对象
	 */
	public abstract PackageInfo getSelfPackageInfo() throws PackageManager.NameNotFoundException;

	/**
	 * 启动应用程序
	 *
	 * @param context     上下文
	 * @param packageName 应用包名
	 *
	 * @return 启动成功返回true，失败返回false
	 */
	public abstract ReturnInfo startApp(Context context, String packageName);

	/**
	 * 获取正在运行的程序列表
	 *
	 * @return App列表
	 */
	public abstract List<AppInfo> getRunningApps();

	/**
	 * 获取apk安装包的信息
	 *
	 * @param apkFile apk文件
	 *
	 * @return
	 */
	public abstract AppInfo getApkInfo(File apkFile);

	/**
	 * 获取apk安装包的信息
	 *
	 * @param apkPath apk路径
	 *
	 * @return
	 */
	public abstract AppInfo getApkInfo(String apkPath);

	/**
	 * 获取内存信息
	 *
	 * @return
	 */
	public abstract String getMemoryInfo();
}
