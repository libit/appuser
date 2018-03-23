/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import cn.lrapps.utils.LogcatTools;

/**
 * Created by libit on 15/8/19.
 */
public class MyApplication extends Application
{
	private static MyApplication instance;

	public static MyApplication getInstance()
	{
		return instance;
	}

	public static Context getContext()
	{
		return getInstance();
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
		new Thread("startLogService")
		{
			@Override
			public void run()
			{
				super.run();
				LogcatTools.getInstance().start();
			}
		}.start();
	}

	@Override
	public void onTerminate()
	{
		LogcatTools.getInstance().stop();
		instance = null;
		super.onTerminate();
	}

	public void backToHome()
	{
		try
		{
			startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
