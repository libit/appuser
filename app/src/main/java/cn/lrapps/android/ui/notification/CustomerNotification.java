/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import cn.lrapps.android.ui.ActivityMain;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.utils.apptools.AppFactory;

import java.util.List;

/**
 * Created by libit on 15/10/19.
 */
public class CustomerNotification
{
	private static final String TAG = CustomerNotification.class.getSimpleName();
	public static final int BLACK_APP_NOTIF_ID = 1;
	private static boolean isInit = false;
	private final NotificationManager notificationManager;
	private Notification mNotification;
	private final Context context;

	public CustomerNotification(Context context)
	{
		this.context = context;
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (!isInit)
		{
			cancelAll();
			isInit = true;
		}
	}

	public synchronized Notification notifyApps(List<AppInfo> appInfos)
	{
		int icon = R.drawable.notification;
		CharSequence tickerText = context.getString(R.string.app_name);
		long when = System.currentTimeMillis();
		NotificationCompat.Builder nb = new NotificationCompat.Builder(context).setSmallIcon(icon).setTicker(tickerText).setWhen(when).setOngoing(true).setOnlyAlertOnce(true).setAutoCancel(false).setLocalOnly(true);
		Intent notificationIntent = new Intent(context, ActivityMain.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		nb.setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		//		nb.setDeleteIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		CustomerNotificationRemoteView contentView = new CustomerNotificationRemoteView(context);
		contentView.clear();
		contentView.addAppInfos(appInfos);
		nb.setContent(contentView);
		//		CustomerNotificationBigRemoteView bigContentView = new CustomerNotificationBigRemoteView(context);
		//		bigContentView.clear();
		//		bigContentView.addAppInfos(appInfos);
		//		nb.setCustomBigContentView(bigContentView);
		mNotification = nb.build();
		mNotification.flags |= Notification.FLAG_NO_CLEAR;
		// We have to re-write content view because getNotification setLatestEventInfo implicitly
		if (!AppFactory.isCompatible(Build.VERSION_CODES.N))
		{
			mNotification.contentView = contentView;
			//			if (AppFactory.isCompatible(Build.VERSION_CODES.JELLY_BEAN))
			//			{
			//				notification.bigContentView = bigContentView;
			//			}
		}
		//		try
		//		{
		//			notificationManager.notify(BLACK_APP_NOTIF_ID, mNotification);
		//		}
		//		catch (Exception e)
		//		{
		//			LogcatTools.error(TAG, "状态栏提示出错！" + e.getMessage());
		//			e.printStackTrace();
		//		}
		return mNotification;
	}

	public synchronized Notification getNotification()
	{
		if (mNotification != null)
		{
			return mNotification;
		}
		int icon = R.drawable.notification;
		CharSequence tickerText = context.getString(R.string.app_name);
		long when = System.currentTimeMillis();
		NotificationCompat.Builder nb = new NotificationCompat.Builder(context).setSmallIcon(icon).setTicker(tickerText).setWhen(when).setOngoing(true).setOnlyAlertOnce(true).setAutoCancel(false).setLocalOnly(true);
		Intent notificationIntent = new Intent(context, ActivityMain.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		nb.setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		CustomerNotificationRemoteView contentView = new CustomerNotificationRemoteView(context);
		contentView.clear();
		contentView.addAppInfos(null);
		nb.setContent(contentView);
		Notification notification = nb.build();
		notification.flags |= Notification.FLAG_NO_CLEAR;
		if (!AppFactory.isCompatible(Build.VERSION_CODES.N))
		{
			notification.contentView = contentView;
		}
		mNotification = notification;
		return mNotification;
	}

	// Cancels
	public final void cancelApps()
	{
		notificationManager.cancel(BLACK_APP_NOTIF_ID);
	}

	public final void cancelAll()
	{
		cancelApps();
	}
}
