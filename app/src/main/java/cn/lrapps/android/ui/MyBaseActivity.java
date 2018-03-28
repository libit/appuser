/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.lrcall.appuser.R;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.viewtools.DisplayTools;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 自定义ActionBar基类
 * Created by libit on 15/8/30.
 */
public class MyBaseActivity extends SwipeBackActivity
{
	//工具栏
	protected Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//		setOverflowShowingAlways();
	}

	//初始化，子类继承时必须先调用此方法再初始化其他方法
	protected void viewInit()
	{
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		if (mToolbar != null)
		{
			CharSequence title = getTitle();
			if (title != null)
			{
				mToolbar.setTitle(title.toString());// 标题的文字需在setSupportActionBar之前，不然会无效
			}
			// toolbar.setSubtitle("副标题");
			setSupportActionBar(mToolbar);
		}
		try
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		catch (NullPointerException e)
		{
			LogcatTools.error("viewInit", "获取ActionBar失败！");
		}
		//设置滑动返回区域
		getSwipeBackLayout().setEdgeSize(DisplayTools.getWindowWidth(this) / 6);
	}

	/**
	 * 界面颜色的更改
	 */
	protected void colorChange(int resId)
	{
		// 用来提取颜色的Bitmap
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			//			Window win = getWindow();
			//			WindowManager.LayoutParams winParams = win.getAttributes();
			//			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			//			winParams.flags |= bits;
			//			win.setAttributes(winParams);
			//			SystemStatusManager tintManager = new SystemStatusManager(this);
			//			//打开系统状态栏控制
			//			tintManager.setStatusBarTintEnabled(true);
			//			tintManager.setStatusBarTintResource(R.drawable.chat_title_bg_repeat);//设置背景
			//			View layoutAll = findViewById(R.id.layoutAll);
			//			//设置系统栏需要的内偏移
			//			layoutAll.setPadding(0, ScreenUtils.getStatusHeight(this), 0, 0);
			//			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	//后退按钮
	protected void setBackButton()
	{
		//		mToolbar.setNavigationIcon(R.drawable.back);
		if (mToolbar != null)
		{
			mToolbar.setNavigationOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					finish();
				}
			});
		}
	}
}
