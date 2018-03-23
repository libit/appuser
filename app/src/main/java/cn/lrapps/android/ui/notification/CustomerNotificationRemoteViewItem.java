/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.notification;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.RemoteViews;

import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;

import java.util.List;

/**
 * Created by libit on 15/10/20.
 */
public class CustomerNotificationRemoteViewItem extends RemoteViews
{
	private static final String TAG = CustomerNotificationRemoteViewItem.class.getSimpleName();
	public static final Parcelable.Creator<CustomerNotificationRemoteViewItem> CREATOR = new Parcelable.Creator<CustomerNotificationRemoteViewItem>()
	{
		public CustomerNotificationRemoteViewItem createFromParcel(Parcel in)
		{
			LogcatTools.debug(TAG, "从Parcel创建对象");
			return new CustomerNotificationRemoteViewItem(in);
		}

		public CustomerNotificationRemoteViewItem[] newArray(int size)
		{
			LogcatTools.debug(TAG, "从Parcel创建对象数组");
			return new CustomerNotificationRemoteViewItem[size];
		}
	};

	public CustomerNotificationRemoteViewItem(Parcel in)
	{
		super(in);
		LogcatTools.debug(TAG, "Parcel的构造函数");
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);
		LogcatTools.debug(TAG, "写入Parcel");
	}

	@Override
	public boolean onLoadClass(Class clazz)
	{
		LogcatTools.debug(TAG, "加载Class");
		return clazz.isAnnotationPresent(CustomerNotificationRemoteViewItem.class);
	}

	public CustomerNotificationRemoteViewItem(String aPackageName)
	{
		super(aPackageName, R.layout.item_notification_app);
	}

	public CustomerNotificationRemoteViewItem(Context context, AppInfo appInfo)
	{
		this(context.getPackageName());
		Bitmap bmp = appInfo.getPhoto();
		init(context, bmp, appInfo.getPackageName());
	}

	private void init(Context context, Bitmap bmp, String packageName)
	{
		if (bmp != null)
		{
			setImageViewBitmap(R.id.iv_head, bmp);
		}
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo = null;
		try
		{
			packageInfo = packageManager.getPackageInfo(packageName, 0);
		}
		catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
			return;
		}
		if (packageInfo == null)
		{
			return;
		}
		String check = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.NOTIFICATION_SWITCH_CLICK);
		//		if (check.equals(PreferenceUtils.ENABLE))
		{
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setPackage(packageName);
			List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
			while (resolveInfoList.iterator().hasNext())
			{
				ResolveInfo resolveInfo = resolveInfoList.iterator().next();
				if (resolveInfo != null)
				{
					String className = resolveInfo.activityInfo.name;
					intent.setComponent(new ComponentName(packageName, className));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					setOnClickPendingIntent(R.id.iv_head, contentIntent);
					break;
				}
			}
		}
		//		else
		//		{
		//			Intent intent = new Intent().setAction(BlackAppWidgetProvider.COLLECTION_VIEW_ACTION);
		//			intent.putExtra(ConstValues.DATA_PACKAGE_NAME, packageName);
		//			setOnClickPendingIntent(R.id.iv_head, PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
		//		}
	}
}
