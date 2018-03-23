/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.reflect.TypeToken;
import cn.lrapps.android.ui.ActivityLauncher;
import cn.lrapps.android.ui.ActivityMain;
import com.lrcall.appuser.R;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by libit on 16/4/16.
 */
public class BlackAppWidgetProvider extends AppWidgetProvider
{
	public static final String BTN_REFRESH_ACTION = "com.lrcall.action.widget_refresh_action";
	public static final String COLLECTION_VIEW_ACTION = "com.lrcall.action.widget_grid_view_action";
	public static final String COLLECTION_VIEW_EXTRA = "com.lrcall.action.collection_view_extra";
	private static final String TAG = "RemoteViews";//BlackAppWidgetProvider.class.getSimpleName();
	private static final Map<Integer, CustomerAppWidgetView> map = new HashMap<>();

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		Log.d(TAG, "BlackAppWidgetProvider onReceive : " + intent.getAction());
		if (action.equals(COLLECTION_VIEW_ACTION))
		{
			// 接收“gridview”的点击事件的广播
			//			int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			//			int viewIndex = intent.getIntExtra(COLLECTION_VIEW_EXTRA, 0);
			String packageName = intent.getStringExtra(ConstValues.DATA_PACKAGE_NAME);
			String myPackageName = null;
			try
			{
				myPackageName = AppFactory.getInstance().getSelfPackageInfo().packageName;
			}
			catch (PackageManager.NameNotFoundException e)
			{
				e.printStackTrace();
			}
			if (!StringTools.isNull(myPackageName) && packageName.equals(myPackageName))
			{
				context.startActivity(new Intent(context, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
			else
			{
				Intent i = new Intent(context, ActivityLauncher.class);
				i.putExtra(ConstValues.DATA_PACKAGE_NAME, packageName);
				i.putExtra(ConstValues.DATA_ACTION, ActivityLauncher.CLOSE);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		}
		else if (action.equals(BTN_REFRESH_ACTION))
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			finally
			{
				// 接收“btn_refresh”的点击事件的广播
				LogcatTools.debug("RemoteViews", "接收“btn_refresh”的点击事件的广播");
				int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
				if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
				{
					appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.gv_black_apps);
					LogcatTools.debug("RemoteViews", "appWidgetId:" + appWidgetId);
				}
				synchronized (map)
				{
					Set<Integer> widgetIds = GsonTools.getObjects(PreferenceUtils.getInstance().getStringValue(PreferenceUtils.APP_WIDGET_INFOS), new TypeToken<Set<Integer>>()
					{
					}.getType());
					if (widgetIds != null)
					{
						for (Integer widgetId : widgetIds)
						{
							map.put(widgetId, null);
						}
					}
					for (Integer aid : map.keySet())
					{
						if (aid == appWidgetId)
						{
							continue;
						}
						appWidgetManager.notifyAppWidgetViewDataChanged(aid, R.id.gv_black_apps);
						LogcatTools.debug("RemoteViews", "aid:" + aid);
					}
				}
			}
		}
		super.onReceive(context, intent);
	}

	/**
	 * Called in response to the {@link AppWidgetManager#ACTION_APPWIDGET_UPDATE} and
	 * {@link AppWidgetManager#ACTION_APPWIDGET_RESTORED} broadcasts when this AppWidget
	 * provider is being asked to provide {@link RemoteViews RemoteViews}
	 * for a set of AppWidgets.  Override this method to implement your own AppWidget functionality.
	 * <p/>
	 * {@more}
	 * 更新部件时调用，在第1次添加部件时也会调用
	 *
	 * @param context          The {@link Context Context} in which this receiver is
	 *                         running.
	 * @param appWidgetManager A {@link AppWidgetManager} object you can call {@link
	 *                         AppWidgetManager#updateAppWidget} on.
	 * @param appWidgetIds     The appWidgetIds for which an update is needed.  Note that this
	 *                         may be all of the AppWidget instances for this provider, or just
	 *                         a subset of them.
	 *
	 * @see AppWidgetManager#ACTION_APPWIDGET_UPDATE
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		Log.d(TAG, "onUpdate被调用,appWidgetManager:" + appWidgetManager.toString());
		synchronized (map)
		{
			Set<Integer> widgetIds = GsonTools.getObjects(PreferenceUtils.getInstance().getStringValue(PreferenceUtils.APP_WIDGET_INFOS), new TypeToken<Set<Integer>>()
			{
			}.getType());
			if (widgetIds != null)
			{
				for (Integer widgetId : widgetIds)
				{
					map.put(widgetId, null);
				}
			}
			for (int appWidgetId : appWidgetIds)
			{
				Log.d(TAG, "onUpdate被调用,appWidgetId:" + appWidgetId);
				// 获取AppWidget对应的视图
				CustomerAppWidgetView rv = new CustomerAppWidgetView(context, appWidgetId);
				// 调用集合管理器对集合进行更新
				appWidgetManager.updateAppWidget(appWidgetId, rv);
				map.put(appWidgetId, rv);
			}
			PreferenceUtils.getInstance().setStringValue(PreferenceUtils.APP_WIDGET_INFOS, GsonTools.toJson(map.keySet()));
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/**
	 * Called in response to the {@link AppWidgetManager#ACTION_APPWIDGET_DELETED} broadcast when
	 * one or more AppWidget instances have been deleted.  Override this method to implement
	 * your own AppWidget functionality.
	 * <p/>
	 * {@more}
	 * 部件从host中删除
	 *
	 * @param context      The {@link Context Context} in which this receiver is
	 *                     running.
	 * @param appWidgetIds The appWidgetIds that have been deleted from their host.
	 *
	 * @see AppWidgetManager#ACTION_APPWIDGET_DELETED
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		super.onDeleted(context, appWidgetIds);
		synchronized (map)
		{
			for (int aid : appWidgetIds)
			{
				map.remove(aid);
			}
			PreferenceUtils.getInstance().setStringValue(PreferenceUtils.APP_WIDGET_INFOS, GsonTools.toJson(map.keySet()));
		}
	}

	/**
	 * Called in response to the {@link AppWidgetManager#ACTION_APPWIDGET_ENABLED} broadcast when
	 * the a AppWidget for this provider is instantiated.  Override this method to implement your
	 * own AppWidget functionality.
	 * <p/>
	 * {@more}
	 * When the last AppWidget for this provider is deleted,
	 * {@link AppWidgetManager#ACTION_APPWIDGET_DISABLED} is sent by the AppWidget manager, and
	 * {@link #onDisabled} is called.  If after that, an AppWidget for this provider is created
	 * again, onEnabled() will be called again.
	 * 第1次创建时调用，之后再创建不会调用
	 *
	 * @param context The {@link Context Context} in which this receiver is
	 *                running.
	 *
	 * @see AppWidgetManager#ACTION_APPWIDGET_ENABLED
	 */
	@Override
	public void onEnabled(Context context)
	{
		Log.d(TAG, "onEnabled 被调用");
		super.onEnabled(context);
	}

	/**
	 * Called in response to the {@link AppWidgetManager#ACTION_APPWIDGET_DISABLED} broadcast, which
	 * is sent when the last AppWidget instance for this provider is deleted.  Override this method
	 * to implement your own AppWidget functionality.
	 * <p/>
	 * {@more}
	 * 当最后一个部件实例 被删除时 调用  用于清除onEnabled执行的操作
	 *
	 * @param context The {@link Context Context} in which this receiver is
	 *                running.
	 *
	 * @see AppWidgetManager#ACTION_APPWIDGET_DISABLED
	 */
	@Override
	public void onDisabled(Context context)
	{
		Log.d(TAG, "onDisabled 被调用");
		super.onDisabled(context);
		synchronized (map)
		{
			map.clear();
			PreferenceUtils.getInstance().setStringValue(PreferenceUtils.APP_WIDGET_INFOS, GsonTools.toJson(map.keySet()));
		}
	}
}
