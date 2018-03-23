/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.services;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

import cn.lrapps.android.ui.widget.GridRemoteViewsFactory;
import cn.lrapps.utils.LogcatTools;

/**
 * Created by libit on 16/4/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetGridRemoteViewService extends RemoteViewsService
{
	private static final String TAG = "RemoteViews";//WidgetGridRemoteViewService.class.getSimpleName();

	/**
	 * To be implemented by the derived service to generate appropriate factories for
	 * the data.
	 *
	 * @param intent
	 */
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent)
	{
		LogcatTools.debug(TAG, "WidgetGridRemoteViewService:" + this.toString());
		return new GridRemoteViewsFactory(this, intent);
	}
}
