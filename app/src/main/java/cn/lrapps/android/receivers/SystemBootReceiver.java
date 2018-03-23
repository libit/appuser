/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.lrapps.android.services.NotificationService;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.StringTools;

/**
 * 开机启动接收者
 *
 * @author libit
 */
public class SystemBootReceiver extends BroadcastReceiver
{
	private static final String TAG = SystemBootReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if (!StringTools.isNull(action))
		{
			if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
			{
				LogcatTools.debug(TAG, "开机启动事件");
				if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_BOOT_START))
				{
					context.startService(new Intent(context, NotificationService.class));
					//					context.startActivity(new Intent(context, ActivityMain.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					//					ActivityMain activityMain = ActivityMain.getInstance();
					//					if (activityMain != null)
					//					{
					//						activityMain.onBackPressed();
					//					}
					//					context.startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}
			}
		}
	}
}
