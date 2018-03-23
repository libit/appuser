/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.services;

import android.app.IntentService;
import android.content.Intent;

import cn.lrapps.enums.StatusType;
import cn.lrapps.events.AppEvent;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 切换状态栏点击事件
 */
public class IntentSwitchNotificationClickService extends IntentService
{
	private static final String TAG = IntentSwitchNotificationClickService.class.getSimpleName();

	public IntentSwitchNotificationClickService()
	{
		super("IntentSwitchNotificationClick");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		LogcatTools.debug(TAG, "onHandleIntent:" + intent);
		int check = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.NOTIFICATION_SWITCH_CLICK);
		if (check == StatusType.ENABLE.getStatus())
		{
			PreferenceUtils.getInstance().setStringValue(PreferenceUtils.NOTIFICATION_SWITCH_CLICK, StatusType.DISABLE.getStatus() + "");
		}
		else
		{
			PreferenceUtils.getInstance().setStringValue(PreferenceUtils.NOTIFICATION_SWITCH_CLICK, StatusType.ENABLE.getStatus() + "");
		}
		EventBus.getDefault().post(new AppEvent(AppEvent.GET_RUNNING_APP, null));
		stopSelf();
	}
}
