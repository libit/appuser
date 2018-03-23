/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.utils.viewtools;

import android.content.Context;
import android.util.DisplayMetrics;

import cn.lrapps.utils.LogcatTools;

public class DisplayTools
{
	/**
	 * 获取屏幕宽度
	 *
	 * @param context
	 *
	 * @return
	 */
	public static int getWindowWidth(Context context)
	{
		final float width = context.getResources().getDisplayMetrics().widthPixels;
		LogcatTools.debug("getWindowWidth", "width:" + width);
		return (int) (width);
	}

	/**
	 * 获取屏幕高度
	 *
	 * @param context
	 *
	 * @return
	 */
	public static int getWindowHeight(Context context)
	{
		final float height = context.getResources().getDisplayMetrics().heightPixels;
		LogcatTools.debug("getWindowHeight", "height:" + height);
		return (int) (height);
	}

	/**
	 * dip转px
	 *
	 * @param context
	 * @param dipValue
	 *
	 * @return
	 */
	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px转dip
	 *
	 * @param context
	 * @param pxValue
	 *
	 * @return
	 */
	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取系统的DPI
	 *
	 * @param context
	 *
	 * @return
	 */
	public static int getDpi(Context context)
	{
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int densityDpi = dm.densityDpi;
		return densityDpi;
	}
}
