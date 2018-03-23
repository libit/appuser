/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import cn.lrapps.android.services.IntentCloseAllEnabledPackageService;
import cn.lrapps.android.services.WidgetGridRemoteViewService;
import com.lrcall.appuser.R;
import cn.lrapps.utils.LogcatTools;

/**
 * Created by libit on 15/10/19.
 */
public class CustomerAppWidgetView extends RemoteViews
{
	private static final String TAG = "RemoteViews";//CustomerAppWidgetView.class.getSimpleName();
	public Creator CREATOR;
	private final Context context;

	public CustomerAppWidgetView(Context context, String aPackageName)
	{
		super(aPackageName, R.layout.widget_black_apps);
		this.context = context;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public CustomerAppWidgetView(Context context, int appWidgetId)
	{
		this(context, context.getPackageName());
		LogcatTools.debug(TAG, "CustomerAppWidgetView构造函数：" + this.toString());
		// 设置响应 “按钮(btn_refresh)” 的intent
		Intent btIntent = new Intent().setAction(BlackAppWidgetProvider.BTN_REFRESH_ACTION);
		btIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		setOnClickPendingIntent(R.id.btn_refresh, btPendingIntent);
		LogcatTools.debug(TAG, "刷新按钮绑定事件");
		// 关闭按钮绑定事件
		Intent intent = new Intent(context, IntentCloseAllEnabledPackageService.class);
		//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		setOnClickPendingIntent(R.id.btn_close, contentIntent);
		LogcatTools.debug(TAG, "关闭按钮绑定事件");
		// 设置 “GridView(gridview)” 的adapter。
		// (01) intent: 对应启动 WidgetGridRemoteViewService(RemoteViewsService) 的intent
		// (02) setRemoteAdapter: 设置 gridview的适配器
		//    通过setRemoteAdapter将gridview和GridWidgetService关联起来，
		//    以达到通过 WidgetGridRemoteViewService 更新 gridview 的目的
		Intent serviceIntent = new Intent(context, WidgetGridRemoteViewService.class);
		serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		setRemoteAdapter(R.id.gv_black_apps, serviceIntent);
		// 设置响应 “GridView(gridview)” 的intent模板
		// 说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
		//     它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
		//        (01) 通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
		//        (02) 然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”
		Intent gridIntent = new Intent().setAction(BlackAppWidgetProvider.COLLECTION_VIEW_ACTION);
		gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		//		gridIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gridIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 设置intent模板
		setPendingIntentTemplate(R.id.gv_black_apps, pendingIntent);
		LogcatTools.debug(TAG, "GridView绑定事件");
	}
}
