/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.AppBlackStatus;
import cn.lrapps.enums.AppEnableStatus;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by libit on 16/4/17.
 */
@TargetApi(11)
public class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
	private static final String TAG = "RemoteViews";//GridRemoteViewsFactory.class.getSimpleName();
	private final Context mContext;
	private final int mAppWidgetId;
	private final List<AppInfo> appInfoList = new ArrayList<>();
	private final ExecutorService executorService = Executors.newFixedThreadPool(1);
	private Boolean needUpdate = true;

	public GridRemoteViewsFactory(Context context, Intent intent)
	{
		this.mContext = context;
		this.mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		LogcatTools.debug(TAG, "GridRemoteViewsFactory构造函数");
	}

	/**
	 * 初始化GridView的数据
	 *
	 * @author skywang
	 */
	private void initGridViewData()
	{
		List<AppInfo> blackAppInfos = DbAppFactory.getInstance().getAppInfoList(AppEnableStatus.ENABLED.getStatus(), null, null, AppBlackStatus.BLACK.getStatus(), null);
		synchronized (appInfoList)
		{
			appInfoList.clear();
			try
			{
				AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(AppFactory.getInstance().getSelfPackageInfo().packageName, true);
				if (appInfo != null && !StringTools.isNull(appInfo.getPackageName()))
				{
					appInfo.setName("黑名单");
					appInfoList.add(appInfo);
				}
			}
			catch (PackageManager.NameNotFoundException e)
			{
				e.printStackTrace();
			}
			if (blackAppInfos != null && blackAppInfos.size() > 0)
			{
				for (AppInfo blackAppInfo : blackAppInfos)
				{
					AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(blackAppInfo.getPackageName(), true);
					if (appInfo != null && !StringTools.isNull(appInfo.getPackageName()))
					{
						appInfoList.add(appInfo);
					}
				}
			}
		}
	}

	@Override
	public void onCreate()
	{
		LogcatTools.debug(TAG, "初始化“集合视图”中的数据");
		// 初始化“集合视图”中的数据
		initGridViewData();
		//		updateData();
	}

	/**
	 * Called when notifyDataSetChanged() is triggered on the remote adapter. This allows a
	 * RemoteViewsFactory to respond to appInfoList changes by updating any internal references.
	 * <p/>
	 * Note: expensive tasks can be safely performed synchronously within this method. In the
	 * interim, the old appInfoList will be displayed within the widget.
	 *
	 * @see AppWidgetManager#notifyAppWidgetViewDataChanged(int[], int)
	 */
	@Override
	public void onDataSetChanged()
	{
		LogcatTools.debug(TAG, "集合视图数据变化");
		synchronized (needUpdate)
		{
			if (needUpdate)
			{
				needUpdate = false;
				executorService.submit(new Thread("updateData")
				{
					@Override
					public void run()
					{
						super.run();
						LogcatTools.debug(TAG, "GridRemoteViewsFactory onDataSetChanged开始更新,时间:" + StringTools.getCurrentTime());
						initGridViewData();
						Intent intent = new Intent().setAction(BlackAppWidgetProvider.BTN_REFRESH_ACTION);
						intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
						mContext.sendBroadcast(intent);
					}
				});
			}
			else
			{
				needUpdate = true;
				LogcatTools.debug(TAG, "GridRemoteViewsFactory onDataSetChanged停止更新,时间:" + StringTools.getCurrentTime());
			}
		}
	}

	@Override
	public void onDestroy()
	{
		LogcatTools.debug(TAG, "集合视图已Destory");
		executorService.shutdown();
	}

	@Override
	public int getCount()
	{
		// 返回“集合视图”中的数据的总数
		return appInfoList.size();
	}

	@Override
	public RemoteViews getViewAt(int position)
	{
		// 获取 grid_view_item.xml 对应的RemoteViews
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_app);
		// 设置 第position位的“视图”的数据
		// 设置 第position位的“视图”对应的响应事件
		Intent fillInIntent = new Intent();
		fillInIntent.putExtra(BlackAppWidgetProvider.COLLECTION_VIEW_EXTRA, position);
		synchronized (appInfoList)
		{
			//			if (position == 0)
			//			{
			//				rv.setImageViewResource(R.id.iv_head, R.mipmap.ic_launcher);
			//				rv.setTextViewText(R.id.tv_label, mContext.getString(R.string.app_name));
			//			}
			//			else
			{
				if (position >= appInfoList.size())
				{
					return null;
				}
				AppInfo appInfo = appInfoList.get(position);
				rv.setImageViewBitmap(R.id.iv_head, appInfo.getPhoto());
				rv.setTextViewText(R.id.tv_label, appInfo.getName());
				fillInIntent.putExtra(ConstValues.DATA_PACKAGE_NAME, appInfo.getPackageName());
			}
		}
		rv.setOnClickFillInIntent(R.id.layout_app, fillInIntent);
		return rv;
	}

	/**
	 * This allows for the use of a custom loading view which appears between the time that
	 * {@link #getViewAt(int)} is called and returns. If null is returned, a default loading
	 * view will be used.
	 *
	 * @return The RemoteViews representing the desired loading view.
	 */
	@Override
	public RemoteViews getLoadingView()
	{
		return null;
	}

	@Override
	public int getViewTypeCount()
	{
		// 只有一类 GridView
		return 1;
	}

	@Override
	public long getItemId(int position)
	{
		// 返回当前项在“集合视图”中的位置
		return position;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}
}
