/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.utils.apptools;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;

import cn.lrapps.android.ui.MyApplication;

/**
 * Created by libit on 2017/9/17.
 */
public class SystemToolsFactoryImpl extends SystemToolsFactory
{
	/**
	 * 获取程序的版本名称
	 *
	 * @return 版本名称
	 */
	@Override
	public String getVersionName()
	{
		try
		{
			PackageManager packageManager = MyApplication.getContext().getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(MyApplication.getContext().getPackageName(), 0);
			return packageInfo.versionName;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
			return "未知";
		}
	}

	/**
	 * 获取程序的版本号
	 *
	 * @return 版本号
	 */
	@Override
	public int getVersionCode()
	{
		try
		{
			PackageManager packageManager = MyApplication.getContext().getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(MyApplication.getContext().getPackageName(), 0);
			return packageInfo.versionCode;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取程序的证书信息
	 *
	 * @return 证书信息
	 */
	@Override
	public String getCertInfo()
	{
		PackageManager pm = MyApplication.getContext().getPackageManager();
		String packageName = MyApplication.getContext().getPackageName();
		PackageInfo packageInfo;
		try
		{
			packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
		}
		catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
			return "libit";
		}
		Signature[] signatures = packageInfo.signatures;
		String str = "";
		for (int i = 0; i < signatures.length; i++)
		{
			str += signatures[i].toCharsString();
		}
		return str;
	}

	/**
	 * 获取手机设备名称
	 *
	 * @return
	 */
	@Override
	public String getDeviceName()
	{
		return Build.MODEL;
	}

	/**
	 * 获取系统版本号
	 *
	 * @return
	 */
	@Override
	public String getSysVersion()
	{
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取固件版本
	 *
	 * @return
	 */
	public String getOsName()
	{
		return Build.DISPLAY;
	}
}
