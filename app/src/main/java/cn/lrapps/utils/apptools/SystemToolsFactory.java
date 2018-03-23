/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.utils.apptools;

/**
 * Created by libit on 2017/9/17.
 */
public abstract class SystemToolsFactory
{
	private static SystemToolsFactory systemToolsFactory;

	synchronized public static SystemToolsFactory getInstance()
	{
		if (systemToolsFactory == null)
		{
			systemToolsFactory = new SystemToolsFactoryImpl();
		}
		return systemToolsFactory;
	}

	/**
	 * 获取程序的版本名称
	 *
	 * @return 版本名称
	 */
	public abstract String getVersionName();

	/**
	 * 获取程序的版本号
	 *
	 * @return 版本号
	 */
	public abstract int getVersionCode();

	/**
	 * 获取程序的证书信息
	 *
	 * @return 证书信息
	 */
	public abstract String getCertInfo();

	/**
	 * 获取手机设备名称
	 *
	 * @return
	 */
	public abstract String getDeviceName();

	/**
	 * 获取系统版本号
	 *
	 * @return
	 */
	public abstract String getSysVersion();

	/**
	 * 获取固件版本
	 *
	 * @return
	 */
	public abstract String getOsName();
}
