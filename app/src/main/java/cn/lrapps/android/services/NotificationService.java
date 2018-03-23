/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.lrapps.android.ui.notification.CustomerNotification;
import cn.lrapps.events.AppEvent;
import cn.lrapps.events.AppListEvent;
import cn.lrapps.utils.LogcatTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by libit on 2017/9/26.
 */
public class NotificationService extends Service
{
	private static final String TAG = NotificationService.class.getSimpleName();
	public CustomerNotification mCustomerNotification;

	@Override
	public void onCreate()
	{
		super.onCreate();
		mCustomerNotification = new CustomerNotification(this);
		LogcatTools.debug(TAG, "创建通知栏");
		showNotify();
		if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		LogcatTools.debug(TAG, "通知栏onStartCommand");
		EventBus.getDefault().post(new AppEvent(AppEvent.GET_RUNNING_APP, null));
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		if (mCustomerNotification != null)
		{
			LogcatTools.debug(TAG, "通知栏被关闭");
			mCustomerNotification.cancelAll();
		}
		if (EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().unregister(this);
		}
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Subscribe
	public void onEventMainThread(final AppListEvent appListEvent)
	{
		if (appListEvent != null)
		{
			if (AppListEvent.GET_RUNNING_APP.equals(appListEvent.getType()))
			{
				mCustomerNotification.notifyApps(appListEvent.getAppInfoList());
			}
			showNotify();
		}
	}

	private void showNotify()
	{
		startForeground(CustomerNotification.BLACK_APP_NOTIF_ID, mCustomerNotification.getNotification());
	}
}
