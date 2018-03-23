/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import cn.lrapps.models.AppInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;

public class ActivityLauncher extends Activity
{
	public static final String CLOSE_ALL = "clearAll";
	public static final String CLOSE = "close";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		finish();
		MyApplication.getInstance().backToHome();//关闭时会启动主程序，所以先回到桌面
		Intent intent = getIntent();
		String packageName = intent.getStringExtra(ConstValues.DATA_PACKAGE_NAME);
		String action = intent.getStringExtra(ConstValues.DATA_ACTION);
		if (!StringTools.isNull(packageName))
		{
			if (StringTools.isNull(action))
			{
				if (packageName.equals(CLOSE_ALL))// 清除按钮
				{
					new ActivityThread(this).closeBlackAppsThread(false);
				}
				else//普通的启动程序
				{
					AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
					AppFactory.getInstance().enableApp(packageName, false);
					//					ReturnInfo returnInfo = AppFactory.getInstance().enableApp(packageName, false);
					//					if (ReturnInfo.isSuccess(returnInfo))
					//					{
					ReturnInfo returnInfo = AppFactory.getInstance().startApp(this, packageName);
					if (!ReturnInfo.isSuccess(returnInfo))
					{
						Toast.makeText(this, appInfo.getName() + "不能启动！", Toast.LENGTH_LONG).show();
					}
					//						EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_ENABLED, Arrays.asList(packageName)));
					//					}
					//					else
					//					{
					//						Toast.makeText(this, appInfo.getName() + "启动失败：" + returnInfo.getMsg() + "！", Toast.LENGTH_LONG).show();
					//					}
				}
			}
			else if (action.equals(CLOSE))
			{
				AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
				ReturnInfo returnInfo = AppFactory.getInstance().disableApp(packageName);
				if (ReturnInfo.isSuccess(returnInfo))
				{
					Toast.makeText(this, appInfo.getName() + "已禁止！", Toast.LENGTH_LONG).show();
					//					EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_DISABLED, Arrays.asList(packageName)));
				}
				else
				{
					Toast.makeText(this, appInfo.getName() + "禁止失败：" + returnInfo.getMsg() + "！", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
