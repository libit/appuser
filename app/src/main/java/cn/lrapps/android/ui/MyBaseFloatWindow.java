/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.lrcall.appuser.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.FloatFuncType;
import cn.lrapps.enums.StatusType;
import cn.lrapps.events.AppEvent;
import cn.lrapps.events.AppListEvent;
import cn.lrapps.models.AppInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.apptools.AppFactory;

/**
 * Created by libit on 16/6/13.
 */
public abstract class MyBaseFloatWindow implements View.OnClickListener
{
	protected final Activity mActivity;
	protected final Context mContext;
	protected View rootView, layoutList;
	protected GridView gvAppList;
	protected GridView gvCommonUseAppList;
	protected final WindowManager windowManager;
	protected final WindowManager.LayoutParams params;
	protected final List<AppInfo> mBlackAppInfoList = new ArrayList<>();
	protected final List<AppInfo> mCommonUseAppInfoList = new ArrayList<>();
	protected final Handler mHandler = new Handler();

	// 构造函数初始化
	protected MyBaseFloatWindow(Activity activity)
	{
		mActivity = activity;
		mContext = activity;
		windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();
	}

	//设置适配器
	public abstract void setAppListAdapter();

	/**
	 * @Method: showTopWindow
	 * @Description: 显示最顶层view
	 */
	public abstract void showTopWindow();

	/**
	 * @Method: clearTopWindow
	 * @Description: 移除最顶层view
	 */
	public void clearTopWindow()
	{
		if (rootView != null && rootView.isShown())
		{
			windowManager.removeView(rootView);
		}
	}

	//获取app列表显示状态
	public int getLayoutListStatus()
	{
		return layoutList.getVisibility();
	}

	//更新内存信息
	public void updateMemoryInfo(String memoryInfo)
	{
	}

	//刷新app列表
	public void refreshAppList()
	{
		EventBus.getDefault().post(new AppEvent(AppEvent.GET_ENABLED_BLACK_APP, null));
	}

	//视图初始化，必须放在子类视图初始化之后
	protected void viewInit()
	{
		if (gvAppList != null)
		{
			gvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					if (mBlackAppInfoList.size() > position)
					{
						AppInfo appInfo = mBlackAppInfoList.get(position);
						if (PreferenceUtils.getInstance().getStringValue(PreferenceUtils.FLOAT_FUNC).equals(FloatFuncType.START.getType()))
						{
							hideList();
							startApp(appInfo);
						}
						else
						{
							disableApp(appInfo);
						}
					}
				}
			});
			gvAppList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
			{
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
				{
					//					switchList();
					if (position >= mBlackAppInfoList.size())
					{
						return true;
					}
					final AppInfo appInfo = mBlackAppInfoList.get(position);
					final PopupMenu popupMenu = new PopupMenu(mContext, gvAppList.getChildAt(position));
					Menu menu = popupMenu.getMenu();
					mActivity.getMenuInflater().inflate(R.menu.menu_float_bottom, menu);
					popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
					{
						@Override
						public boolean onMenuItemClick(MenuItem item)
						{
							int id = item.getItemId();
							if (id == R.id.action_start_app)
							{
								hideList();
								startApp(appInfo);
							}
							else if (id == R.id.action_close_app)
							{
								AppFactory.getInstance().killApp(appInfo.getPackageName());
								refreshAppList();
							}
							else if (id == R.id.action_disable_app)
							{
								disableApp(appInfo);
							}
							else if (id == R.id.action_uninstall_app)
							{
								hideList();
								AppFactory.getInstance().uninstallApp(appInfo.getPackageName(), false);
							}
							return true;
						}
					});
					popupMenu.show();
					return true;
				}
			});
		}
		if (gvCommonUseAppList != null)
		{
			gvCommonUseAppList.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					if (mCommonUseAppInfoList.size() > position)
					{
						AppInfo appInfo = mCommonUseAppInfoList.get(position);
						hideList();
						enableApp(appInfo.getPackageName());
					}
				}
			});
			gvCommonUseAppList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
			{
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
				{
					//					switchList();
					if (position >= mCommonUseAppInfoList.size())
					{
						return true;
					}
					final AppInfo appInfo = mCommonUseAppInfoList.get(position);
					final PopupMenu popupMenu = new PopupMenu(mContext, gvCommonUseAppList.getChildAt(position));
					Menu menu = popupMenu.getMenu();
					mActivity.getMenuInflater().inflate(R.menu.menu_float_bottom, menu);
					popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
					{
						@Override
						public boolean onMenuItemClick(MenuItem item)
						{
							int id = item.getItemId();
							if (id == R.id.action_start_app)
							{
								hideList();
								startApp(appInfo);
							}
							else if (id == R.id.action_close_app)
							{
								AppFactory.getInstance().killApp(appInfo.getPackageName());
								refreshAppList();
							}
							else if (id == R.id.action_disable_app)
							{
								disableApp(appInfo);
							}
							else if (id == R.id.action_uninstall_app)
							{
								hideList();
								AppFactory.getInstance().uninstallApp(appInfo.getPackageName(), false);
							}
							return true;
						}
					});
					popupMenu.show();
					return true;
				}
			});
		}
	}

	//切换显示app列表
	protected void switchList()
	{
		if (layoutList.getVisibility() == View.VISIBLE)
		{
			hideList();
		}
		else
		{
			showList();
		}
	}

	//显示app列表
	protected void showList()
	{
		if (layoutList.getVisibility() != View.VISIBLE)
		{
			refreshAppList();
			layoutList.setVisibility(View.VISIBLE);
		}
	}

	//隐藏app列表
	protected void hideList()
	{
		if (layoutList.getVisibility() == View.VISIBLE)
		{
			layoutList.setVisibility(View.GONE);
		}
		LogcatTools.debug("hideList", "隐藏app列表");
		PreferenceUtils.getInstance().setStringValue(PreferenceUtils.FLOAT_FUNC, FloatFuncType.START.getType());
	}

	protected void startApp(AppInfo appInfo)
	{
		if (!appInfo.isEnabled())
		{
			enableApp(appInfo.getPackageName());
		}
		ReturnInfo returnInfo = AppFactory.getInstance().startApp(MyApplication.getContext(), appInfo.getPackageName());
		if (!ReturnInfo.isSuccess(returnInfo))
		{
			Toast.makeText(mActivity, appInfo.getName() + "不能启动！", Toast.LENGTH_LONG).show();
		}
	}

	protected void enableApp(final String packageName)
	{
		AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
		if (appInfo.isEnabled())
		{
			Toast.makeText(mActivity, appInfo.getName() + "已启用。", Toast.LENGTH_LONG).show();
		}
		else
		{
			ReturnInfo returnInfo = AppFactory.getInstance().enableApp(packageName, false);
			String msg = "";
			if (ReturnInfo.isSuccess(returnInfo))
			{
				appInfo.setIsEnabled(false);
				msg = String.format("启用%s成功。", appInfo.getName());
				Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
			}
			else
			{
				msg = String.format("启用%s失败：%s。", appInfo.getName(), returnInfo.getMsg());
				Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
			}
		}
		refreshAppList();
	}

	protected void disableApp(final AppInfo appInfo)
	{
		//		mHandler.post(new Thread("disableApp")
		//		{
		//			@Override
		//			public void run()
		//			{
		//				super.run();
		//			}
		//		});
		ReturnInfo returnInfo = AppFactory.getInstance().disableApp(appInfo.getPackageName());
		String msg = "";
		if (ReturnInfo.isSuccess(returnInfo))
		{
			appInfo.setIsEnabled(false);
			msg = String.format("冻结%s成功。", appInfo.getName());
			Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
		}
		else
		{
			msg = String.format("冻结%s失败：%s。", appInfo.getName(), returnInfo.getMsg());
			Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
		}
		refreshAppList();
	}

	protected void getCommonUseApps()
	{
		Thread thread = new Thread("getCommonUseAppsThread")
		{
			@Override
			public void run()
			{
				super.run();
				List<AppInfo> appInfoList = DbAppFactory.getInstance().getAppInfoList(null, null, null, null, StatusType.ENABLE.getStatus());
				EventBus.getDefault().post(new AppListEvent(AppListEvent.GET_COMMON_USE_APP, appInfoList));
			}
		};
		thread.start();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_show:
			{
				showList();
				break;
			}
			case R.id.btn_home:
			{
				hideList();
				MyApplication.getInstance().backToHome();
				break;
			}
			case R.id.btn_start_app:
			{
				hideList();
				//				mContext.startActivity(new Intent(MyApplication.getContext(), ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				MyApplication.getContext().startActivity(new Intent(MyApplication.getContext(), ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				break;
			}
			case R.id.btn_close_all:
			{
				hideList();
				Intent intent = new Intent(MyApplication.getContext(), ActivityLauncher.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(ConstValues.DATA_PACKAGE_NAME, ActivityLauncher.CLOSE_ALL);
				MyApplication.getContext().startActivity(intent);
				break;
			}
			default:
			{
				hideList();
			}
		}
	}
}
