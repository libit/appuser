/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import cn.lrapps.models.AppInfo;
import cn.lrapps.enums.StatusType;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;

/**
 * Created by libit on 2017/9/26.
 */
public class AppService extends Service
{
	private static final String TAG = AppService.class.getSimpleName();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		LogcatTools.debug(TAG, "AppService onStartCommand");
		String packageName = intent.getStringExtra(ConstValues.DATA_PACKAGE_NAME);
		int status = intent.getIntExtra(ConstValues.DATA_APP_STATUS, StatusType.ENABLE.getStatus());
		LogcatTools.debug(TAG, "packageName:" + packageName + ",status:" + status);
		if (!StringTools.isNull(packageName))
		{
			AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
			if (status == StatusType.ENABLE.getStatus() || status == StatusType.START.getStatus())
			{
				ReturnInfo returnInfo = AppFactory.getInstance().disableApp(packageName);
				if (!ReturnInfo.isSuccess(returnInfo))
				{
					Toast.makeText(this, appInfo.getName() + "启用失败：" + returnInfo.getMsg() + "！", Toast.LENGTH_LONG).show();
				}
				if (status == StatusType.START.getStatus())
				{
					returnInfo = AppFactory.getInstance().startApp(this, packageName);
					if (!ReturnInfo.isSuccess(returnInfo))
					{
						Toast.makeText(this, appInfo.getName() + "不能启动！", Toast.LENGTH_LONG).show();
					}
				}
			}
			else if (status == StatusType.DISABLE.getStatus())
			{
				ReturnInfo returnInfo = AppFactory.getInstance().disableApp(packageName);
				if (ReturnInfo.isSuccess(returnInfo))
				{
					Toast.makeText(this, appInfo.getName() + "已禁止！", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(this, appInfo.getName() + "禁止失败：" + returnInfo.getMsg() + "！", Toast.LENGTH_LONG).show();
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}
