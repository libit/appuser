/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.androidquery.AQuery;
import cn.lrapps.android.ui.MyApplication;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.viewtools.BitmapTools;
import cn.lrapps.utils.viewtools.DisplayTools;

/**
 * 图片服务类
 * Created by libit on 16/4/6.
 */
public class PicService extends BaseService
{
	public PicService(Context context)
	{
		super(context);
	}

	/**
	 * 获取图片
	 *
	 * @param imageView 图片控件
	 * @param url       图片地址
	 * @param width     图片宽度
	 */
	public static void ajaxGetPic(ImageView imageView, String url, int width)
	{
		AQuery aQuery = new AQuery(imageView);
		Bitmap bitmap = aQuery.getCachedImage(url, width);
		if (bitmap != null)
		{
			LogcatTools.debug("ajaxGetPic", "从缓存读取图片:" + url);
			imageView.setImageBitmap(bitmap);
		}
		else
		{
			LogcatTools.debug("ajaxGetPic", "图片地址:" + url);
			aQuery.image(url, false, true);
		}
	}

	/**
	 * 获取图片，并设置成圆形
	 *
	 * @param imageView 图片控件
	 * @param url       图片地址
	 * @param width     图片宽度
	 */
	public static void ajaxGetRoundPic(ImageView imageView, String url, int width)
	{
		ajaxGetRoundPic(imageView, url, width, null);
	}

	public static void ajaxGetRoundPic(ImageView imageView, String url, int width, Integer defaultImageRes)
	{
		if (defaultImageRes != null)
		{
			imageView.setImageResource(defaultImageRes);
		}
		AQuery aQuery = new AQuery(imageView);
		Bitmap bitmap = aQuery.getCachedImage(url, width);
		if (bitmap != null)
		{
			LogcatTools.debug("ajaxGetRoundPic", "从缓存读取图片:" + url);
			bitmap = BitmapTools.createRoundConerImage(bitmap, width, width, 50 * width / DisplayTools.getWindowWidth(MyApplication.getContext()), 50 * width / DisplayTools.getWindowWidth(MyApplication.getContext()));
			imageView.setImageBitmap(bitmap);
		}
		else
		{
			LogcatTools.debug("ajaxGetRoundPic", "图片地址:" + url);
			if (defaultImageRes != null)
			{
				aQuery.image(url, false, true, width, defaultImageRes);
			}
			else
			{
				aQuery.image(url, false, true, 50 * width / DisplayTools.getWindowWidth(MyApplication.getContext()));
			}
		}
	}

	/**
	 * 获取图片，并设置成圆形
	 *
	 * @param imageView 图片控件
	 * @param url       图片地址
	 * @param width     图片宽度
	 */
	public static void ajaxGetRoundPic(ImageView imageView, String url, int width, int height, int radiusX, int radiusY)
	{
		AQuery aQuery = new AQuery(imageView);
		Bitmap bitmap = aQuery.getCachedImage(url, width);
		if (bitmap != null)
		{
			LogcatTools.debug("ajaxGetRoundPic", "从缓存读取图片:" + url);
			bitmap = BitmapTools.createRoundConerImage(bitmap, width, height, radiusX, radiusY);
			imageView.setImageBitmap(bitmap);
		}
		else
		{
			LogcatTools.debug("ajaxGetRoundPic", "图片地址:" + url);
			aQuery.image(url, false, true);
		}
	}

	/**
	 * 获取图片，并设置成圆形
	 *
	 * @param imageView 图片控件
	 * @param url       图片地址
	 * @param width     图片宽度
	 */
	public static void ajaxGetRoundTopPic(ImageView imageView, String url, int width, int radius)
	{
		AQuery aQuery = new AQuery(imageView);
		Bitmap bitmap = aQuery.getCachedImage(url, width);
		if (bitmap != null)
		{
			LogcatTools.debug("ajaxGetRoundTopPic", "从缓存读取图片:" + url);
			bitmap = BitmapTools.fillet(BitmapTools.TOP, bitmap, radius);
			imageView.setImageBitmap(bitmap);
		}
		else
		{
			LogcatTools.debug("ajaxGetRoundTopPic", "图片地址:" + url);
			aQuery.image(url, false, true);
		}
	}
}
