/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lrapps.android.ui.adapter.FloatAppListAdapter;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.enums.FloatFuncType;
import cn.lrapps.events.AppEvent;
import cn.lrapps.events.AppListEvent;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.viewtools.DisplayTools;
import cn.lrapps.utils.viewtools.ViewHeightCalTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by libit on 16/6/13.
 */
public class FloatSideBottom extends MyBaseFloatWindow
{
	private TextView tvMemory;
	private CheckBox cbFunc;

	public FloatSideBottom(Activity activity)
	{
		super(activity);
	}

	public void viewInit()
	{
		rootView = LayoutInflater.from(mContext).inflate(R.layout.float_window_side_bottom, null);
		layoutList = rootView.findViewById(R.id.layout_list);
		gvAppList = (GridView) rootView.findViewById(R.id.gv_black_apps);
		gvCommonUseAppList = (GridView) rootView.findViewById(R.id.gv_common_use_apps);
		tvMemory = (TextView) rootView.findViewById(R.id.tv_memory_info);
		rootView.setOnClickListener(this);
		rootView.findViewById(R.id.btn_start_app).setOnClickListener(this);
		rootView.findViewById(R.id.btn_show).setOnClickListener(this);
		rootView.findViewById(R.id.btn_home).setOnClickListener(this);
		rootView.findViewById(R.id.btn_close_all).setOnClickListener(this);
		cbFunc = (CheckBox) rootView.findViewById(R.id.cb_float_func);
		cbFunc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
				{
					buttonView.setButtonDrawable(R.drawable.btn_checked);
					PreferenceUtils.getInstance().setStringValue(PreferenceUtils.FLOAT_FUNC, FloatFuncType.START.getType());
				}
				else
				{
					buttonView.setButtonDrawable(R.drawable.btn_nocheck);
					PreferenceUtils.getInstance().setStringValue(PreferenceUtils.FLOAT_FUNC, FloatFuncType.DISABLE.getType());
				}
			}
		});
		LinearLayout layoutSideLine = (LinearLayout) rootView.findViewById(R.id.layout_side_line);
		ViewGroup.LayoutParams layoutParams = layoutSideLine.getLayoutParams();
		layoutParams.height = DisplayTools.dip2px(mContext, PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.FLOAT_SIDE_WIDTH));
		layoutSideLine.setLayoutParams(layoutParams);
		super.viewInit();
	}

	/**
	 * @Method: showTopWindow
	 * @Description: 显示最顶层view
	 */
	@Override
	public void showTopWindow()
	{
		viewInit();
		// window管理器
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
		params.flags = params.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		params.flags = params.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		//		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;//WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
		// 设置全屏显示 可以根据自己需要设置大小
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.BOTTOM;
		// 设置显示初始位置 屏幕左上角为原点
		int intX = 0, intY = 0;
		params.x = intX;
		params.y = intY;
		params.format = PixelFormat.TRANSPARENT;
		// topWindow显示到最顶部
		windowManager.addView(rootView, params);
		final int windowHeight = DisplayTools.getWindowHeight(mContext);
		rootView.setOnTouchListener(new View.OnTouchListener()
		{
			private float lastY = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				int action = event.getAction();
				LogcatTools.debug("DraftImageView", "action:" + action + ",lastY:" + lastY + ".");
				if (action == MotionEvent.ACTION_DOWN)
				{
					lastY = event.getRawY();
					LogcatTools.debug("hideList", "ACTION_DOWN");
					//					LogcatTools.debug("DraftImageView", "ACTION_DOWN lastY:" + lastY + ".");
					//					showList();
					//					return true;
				}
				else if (action == MotionEvent.ACTION_UP)
				{
					float dy = lastY - event.getRawY();
					lastY = event.getRawY();
					LogcatTools.debug("hideList", "ACTION_UP");
					//					LogcatTools.debug("DraftImageView", "ACTION_UP lastY:" + lastY + ",dy:" + dy + ".");
					//					int lineHeight = rootView.findViewById(R.id.layout_side_line).getHeight();
					if (dy > windowHeight / 8)
					{
						showList();
						return true;
					}
					else if (dy < -2)
					{
						hideList();
						return true;
					}
				}
				else if (action == MotionEvent.ACTION_OUTSIDE)
				{
					LogcatTools.debug("hideList", "ACTION_OUTSIDE");
					//					LogcatTools.debug("DraftImageView", "ACTION_OUTSIDE,lastY:" + lastY + ".");
					hideList();
					return true;
				}
				return false;
			}
		});
		if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
		getCommonUseApps();
	}

	@Override
	public void clearTopWindow()
	{
		if (EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().unregister(this);
		}
		super.clearTopWindow();
	}

	@Override
	public void refreshAppList()
	{
		super.refreshAppList();
		EventBus.getDefault().post(new AppEvent(AppEvent.GET_RUNNING_APP, null));
		getCommonUseApps();
	}

	@Subscribe
	public void onEventMainThread(final AppListEvent appListEvent)
	{
		if (appListEvent != null)
		{
			if (AppListEvent.GET_COMMON_USE_APP.equals(appListEvent.getType()))
			{
				mHandler.post(new Thread()
				{
					@Override
					public void run()
					{
						super.run();
						List<AppInfo> appInfoList = appListEvent.getAppInfoList();
						mCommonUseAppInfoList.clear();
						if (appInfoList != null)
						{
							for (AppInfo appInfo : appInfoList)
							{
								mCommonUseAppInfoList.add(appInfo);
							}
						}
						FloatAppListAdapter floatAppListAdapter = new FloatAppListAdapter(mContext, appInfoList);
						gvCommonUseAppList.setAdapter(floatAppListAdapter);
						int num = 5;
						if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
						{
							num = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_PORTRAIT);
						}
						else
						{
							num = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_LANDSCAPE);
						}
						ViewHeightCalTools.setGridViewHeight(gvCommonUseAppList, num, true);
						ViewHeightCalTools.setGridViewHeight(gvAppList, num, true);
					}
				});
			}
		}
	}

	//显示app列表
	@Override
	protected void showList()
	{
		if (layoutList.getVisibility() != View.VISIBLE)
		{
			if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			{
				gvAppList.setNumColumns(PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_PORTRAIT));
			}
			else
			{
				gvAppList.setNumColumns(PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_LANDSCAPE));
			}
		}
		LogcatTools.debug("hideList", PreferenceUtils.getInstance().getStringValue(PreferenceUtils.FLOAT_FUNC));
		boolean b = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.FLOAT_FUNC).equals(FloatFuncType.START.getType());
		cbFunc.setChecked(b);
		if (b)
		{
			cbFunc.setButtonDrawable(R.drawable.btn_checked);
		}
		else
		{
			cbFunc.setButtonDrawable(R.drawable.btn_nocheck);
		}
		super.showList();
	}

	@Override
	public void updateMemoryInfo(String memoryInfo)
	{
		super.updateMemoryInfo(memoryInfo);
		tvMemory.setText(memoryInfo);
	}

	@Override
	public void setAppListAdapter()
	{
		FloatAppListAdapter floatAppListAdapter = new FloatAppListAdapter(mContext, mBlackAppInfoList);
		gvAppList.setAdapter(floatAppListAdapter);
		int num = 5;
		if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			num = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_PORTRAIT);
		}
		else
		{
			num = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_LANDSCAPE);
		}
		ViewHeightCalTools.setGridViewHeight(gvAppList, num, true);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_show:
				//			{
				//				switchList();
				//				break;
				//			}
			case R.id.btn_home:
				//			{
				//				switchList();
				//				MyApplication.getInstance().backToHome();
				//				break;
				//			}
			case R.id.btn_start_app:
				//			{
				//				switchList();
				//				MyApplication.getContext().startActivity(new Intent(MyApplication.getContext(), ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				//				break;
				//			}
			case R.id.btn_close_all:
				//			{
				//				LogcatTools.debug("DraftImageView", "btn_close_all");
				//				switchList();
				//				Intent intent3 = new Intent(MyApplication.getContext(), ActivityLauncher.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//				intent3.putExtra(ConstValues.DATA_PACKAGE_NAME, "clearAll");
				//				MyApplication.getContext().startActivity(intent3);
				//				break;
				//			}
			{
				super.onClick(v);
			}
			default:
			{
				hideList();
			}
		}
	}
}
