/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import cn.lrapps.models.AppInfo;

/**
 * APP状态变化接口
 * Created by libit on 15/8/26.
 */
public interface OnAppStatusListener
{
	// App被禁用
	//     boolean OnDisabledApp(final AppInfo appInfo);
	// App被启用
	//     boolean OnEnabledApp(final AppInfo appInfo);

	/**
	 * App的状态发生变化
	 *
	 * @param appInfo App信息,如果有多个App发生变化,则参数设为null
	 *
	 * @return 是否处理
	 */
	boolean OnAppStatusChanged(final AppInfo appInfo);
}
