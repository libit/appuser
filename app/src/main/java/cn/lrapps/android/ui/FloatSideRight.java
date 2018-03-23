/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Activity;
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

import com.lrcall.appuser.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import cn.lrapps.android.ui.adapter.FloatAppListAdapter;
import cn.lrapps.enums.FloatFuncType;
import cn.lrapps.events.AppEvent;
import cn.lrapps.events.AppListEvent;
import cn.lrapps.models.AppInfo;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.viewtools.DisplayTools;
import cn.lrapps.utils.viewtools.ViewHeightCalTools;

/**
 * Created by libit on 16/6/13.
 */
public class FloatSideRight extends MyBaseFloatWindow
{
	private CheckBox cbFunc;
	private final int COL_NUM = 3;

	public FloatSideRight(Activity activity)
	{
		super(activity);
	}

	public void viewInit()
	{
		rootView = LayoutInflater.from(mContext).inflate(R.layout.float_window_side_right, null);
		layoutList = rootView.findViewById(R.id.layout_list);
		gvCommonUseAppList = (GridView) rootView.findViewById(R.id.gv_common_use_apps);
		gvAppList = (GridView) rootView.findViewById(R.id.gv_black_apps);
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
		layoutParams.width = DisplayTools.dip2px(mContext, PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.FLOAT_SIDE_WIDTH));
		layoutSideLine.setLayoutParams(layoutParams);
		super.viewInit();
	}

	//显示app列表
	@Override
	protected void showList()
	{
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

	/**
	 * @Method: showTopWindow
	 * @Description: 显示最顶层view
	 */
	public void showTopWindow()
	{
		viewInit();
		// window管理器
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.flags = params.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		params.flags = params.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		// 设置全屏显示 可以根据自己需要设置大小
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.MATCH_PARENT;
		params.gravity = Gravity.RIGHT;
		// 设置显示初始位置 屏幕左上角为原点
		int intX = 0, intY = 0;
		params.x = intX;
		params.y = intY;
		params.format = PixelFormat.TRANSPARENT;
		// topWindow显示到最顶部
		windowManager.addView(rootView, params);
		final int windowWidth = DisplayTools.getWindowWidth(mContext);
		final int windowHeight = DisplayTools.getWindowHeight(mContext);
		rootView.setOnTouchListener(new View.OnTouchListener()
		{
			private float downLastX = 0;
			private float downLastY = 0;
			private float lastX = 0;
			private float lastY = 0;
			private float outLastX = 0;
			private float outLastY = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				int action = event.getAction();
				LogcatTools.debug("DraftImageView", "action:" + action + ",lastX:" + lastX + ".");
				if (action == MotionEvent.ACTION_DOWN)
				{
					lastX = event.getRawX();
					lastY = event.getRawY();
					downLastX = lastX;
					downLastY = lastY;
					int left = rootView.getLeft();
					int top = rootView.getTop();
					int right = rootView.getRight();
					int bottom = rootView.getBottom();
					LogcatTools.debug("DraftImageView", "ACTION_DOWN lastX:" + lastX + ",lastY:" + lastY + ",left:" + left + ",right:" + right + ",top:" + top + ",bottom:" + bottom + ",params.x:" + params.x + ",params.y:" + params.y);
				}
				else if (action == MotionEvent.ACTION_UP)
				{
					float dx = lastX - event.getRawX();
					float dy = lastX - event.getRawY();
					lastX = event.getRawX();
					lastY = event.getRawY();
					LogcatTools.debug("DraftImageView", "ACTION_MOVE lastX:" + lastX + ",lastY:" + lastY + ",left:" + rootView.getLeft() + ",right:" + rootView.getRight() + ",top:" + rootView.getTop() + ",bottom:" + rootView.getBottom() + ",params.x:" + params.x + ",params.y:" + params.y);
					LogcatTools.debug("DraftImageView", "dx:" + dx + "," + "dy:" + dy + ".");
					//					if (Math.abs(dx) < windowWidth / 8 && Math.abs(dy) > windowHeight / 8)
					//					{
					//						showList();
					//						return true;
					//					}
					//					else
					//					{
					if (dx > windowWidth / 8)
					{
						showList();
						return true;
					}
					else if (dx < -2)
					{
						hideList();
						return true;
					}
					//					}
				}
				else if (action == MotionEvent.ACTION_OUTSIDE)
				{
					lastX = event.getRawX();
					lastY = event.getRawY();
					outLastX = lastX;
					outLastY = lastY;
					float dx = lastX - outLastX;
					float dy = lastY - outLastY;
					LogcatTools.debug("DraftImageView", "ACTION_OUTSIDE lastX:" + lastX + ",lastY:" + lastY + ",left:" + rootView.getLeft() + ",right:" + rootView.getRight() + ",top:" + rootView.getTop() + ",bottom:" + rootView.getBottom() + ",params.x:" + params.x + ",params.y:" + params.y);
					//					LogcatTools.debug("DraftImageView", "outLastX:" + outLastX + "右边距:" + Math.abs(windowWidth - outLastX) + ",滑动长度:" + Math.abs(dx));
					//					if (Math.abs(DisplayTools.getWindowWidth(mActivity) - outLastX) < 100 && Math.abs(dx) > 100)
					//					{
					//						showList();
					//					}
					//					else
					//					{
					hideList();
					//					}
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
						ViewHeightCalTools.setGridViewHeight(gvCommonUseAppList, COL_NUM, true);
						ViewHeightCalTools.setGridViewHeight(gvAppList, COL_NUM, true);
					}
				});
			}
		}
	}

	@Override
	public void setAppListAdapter()
	{
		FloatAppListAdapter floatAppListAdapter = new FloatAppListAdapter(mContext, mBlackAppInfoList);
		gvAppList.setAdapter(floatAppListAdapter);
		ViewHeightCalTools.setGridViewHeight(gvAppList, COL_NUM, true);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_show:
				//			{
				//				showList();
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
