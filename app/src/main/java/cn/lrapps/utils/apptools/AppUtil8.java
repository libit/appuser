/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.utils.apptools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import cn.lrapps.android.ui.MyApplication;
import cn.lrapps.models.AppInfo;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.AppEnableStatus;
import cn.lrapps.enums.AppType;
import cn.lrapps.enums.StatusType;
import cn.lrapps.models.ErrorInfo;
import cn.lrapps.models.MemoryInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.StringTools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by libit on 15/8/19.
 */
public class AppUtil8 extends AppFactory
{
	@Override
	synchronized public List<AppInfo> syncDBApps(boolean isFirstRun)
	{
		long t0 = System.currentTimeMillis();
		ArrayList<AppInfo> appInfos = new ArrayList<>();
		PackageManager packageManager = MyApplication.getContext().getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		String selfName = "";
		try
		{
			PackageInfo packageInfo = getSelfPackageInfo();
			selfName = packageInfo.packageName;
		}
		catch (PackageManager.NameNotFoundException e)
		{
		}
		int count = packageInfos.size();
		for (int i = 0; i < count; i++)
		{
			PackageInfo packageInfo = packageInfos.get(i);
			if (!StringTools.isNull(selfName) && selfName.equals(packageInfo.packageName))
			{
				continue;
			}
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			int appType = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0 ? AppType.SYSTEM.getType() : AppType.USER.getType();
			// 构造App信息
			AppInfo appInfo = new AppInfo();
			appInfo.setUid(applicationInfo.uid + "");
			long t1 = System.currentTimeMillis();
			appInfo.setName(applicationInfo.loadLabel(packageManager).toString());
			long t2 = System.currentTimeMillis();
			appInfo.setPackageName(packageInfo.packageName);
			appInfo.setVersionName(packageInfo.versionName);
			appInfo.setVersionCode(packageInfo.versionCode);
			appInfo.setType(appType);
			appInfo.setIsEnabled(applicationInfo.enabled);
			appInfo.setIsExist(true);
			appInfo.setCommonUse(StatusType.DISABLE.getStatus());
			//            if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.SHOW_SYSTEM_APP))
			//            {
			//                appInfo.setIsExist(true);
			//            }
			//            else
			//            {
			//                appInfo.setIsExist(appType != ConstValues.SYSTEM);
			//            }
			if (isFirstRun)
			{
				LogcatTools.debug("isFirstRun", "第一次运行");
				// 如果程序无法启动，则加入到隐藏列表
				Intent intent = new Intent(Intent.ACTION_MAIN, null);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setPackage(applicationInfo.packageName);
				List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
				boolean isHide = !resolveInfoList.iterator().hasNext() && appType == AppType.SYSTEM.getType();
				appInfo.setIsHide(isHide);
				appInfo.setBlack(false);
			}
			else
			{
				LogcatTools.debug("isFirstRun", "第N次运行");
				appInfo.setIsHide(DbAppFactory.getInstance().isHide(packageInfo.packageName));
				appInfo.setBlack(DbAppFactory.getInstance().isBlack(packageInfo.packageName));
			}
			LogcatTools.info("syncDBApps", appInfo.toString());
			// 同步到数据库
			//            DbAppFactory.getInstance().addOrUpdate(appInfo);
			appInfos.add(appInfo);
			//            AppConfig.showLog("syncDBApps", "单个花费时间：" + (t2 - t1) + "毫秒");
		}
		LogcatTools.info("syncDBApps", "开始处理app");
		// 先将App状态设置成不存在，再一个个添加
		long t4 = System.currentTimeMillis();
		DbAppFactory.getInstance().resetAppStatus(null, null, false);
		LogcatTools.info("syncDBApps", "开始处理app1");
		long t5 = System.currentTimeMillis();
		DbAppFactory.getInstance().addOrUpdateList(appInfos);
		long t3 = System.currentTimeMillis();
		LogcatTools.info("syncDBApps", "总花费时间：" + (t3 - t0) + "毫秒,addOrUpdateList花费时间：" + (t3 - t5) + "毫秒");
		return appInfos;
	}

	@Override
	public List<AppInfo> getApps(int type, Integer status)
	{
		long t0 = System.currentTimeMillis();
		List<AppInfo> appInfos = new ArrayList<>();
		PackageManager packageManager = MyApplication.getContext().getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		//        PackageInfo myPackageInfo = null;// 程序自身的包信息
		//        try
		//        {
		//            myPackageInfo = getSelfPackageInfo();// 程序自身的包信息
		//        }
		//        catch (PackageManager.NameNotFoundException e)
		//        {
		//        }
		int count = packageInfos.size();
		for (int i = 0; i < count; i++)
		{
			PackageInfo packageInfo = packageInfos.get(i);
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			//            if (myPackageInfo.packageName == packageInfo.packageName)// 如果是自身应用，则跳过
			//            {
			//                continue;
			//            }
			if (DbAppFactory.getInstance().isHide(packageInfo.packageName))
			{
				continue;
			}
			int appType = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0 ? AppType.SYSTEM.getType() : AppType.USER.getType();
			int appStatus = AppEnableStatus.getAppStatus(applicationInfo.enabled);
			// 如果要查询的是用户程序
			if (appType != type)
			{
				continue;
			}
			// 要查询的状态
			if (status != null && appStatus != status)
			{
				continue;
			}
			AppInfo appInfo = new AppInfo();
			appInfo.setUid(applicationInfo.uid + "");
			appInfo.setName(applicationInfo.loadLabel(packageManager).toString());
			appInfo.setPackageName(applicationInfo.packageName);
			appInfo.setVersionName(packageInfo.versionName);
			appInfo.setVersionCode(packageInfo.versionCode);
			//			try
			//			{
			//				appInfo.setPhoto(((BitmapDrawable) applicationInfo.loadIcon(packageManager)).getBitmap());
			//			}
			//			catch (ClassCastException e)
			//			{
			//				AppConfig.showLog("ClassCastException", "\"" + appInfo.getName() + "\"强制转换图片失败了！");
			//			}
			appInfo.setType(appType);
			appInfo.setIsEnabled(applicationInfo.enabled);
			LogcatTools.debug("appInfo", appInfo.toString());
			appInfos.add(appInfo);
		}
		long t1 = System.currentTimeMillis();
		Collections.sort(appInfos, new AppInfo());
		long t2 = System.currentTimeMillis();
		LogcatTools.debug("getApps", "总花费时间：" + (t2 - t0) + "毫秒,排序花费时间：" + (t2 - t1) + "毫秒");
		return appInfos;
	}

	/**
	 * 获取App信息
	 *
	 * @param packageName App的packageName
	 * @param showPhoto   是否显示图片
	 *
	 * @return App对象
	 */
	@Override
	public AppInfo getAppInfoByPackageName(String packageName, boolean showPhoto)
	{
		AppInfo appInfo = new AppInfo();
		PackageManager packageManager = MyApplication.getContext().getPackageManager();
		try
		{
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			int appType = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0 ? AppType.SYSTEM.getType() : AppType.USER.getType();
			appInfo.setUid(applicationInfo.uid + "");
			appInfo.setName(applicationInfo.loadLabel(packageManager).toString());
			appInfo.setPackageName(applicationInfo.packageName);
			appInfo.setVersionName(packageInfo.versionName);
			appInfo.setVersionCode(packageInfo.versionCode);
			if (showPhoto)
			{
				try
				{
					appInfo.setPhoto(((BitmapDrawable) applicationInfo.loadIcon(packageManager)).getBitmap());
				}
				catch (ClassCastException e)
				{
					LogcatTools.debug("ClassCastException", "\"" + appInfo.getName() + "\"强制转换图片失败了！");
				}
			}
			appInfo.setType(appType);
			appInfo.setIsEnabled(applicationInfo.enabled);
			appInfo.setIsExist(true);
			LogcatTools.debug("appInfo", appInfo.toString());
		}
		catch (PackageManager.NameNotFoundException e)
		{
			LogcatTools.debug("NameNotFoundException", "包名" + packageName + "\"未找到！");
		}
		return appInfo;
	}

	/**
	 * 获取APP的图标
	 *
	 * @param packageName
	 *
	 * @return
	 */
	@Override
	public Drawable getAppIconByPackageName(String packageName)
	{
		PackageManager packageManager = MyApplication.getContext().getPackageManager();
		try
		{
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			return applicationInfo.loadIcon(packageManager);
		}
		catch (PackageManager.NameNotFoundException e)
		{
			LogcatTools.debug("NameNotFoundException", "包名" + packageName + "\"未找到！");
		}
		return null;
	}

	/**
	 * 获取自身App包信息
	 *
	 * @return App对象
	 */
	@Override
	public PackageInfo getSelfPackageInfo() throws PackageManager.NameNotFoundException
	{
		PackageManager packageManager = MyApplication.getContext().getPackageManager();
		return packageManager.getPackageInfo(MyApplication.getContext().getPackageName(), 0);
	}

	/**
	 * 启动应用程序
	 *
	 * @param packageName 应用包名
	 *
	 * @return 启动成功返回true，失败返回false
	 */
	@Override
	public ReturnInfo startApp(Context context, String packageName)
	{
		if (StringTools.isNull(packageName))
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		return appService.startApp(context, packageName);
	}

	/**
	 * 获取正在运行的程序列表
	 *
	 * @return App列表
	 */
	@Override
	public List<AppInfo> getRunningApps()
	{
		PackageInfo myPackageInfo = null;// 程序自身的包信息
		try
		{
			myPackageInfo = getSelfPackageInfo();// 程序自身的包信息
		}
		catch (PackageManager.NameNotFoundException e)
		{
		}
		List<AppInfo> appInfoList = new ArrayList<>();
		ActivityManager activityManager = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = new CopyOnWriteArrayList<>(activityManager.getRunningAppProcesses());
		final int MAX_COUNT = 5;
		int blackCount = 0;
		//		List<BlackAppInfo> blackAppInfoList = new ArrayList<>();
		for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfoList)
		{
			LogcatTools.debug("runningAppProcessInfo", "uid:" + runningAppProcessInfo.uid + ",processName:" + runningAppProcessInfo.processName);
			for (String packageName : runningAppProcessInfo.pkgList)
			{
				if (DbAppFactory.getInstance().isHide(packageName))
				{
					continue;
				}
				if (myPackageInfo != null && myPackageInfo.packageName.equals(packageName))// 如果是自身，则跳过
				{
					continue;
				}
				// 如果已存在，则跳过继续
				boolean isExist = false;
				for (AppInfo appInfo : appInfoList)
				{
					if (appInfo.getPackageName().equals(packageName))
					{
						isExist = true;
						break;
					}
				}
				if (isExist)
				{
					continue;
				}
				AppInfo newAppInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, true);
				if (newAppInfo != null)
				{
					LogcatTools.debug("runningAppProcessInfo", "newAppInfo:" + newAppInfo.toString());
					//					BlackAppInfo blackAppInfo = DbBlackAppFactory.getInstance().query(newAppInfo.getPackageName());
					//					if (blackAppInfo != null && !StringTools.isNull(blackAppInfo.getPackageName()))
					//					{
					//						if (!blackAppInfoList.contains(blackAppInfo))
					//						{
					//							blackCount++;
					//							blackAppInfoList.add(blackAppInfo);
					//							LogcatTools.debug("runningAppProcessInfo", "黑名单" + blackCount + ":" + blackAppInfo.toString());
					//							if (blackCount > MAX_COUNT)
					//							{
					//								AppFactory.getInstance().disableApp(blackAppInfo.getPackageName());
					//								LogcatTools.debug("runningAppProcessInfo", "禁止黑名单:" + blackAppInfo.toString());
					//								continue;
					//							}
					//						}
					//					}
					appInfoList.add(newAppInfo);
				}
			}
		}
		//		List<BlackAppInfo> enabledBlackAppInfoList = DbBlackAppFactory.getInstance().getEnabledBlackAppList();
		//		for (BlackAppInfo blackAppInfo : enabledBlackAppInfoList)
		//		{
		//			if (!blackAppInfoList.contains(blackAppInfo))
		//			{
		//				blackCount++;
		//				if (blackCount > MAX_COUNT)
		//				{
		//					AppFactory.getInstance().disableApp(blackAppInfo.getPackageName());
		//					LogcatTools.debug("runningAppProcessInfo", "禁止黑名单:" + blackAppInfo.toString());
		//				}
		//			}
		//		}
		return appInfoList;
	}

	/**
	 * 安装App
	 *
	 * @param file 安装包
	 */
	@Override
	public ReturnInfo installApp(File file, boolean root)
	{
		if (root)
		{
			return new IAppServiceRootImpl().installApp(file);
		}
		else
		{
			return new IAppServiceUnRootImpl().installApp(file);
		}
	}

	/**
	 * 卸载App
	 *
	 * @param packageName App包名
	 */
	@Override
	public ReturnInfo uninstallApp(String packageName, boolean root)
	{
		ReturnInfo returnInfo = null;
		if (root)
		{
			returnInfo = new IAppServiceRootImpl().uninstallApp(packageName);
		}
		else
		{
			returnInfo = new IAppServiceUnRootImpl().uninstallApp(packageName);
		}
		//处理数据库
		if (ReturnInfo.isSuccess(returnInfo))
		{
			// 设置应用不存在
			//			DbAppFactory.getInstance().setAppExist(packageName, false);
		}
		return returnInfo;
	}

	/**
	 * 更改App状态
	 *
	 * @param packageName
	 * @param enable
	 * @param deleteBlackApp
	 */
	@Override
	protected void updateAppStatus(String packageName, boolean enable, boolean deleteBlackApp)
	{
		if (StringTools.isNull(packageName))
		{
			return;
		}
		// 将App的状态设为启用
		AppInfo appInfo = DbAppFactory.getInstance().getAppInfo(packageName);
		if (appInfo == null)
		{
			return;
		}
		appInfo.setIsEnabled(enable);
		if (deleteBlackApp)
		{
			appInfo.setBlack(false);
		}
		else
		{
			appInfo.setBlack(true);
		}
		appInfo.setIsExist(true);
		DbAppFactory.getInstance().update(appInfo);
	}

	/**
	 * 启用App
	 *
	 * @param packageName    App包名
	 * @param deleteBlackApp 是否在黑名单中删除
	 *
	 * @return
	 */
	@Override
	public ReturnInfo enableApp(String packageName, boolean deleteBlackApp)
	{
		if (StringTools.isNull(packageName))
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		return enableApps(Arrays.asList(packageName), deleteBlackApp);
	}

	/**
	 * 启用App
	 *
	 * @param packageNames   App包名列表
	 * @param deleteBlackApp 是否在黑名单中删除
	 *
	 * @return
	 */
	@Override
	public ReturnInfo enableApps(List<String> packageNames, boolean deleteBlackApp)
	{
		if (packageNames == null || packageNames.size() < 1)
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		List<String> disPackageNames = new ArrayList<>();
		for (String packageName : packageNames)
		{
			if (StringTools.isNull(packageName))
			{
				continue;
			}
			AppInfo appInfo = getAppInfoByPackageName(packageName, false);
			if (appInfo != null)
			{
				if (!appInfo.isEnabled())
				{
					LogcatTools.debug("enableApps", "packageName:" + packageName);
					disPackageNames.add(packageName);
				}
				else
				{
					updateAppStatus(packageName, true, deleteBlackApp);
				}
			}
		}
		ReturnInfo returnInfo = appService.enableApps(disPackageNames);
		if (ReturnInfo.isSuccess(returnInfo))// 启用成功
		{
			for (String packageName : packageNames)
			{
				if (StringTools.isNull(packageName))
				{
					continue;
				}
				updateAppStatus(packageName, true, deleteBlackApp);
			}
		}
		return returnInfo;
	}

	/**
	 * 禁用App
	 *
	 * @param packageName App包名
	 *
	 * @return
	 */
	@Override
	public ReturnInfo disableApp(String packageName)
	{
		if (StringTools.isNull(packageName))
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		return disableApps(Arrays.asList(packageName));
	}

	/**
	 * 禁用App
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
		List<String> enablePackageNames = new ArrayList<>();
		for (String packageName : packageNames)
		{
			if (StringTools.isNull(packageName))
			{
				continue;
			}
			AppInfo appInfo = getAppInfoByPackageName(packageName, false);
			if (appInfo == null || appInfo.isEnabled())
			{
				enablePackageNames.add(packageName);
			}
		}
		ReturnInfo returnInfo = appService.disableApps(enablePackageNames);
		if (ReturnInfo.isSuccess(returnInfo))
		{
			for (String packageName : packageNames)
			{
				updateAppStatus(packageName, false, false);
			}
		}
		return returnInfo;
	}

	/**
	 * 关闭正在运行的程序信息
	 *
	 * @param packageName App包名
	 *
	 * @return
	 */
	@Override
	public ReturnInfo killApp(String packageName)
	{
		if (StringTools.isNull(packageName))
		{
			return new ReturnInfo(ErrorInfo.PARAM_ERROR, "包名不能为空");
		}
		return appService.killApp(packageName);
	}

	/**
	 * 获取apk安装包的信息
	 *
	 * @param apkFile
	 *
	 * @return App列表
	 */
	@Override
	public AppInfo getApkInfo(File apkFile)
	{
		String apkPath = apkFile.getAbsolutePath();
		return getApkInfo(apkPath);
	}

	/**
	 * 获取apk安装包的信息
	 *
	 * @param apkPath apk路径
	 *
	 * @return
	 */
	@Override
	public AppInfo getApkInfo(String apkPath)
	{
		AppInfo appInfo = null;
		PackageManager packageManager = MyApplication.getInstance().getPackageManager();
		PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		if (packageInfo != null)
		{
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			/* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
			applicationInfo.sourceDir = apkPath;
			applicationInfo.publicSourceDir = apkPath;
			String appName = packageManager.getApplicationLabel(applicationInfo).toString();// 得到应用名
			//			String packageName = applicationInfo.packageName; // 得到包名
			//			String version = packageInfo.versionName; // 得到版本信息
			/* icon1和icon2其实是一样的 */
			Drawable icon1 = packageManager.getApplicationIcon(applicationInfo);// 得到图标信息
			Drawable icon2 = applicationInfo.loadIcon(packageManager);
			Bitmap bitmap = null;
			try
			{
				bitmap = ((BitmapDrawable) icon2).getBitmap();
			}
			catch (OutOfMemoryError e)
			{
				LogcatTools.error("ApkIconLoader", e.toString());
			}
			appInfo = new AppInfo(apkPath, applicationInfo.uid + "", appName, null, null, applicationInfo.packageName, null, packageInfo.versionName, packageInfo.versionCode, bitmap, AppType.USER.getType(), true, false, false, false, StatusType.DISABLE.getStatus());
		}
		return appInfo;
	}

	/**
	 * 获取内存信息
	 *
	 * @return
	 */
	@Override
	public String getMemoryInfo()
	{
		MemoryInfo memoryInfo = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo outMemoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(outMemoryInfo);
		memoryInfo.setUnusedMemory(outMemoryInfo.availMem);
		if (isCompatible(16))
		{
			memoryInfo.setTotalMemory(outMemoryInfo.totalMem);
		}
		else
		{
		}
		String json = new ReturnInfo(ErrorInfo.SUCCESS, GsonTools.toJson(memoryInfo)).toString();
		LogcatTools.debug("getMemoryInfo", "json:" + json);
		return json;
	}
}
