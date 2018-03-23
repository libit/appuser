/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.RemoteViews;

import cn.lrapps.android.services.IntentCloseAllEnabledPackageService;
import cn.lrapps.android.services.IntentSwitchNotificationClickService;
import cn.lrapps.android.ui.ActivityMain;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.AppBlackStatus;
import cn.lrapps.enums.AppEnableStatus;
import cn.lrapps.enums.StatusType;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.apptools.AppFactory;

import java.util.List;

/**
 * Created by libit on 15/10/19.
 */
public class CustomerNotificationRemoteView extends RemoteViews
{
	private static final String TAG = CustomerNotificationRemoteView.class.getSimpleName();
	private final Context context;
	public static final Parcelable.Creator<CustomerNotificationRemoteView> CREATOR = new Parcelable.Creator<CustomerNotificationRemoteView>()
	{
		public CustomerNotificationRemoteView createFromParcel(Parcel in)
		{
			return new CustomerNotificationRemoteView(in);
		}

		public CustomerNotificationRemoteView[] newArray(int size)
		{
			return new CustomerNotificationRemoteView[size];
		}
	};

	private CustomerNotificationRemoteView(Parcel in)
	{
		super(in);
		context = in.readParcelable(Context.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);
		LogcatTools.debug(TAG, "写入Parcel");
		//		dest.writeValue(context);
	}

	public CustomerNotificationRemoteView(Context context, String aPackageName)
	{
		//		super(aPackageName, R.layout.notification_black_apps);
		super(aPackageName, R.layout.notification_app_bar);
		this.context = context;
	}

	public CustomerNotificationRemoteView(Context context)
	{
		this(context, context.getPackageName());
		//切换按钮绑定事件
		setOnClickPendingIntent(R.id.btn_switch, PendingIntent.getService(context, 0, new Intent(context, IntentSwitchNotificationClickService.class), PendingIntent.FLAG_UPDATE_CURRENT));
		// 关闭按钮绑定事件
		Intent intent = new Intent(context, IntentCloseAllEnabledPackageService.class);
		//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		setOnClickPendingIntent(R.id.btn_close, contentIntent);
		// 进入APP按钮绑定事件
		setOnClickPendingIntent(R.id.btn_app, PendingIntent.getActivity(context, 0, new Intent(context, ActivityMain.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT));
	}

	public void clear()
	{
		removeAllViews(R.id.layout_apps);
	}

	public void addAppInfos(List<AppInfo> appInfos)
	{
		List<AppInfo> appInfoList = DbAppFactory.getInstance().getAppInfoList(AppEnableStatus.ENABLED.getStatus(), null, null, AppBlackStatus.BLACK.getStatus(), null);
		int MAX = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_PORTRAIT) - 1;
		if (MAX < 1)
		{
			MAX = 4;
		}
		boolean b = false;
		if (appInfoList != null && appInfoList.size() > 0)
		{
			setTextViewText(R.id.tv_label, "黑名单");
			setViewVisibility(R.id.line2, View.VISIBLE);
			setViewVisibility(R.id.btn_close, View.VISIBLE);
			int i = 0;
			for (AppInfo appInfo : appInfoList)
			{
				LogcatTools.debug("AppInfo", "AppInfo:" + appInfo.toString());
				b = true;
				appInfo = AppFactory.getInstance().getAppInfoByPackageName(appInfo.getPackageName(), true);
				CustomerNotificationRemoteViewItem cr = new CustomerNotificationRemoteViewItem(context, appInfo);
				addView(R.id.layout_apps, cr);
				i++;
				if (i >= MAX)
				{
					break;
				}
			}
		}
		if (!b)
		{
			PreferenceUtils.getInstance().setStringValue(PreferenceUtils.NOTIFICATION_SWITCH_CLICK, StatusType.ENABLE.getStatus() + "");
			setTextViewText(R.id.tv_label, "运行应用");
			setViewVisibility(R.id.layout_func, View.GONE);
			int i = 0;
			if (appInfos != null && appInfos.size() > 0)
			{
				for (AppInfo appInfo : appInfos)
				{
					CustomerNotificationRemoteViewItem cr = new CustomerNotificationRemoteViewItem(context, appInfo);
					addView(R.id.layout_apps, cr);
					i++;
					if (i > MAX)
					{
						break;
					}
				}
			}
		}
		else
		{
			setViewVisibility(R.id.layout_func, View.VISIBLE);
			//			String check = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.NOTIFICATION_SWITCH_CLICK);
			//			if (check.equals(PreferenceUtils.ENABLE))
			//			{
			//				setImageViewResource(R.id.btn_switch, R.drawable.btn_checked);
			//			}
			//			else
			//			{
			//				setImageViewResource(R.id.btn_switch, R.drawable.btn_nocheck);
			//			}
		}
	}
}
