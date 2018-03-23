/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2016.
 */
package com.external.xlistview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lrcall.appuser.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by limxing on 16/1/7.
 */
public class LoadView extends ImageView
{
	private float degrees = 0f;
	private Matrix max;
	private int width;
	private int height;
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			degrees += 30f;
			max.setRotate(degrees, width, height);
			setImageMatrix(max);
			if (degrees == 360)
			{
				degrees = 0;
			}
		}
	};
	private Bitmap bitmap;

	public LoadView(Context context)
	{
		super(context);
		init();
	}

	public LoadView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public LoadView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init()
	{
		setScaleType(ScaleType.MATRIX);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loading);
		setImageBitmap(bitmap);
		max = new Matrix();
		width = bitmap.getWidth() / 2;
		height = bitmap.getHeight() / 2;
		Timer time = new Timer();
		time.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				handler.sendEmptyMessage(0);
			}
		}, 0, 80);
	}
}
