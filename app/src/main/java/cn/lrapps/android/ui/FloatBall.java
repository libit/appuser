/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;

import cn.lrapps.android.ui.adapter.FloatAppListAdapter;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.enums.LocationType;
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
public class FloatBall extends MyBaseFloatWindow
{
	private ImageView imgBtnShow;
	private final int COL_NUM = 4;
	private LocationType mLocationType = LocationType.NONE;

	public FloatBall(Activity activity)
	{
		super(activity);
	}

	@Override
	public void viewInit()
	{
		rootView = LayoutInflater.from(mContext).inflate(R.layout.float_window_ball, null);
		layoutList = rootView.findViewById(R.id.layout_list);
		imgBtnShow = (ImageView) rootView.findViewById(R.id.btn_show);
		gvAppList = (GridView) rootView.findViewById(R.id.gv_black_apps);
		gvCommonUseAppList = (GridView) rootView.findViewById(R.id.gv_common_use_apps);
		gvAppList.setNumColumns(COL_NUM);
		gvCommonUseAppList.setNumColumns(COL_NUM);
		rootView.setOnClickListener(this);
		rootView.findViewById(R.id.btn_start_app).setOnClickListener(this);
		imgBtnShow.setOnClickListener(this);
		rootView.findViewById(R.id.btn_close_all).setOnClickListener(this);
		super.viewInit();
	}

	@Override
	protected void showList()
	{
		LogcatTools.debug("showList", "FloatBall的showList调用！");
		super.showList();
		params.x = 0;
		params.y = 0;
		windowManager.updateViewLayout(rootView, params);
		imgBtnShow.setImageResource(R.drawable.ball);
		rootView.findViewById(R.id.btn_start_app).setVisibility(View.VISIBLE);
		rootView.findViewById(R.id.btn_close_all).setVisibility(View.VISIBLE);
		((View) imgBtnShow.getParent()).setBackgroundResource(R.drawable.bg_sharp_10);
	}

	@Override
	protected void hideList()
	{
		super.hideList();
		imgBtnShow.setAlpha(0.4f);
		rootView.findViewById(R.id.btn_start_app).setVisibility(View.GONE);
		rootView.findViewById(R.id.btn_close_all).setVisibility(View.GONE);
		((View) imgBtnShow.getParent()).setBackgroundColor(0x00ffffff);
		int winWidth = DisplayTools.getWindowWidth(mContext);
		int winHeight = DisplayTools.getWindowHeight(mContext);
		if (mLocationType == LocationType.LEFT)
		{
			setBallImg(-winWidth, 0);
			params.x = -winWidth;
			params.y = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.HOVER_BALL_Y);
		}
		else if (mLocationType == LocationType.RIGHT)
		{
			setBallImg(winWidth, 0);
			params.x = winWidth;
			params.y = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.HOVER_BALL_Y);
		}
		else if (mLocationType == LocationType.BOTTOM)
		{
			setBallImg(winWidth / 2, winHeight);
			params.x = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.HOVER_BALL_X);
			params.y = winHeight;
		}
		if (mLocationType != LocationType.NONE)
		{
			rootView.post(new Thread("resizeview")
			{
				@Override
				public void run()
				{
					super.run();
					windowManager.updateViewLayout(rootView, params);
				}
			});
		}
	}

	@Override
	public void refreshAppList()
	{
		super.refreshAppList();
		EventBus.getDefault().post(new AppEvent(AppEvent.GET_RUNNING_APP, null));
		getCommonUseApps();
	}

	@Override
	public void setAppListAdapter()
	{
		FloatAppListAdapter floatAppListAdapter = new FloatAppListAdapter(mContext, mBlackAppInfoList);
		gvAppList.setAdapter(floatAppListAdapter);
		ViewHeightCalTools.setGridViewHeight(gvAppList, COL_NUM, true);
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
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// 设置显示初始位置 屏幕左上角为原点
		String x = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.HOVER_BALL_X);
		String y = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.HOVER_BALL_Y);
		int intX = 0, intY = 0;
		try
		{
			intX = Integer.parseInt(x);
		}
		catch (Exception e)
		{
		}
		try
		{
			intY = Integer.parseInt(y);
		}
		catch (Exception e)
		{
		}
		params.x = intX;
		params.y = intY;
		params.format = PixelFormat.TRANSPARENT;
		// topWindow显示到最顶部
		windowManager.addView(rootView, params);
		imgBtnShow.setOnTouchListener(new View.OnTouchListener()
		{
			private float downLastX = 0;
			private float downLastY = 0;
			private float lastX = 0;
			private float lastY = 0;
			//			private boolean bMoved = false;

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				int action = event.getAction();
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
				else if (action == MotionEvent.ACTION_MOVE)
				{
					float dx = event.getRawX() - lastX;
					float dy = event.getRawY() - lastY;
					lastX = event.getRawX();
					lastY = event.getRawY();
					LogcatTools.debug("DraftImageView", "ACTION_MOVE lastX:" + lastX + ",lastY:" + lastY + ",left:" + rootView.getLeft() + ",right:" + rootView.getRight() + ",top:" + rootView.getTop() + ",bottom:" + rootView.getBottom() + ",params.x:" + params.x + ",params.y:" + params.y);
					//					setBallImg(lastX, lastY);
					if (dx != 0 || dy != 0)
					{
						params.x = params.x + (int) dx;
						params.y = params.y + (int) dy;
						PreferenceUtils.getInstance().setStringValue(PreferenceUtils.HOVER_BALL_X, params.x + "");
						PreferenceUtils.getInstance().setStringValue(PreferenceUtils.HOVER_BALL_Y, params.y + "");
						windowManager.updateViewLayout(rootView, params);
						//						bMoved = true;
						return true;
					}
				}
				else if (action == MotionEvent.ACTION_UP)
				{
					lastX = event.getRawX();
					lastY = event.getRawY();
					float dx = lastX - downLastX;
					float dy = lastY - downLastY;
					LogcatTools.debug("DraftImageView", "ACTION_UP lastX:" + lastX + ",lastY:" + lastY + ",left:" + rootView.getLeft() + ",right:" + rootView.getRight() + ",top:" + rootView.getTop() + ",bottom:" + rootView.getBottom() + ",params.x:" + params.x + ",params.y:" + params.y);
					setBallImg(lastX, lastY);
					if (Math.abs(dx) > 10 || Math.abs(dy) > 10)
					{
						return true;
					}
				}
				return false;
			}
		});
		rootView.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				int action = event.getAction();
				if (action == MotionEvent.ACTION_OUTSIDE)
				{
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
		setBallImg(PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.HOVER_BALL_X), PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.HOVER_BALL_Y));
	}

	private void setBallImg(float x, float y)
	{
		int winWidth = DisplayTools.getWindowWidth(mContext);
		int winHeight = DisplayTools.getWindowHeight(mContext);
		if (x < imgBtnShow.getWidth())
		{
			mLocationType = LocationType.LEFT;
			imgBtnShow.setImageResource(R.drawable.ic_ball_right);
			//			params.x = 0;
			//			windowManager.updateViewLayout(rootView, params);
		}
		else if (x > winWidth - imgBtnShow.getWidth())
		{
			mLocationType = LocationType.RIGHT;
			imgBtnShow.setImageResource(R.drawable.ic_ball_left);
			//			params.x = winWidth - imgBtnShow.getWidth();
			//			windowManager.updateViewLayout(rootView, params);
		}
		else if (y > winHeight - 30)
		{
			mLocationType = LocationType.BOTTOM;
			imgBtnShow.setImageResource(R.drawable.ic_ball_up);
		}
		else
		{
			//			mLocationType = LocationType.NONE;
			imgBtnShow.setImageResource(R.drawable.ball);
		}
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
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_show:
			{
				switchList();
				break;
			}
			case R.id.btn_start_app:
				//			{
				//				switchList();
				//				mContext.startActivity(new Intent(MyApplication.getContext(), ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				//				//				MyApplication.getContext().startActivity(new Intent(MyApplication.getContext(), ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				//				break;
				//			}
			case R.id.btn_close_all:
				//			{
				//				switchList();
				//				Intent intent = new Intent(MyApplication.getContext(), ActivityLauncher.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//				intent.putExtra(ConstValues.DATA_PACKAGE_NAME, "clearAll");
				//				mContext.startActivity(intent);
				//				break;
				//			}
			{
				super.onClick(v);
			}
			default:
			{
				imgBtnShow.setAlpha(0.4f);
				layoutList.setVisibility(View.GONE);
			}
		}
	}
}
