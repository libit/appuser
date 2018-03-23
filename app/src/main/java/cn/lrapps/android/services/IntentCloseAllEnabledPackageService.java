/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.services;

import android.app.IntentService;
import android.content.Intent;

import cn.lrapps.android.ui.ActivityThread;

/**
 * 关闭所有黑名单
 */
public class IntentCloseAllEnabledPackageService extends IntentService
{
	public IntentCloseAllEnabledPackageService()
	{
		super("closeAllEnabledPackage");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		new ActivityThread(this).closeBlackAppsThread(false);
		stopSelf();
	}
}
