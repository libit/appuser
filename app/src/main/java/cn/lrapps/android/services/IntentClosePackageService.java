/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import cn.lrapps.models.AppInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;

/**
 * 关闭黑名单
 */
public class IntentClosePackageService extends IntentService
{
	private static final String TAG = IntentClosePackageService.class.getSimpleName();

	public IntentClosePackageService()
	{
		super("closePackage");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		String packageName = intent.getStringExtra(ConstValues.DATA_PACKAGE_NAME);
		LogcatTools.debug(TAG, "onHandleIntent:" + packageName);
		if (!StringTools.isNull(packageName))
		{
			AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
			ReturnInfo returnInfo = AppFactory.getInstance().disableApp(packageName);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				Toast.makeText(this, appInfo.getName() + "已禁止！", Toast.LENGTH_LONG).show();
				//				EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_CHANGED, Arrays.asList(packageName)));
			}
			else
			{
				Toast.makeText(this, appInfo.getName() + "禁止失败：" + returnInfo.getMsg() + "！", Toast.LENGTH_LONG).show();
			}
		}
		stopSelf();
	}
}
