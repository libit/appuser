/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.utils.viewtools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by libit on 16/7/10.
 */
public class BitmapTools
{
	/**
	 * 压缩Bmp到ByteArrayOutputStream
	 *
	 * @param bitmap 要压缩的图片
	 *
	 * @return
	 */
	public static ByteArrayOutputStream compressToByteArrayOutputStream(Bitmap bitmap)
	{
		if (bitmap == null)
		{
			return null;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try
		{
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);// 把数据写入文件
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if (byteArrayOutputStream != null)
			{
				try
				{
					byteArrayOutputStream.flush();
					byteArrayOutputStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return byteArrayOutputStream;
	}

	/**
	 * 压缩图片到FileOutputStream
	 *
	 * @param bitmap   要压缩的图片
	 * @param filePath 保存的文件路径
	 *
	 * @return
	 */
	public static FileOutputStream compressToFileOutputStream(Bitmap bitmap, String filePath)
	{
		if (bitmap == null)
		{
			return null;
		}
		FileOutputStream f = null;
		try
		{
			f = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, f);// 把数据写入文件
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if (f != null)
			{
				try
				{
					f.flush();
					f.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return f;
	}

	/**
	 * 从文件获取BMP图片
	 *
	 * @param filePath
	 *
	 * @return
	 */
	public static Bitmap getBmpFile(String filePath)
	{
		return BitmapFactory.decodeFile(filePath);
	}

	/**
	 * 保存Bmp文件
	 *
	 * @param file
	 * @param filePath 保存的文件路径
	 */
	public static void saveBmpFile(File file, String filePath)
	{
		try
		{
			FileOutputStream fOut = new FileOutputStream(filePath);
			Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 根据给定的图片和已经测量出来的宽高来绘制圆角图形
	 * 原理：
	 * 基本原理就是先画一个圆角的图形出来，然后在圆角图形上画我们的源图片，
	 * 圆角图形跟我们的源图片堆叠时我们取交集并显示上层的图形
	 * 原理就是这样，很简单。
	 */
	public static Bitmap createRoundConerImage(Bitmap source, int width, int height, int radiusX, int radiusY)
	{
		try
		{
			final Paint paint = new Paint();
			/**开启抗锯齿**/
			paint.setAntiAlias(true);
			/****/
			Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			/**
			 * Construct a canvas with the specified bitmap to draw into. The bitmapmust be mutable
			 * 以bitmap对象创建一个画布，则将内容都绘制在bitmap上，bitmap不得为null;
			 */
			Canvas canvas = new Canvas(target);
			/**新建一个矩形绘制区域,并给出左上角和右下角的坐标**/
			RectF rect = new RectF(0, 0, width, height);
			/**
			 * 把图片缩放成我们想要的大小
			 */
			source = Bitmap.createScaledBitmap(source, width, height, false);
			/**在绘制矩形区域绘制用画笔绘制一个圆角矩形**/
			canvas.drawRoundRect(rect, radiusX, radiusY, paint);
			/**
			 * 我简单理解为设置画笔在绘制时图形堆叠时候的显示模式
			 * SRC_IN:取两层绘制交集。显示上层。
			 */
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(source, 0, 0, paint);
			/****/
			return target;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static final int ALL = 347120;
	public static final int TOP = 547120;
	public static final int LEFT = 647120;
	public static final int RIGHT = 747120;
	public static final int BOTTOM = 847120;

	/**
	 * 指定图片的切边，对图片进行圆角处理
	 *
	 * @param type    具体参见：{ BitmapTools.ALL} , { BitmapTools.TOP} , { BitmapTools.LEFT} , { BitmapTools.RIGHT} , { BitmapTools.BOTTOM}
	 * @param bitmap  需要被切圆角的图片
	 * @param roundPx 要切的像素大小
	 *
	 * @return
	 */
	public static Bitmap fillet(int type, Bitmap bitmap, int roundPx)
	{
		try
		{
			// 其原理就是：先建立一个与图片大小相同的透明的Bitmap画板
			// 然后在画板上画出一个想要的形状的区域。
			// 最后把源图片帖上。
			final int width = bitmap.getWidth();
			final int height = bitmap.getHeight();
			Bitmap paintingBoard = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(paintingBoard);
			canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
			final Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			if (TOP == type)
			{
				clipTop(canvas, paint, roundPx, width, height);
			}
			else if (LEFT == type)
			{
				clipLeft(canvas, paint, roundPx, width, height);
			}
			else if (RIGHT == type)
			{
				clipRight(canvas, paint, roundPx, width, height);
			}
			else if (BOTTOM == type)
			{
				clipBottom(canvas, paint, roundPx, width, height);
			}
			else
			{
				clipAll(canvas, paint, roundPx, width, height);
			}
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			//帖子图
			final Rect src = new Rect(0, 0, width, height);
			final Rect dst = src;
			canvas.drawBitmap(bitmap, src, dst, paint);
			return paintingBoard;
		}
		catch (Exception exp)
		{
			return bitmap;
		}
	}

	private static void clipLeft(final Canvas canvas, final Paint paint, int offset, int width, int height)
	{
		final Rect block = new Rect(offset, 0, width, height);
		canvas.drawRect(block, paint);
		final RectF rectF = new RectF(0, 0, offset * 2, height);
		canvas.drawRoundRect(rectF, offset, offset, paint);
	}

	private static void clipRight(final Canvas canvas, final Paint paint, int offset, int width, int height)
	{
		final Rect block = new Rect(0, 0, width - offset, height);
		canvas.drawRect(block, paint);
		final RectF rectF = new RectF(width - offset * 2, 0, width, height);
		canvas.drawRoundRect(rectF, offset, offset, paint);
	}

	private static void clipTop(final Canvas canvas, final Paint paint, int offset, int width, int height)
	{
		final Rect block = new Rect(0, offset, width, height);
		canvas.drawRect(block, paint);
		final RectF rectF = new RectF(0, 0, width, offset * 2);
		canvas.drawRoundRect(rectF, offset, offset, paint);
	}

	private static void clipBottom(final Canvas canvas, final Paint paint, int offset, int width, int height)
	{
		final Rect block = new Rect(0, 0, width, height - offset);
		canvas.drawRect(block, paint);
		final RectF rectF = new RectF(0, height - offset * 2, width, height);
		canvas.drawRoundRect(rectF, offset, offset, paint);
	}

	private static void clipAll(final Canvas canvas, final Paint paint, int offset, int width, int height)
	{
		final RectF rectF = new RectF(0, 0, width, height);
		canvas.drawRoundRect(rectF, offset, offset, paint);
	}
}
