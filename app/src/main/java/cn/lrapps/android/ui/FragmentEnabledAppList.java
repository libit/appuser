/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import cn.lrapps.android.ui.adapter.AppListAdapter;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.AppEnableStatus;
import cn.lrapps.enums.AppShowStatus;
import cn.lrapps.events.AppEvent;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.apptools.AppFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class FragmentEnabledAppList extends MyBaseFragment
{
	private static final String TAG = FragmentEnabledAppList.class.getSimpleName();
	private static final int APP_STATUS = AppEnableStatus.ENABLED.getStatus();
	private final Handler mHandler = new Handler();
	private GridView gvEnabledAppList;

	public FragmentEnabledAppList()
	{
	}

	public static FragmentEnabledAppList newInstance()
	{
		return new FragmentEnabledAppList();
	}

	@Override
	public Activity getAttachedActivity()
	{
		if (mActivity != null)
		{
			return mActivity;
		}
		else
		{
			return this.getActivity();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_app_list, container, false);
		viewInit(rootView);
		if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		if (EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().unregister(this);
		}
		super.onDestroyView();
	}
	//	@Override
	//	public void onAppClicked(AppInfo appInfo)
	//	{
	//	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		this.mActivity = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		registerForContextMenu(gvEnabledAppList);
		getAppInfos();
	}

	@Override
	public void onPause()
	{
		unregisterForContextMenu(gvEnabledAppList);
		super.onPause();
	}
	//	@Override
	//	public void fragmentShow()
	//	{
	//		super.fragmentShow();
	//		registerForContextMenu(gvEnabledAppList);
	//	}
	//
	//	@Override
	//	public void fragmentHide()
	//	{
	//		unregisterForContextMenu(gvEnabledAppList);
	//		super.fragmentHide();
	//	}

	@Override
	protected void viewInit(View rootView)
	{
		gvEnabledAppList = (GridView) rootView.findViewById(R.id.gvAppList);
		super.viewInit(rootView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		getAttachedActivity().getMenuInflater().inflate(R.menu.menu_gridview_enabled, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		LogcatTools.debug("onContextItemSelected", "FragmentEnabledAppList");
		int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
		if (mAppInfoList.size() <= position)
		{
			return super.onContextItemSelected(item);
		}
		AppInfo appInfo = (AppInfo) mAppListAdapter.getItem(position);
		LogcatTools.debug("onContextItemSelected", "APPINFO:" + appInfo.toString());
		int id = item.getItemId();
		if (id == R.id.action_enable_hide_app)
		{
			hideApp(appInfo);
			return true;
		}
		else if (id == R.id.action_enable_enable)
		{
			enableApp(appInfo, false);
			return true;
		}
		else if (id == R.id.action_enable_disable)
		{
			disableApp(appInfo);
			return true;
		}
		else if (id == R.id.action_enable_uninstall)
		{
			AppFactory.getInstance().uninstallApp(appInfo.getPackageName(), false);
			//			onAppClicked(appInfo);
			return true;
		}
		else if (id == R.id.action_enable_create_desktop_icon)
		{
			appInfo = AppFactory.getInstance().getAppInfoByPackageName(appInfo.getPackageName(), true);
			createAppShortcut(appInfo);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void getAppInfos()
	{
		mHandler.post(new Thread("getAppInfos")
		{
			@Override
			public void run()
			{
				super.run();
				mAppInfoList = DbAppFactory.getInstance().getAppInfoList(APP_STATUS, null, AppShowStatus.SHOW.getStatus(), null, null);
				initData();
			}
		});
	}

	private void initData()
	{
		if (mAppListAdapter == null)
		{
			mAppListAdapter = new AppListAdapter(getAttachedActivity(), mAppInfoList);
			gvEnabledAppList.setAdapter(mAppListAdapter);
			gvEnabledAppList.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					LogcatTools.debug("onItemClick", String.format("position:%d,id:%d,parent:%s,view:%s.", position, id, parent, view));
					AppInfo appInfo = mAppInfoList.get(position);
					enableApp(appInfo, false);
				}
			});
		}
		else
		{
			mAppListAdapter.setData(mAppInfoList);
			mAppListAdapter.notifyDataSetChanged();
		}
		if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			gvEnabledAppList.setNumColumns(PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_PORTRAIT));
		}
		else
		{
			gvEnabledAppList.setNumColumns(PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_LANDSCAPE));
		}
	}

	public boolean OnAppStatusChanged(final AppInfo appInfo)
	{
		if (appInfo == null || mAppListAdapter == null || mAppInfoList == null)
		{
			getAppInfos();
			return true;
		}
		boolean isExist = false;
		for (AppInfo app : mAppInfoList)
		{
			if (app.getPackageName().equals(appInfo.getPackageName()))
			{
				isExist = true;
				break;
			}
		}
		if (!isExist)
		{
			if (appInfo.isEnabled())
			{
				mAppInfoList.add(appInfo);
				mAppListAdapter.setData(mAppInfoList);
				mAppListAdapter.notifyDataSetChanged();
				return true;
			}
		}
		else
		{
			if (appInfo.isHide() || !appInfo.isEnabled())
			{
				mAppListAdapter.remove(appInfo.getUid());
				mAppInfoList = mAppListAdapter.getData();
				mAppListAdapter.notifyDataSetChanged();
				return true;
			}
		}
		return false;
	}

	@Subscribe
	public void onEventMainThread(final AppEvent event)
	{
		mHandler.post(new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				if (event != null)
				{
					if (event.getType().equalsIgnoreCase(AppEvent.APP_STATUS_CHANGED))
					{
						if (event.getPackageNameList() != null && event.getPackageNameList().size() != 1)
						{
							for (String packageName : event.getPackageNameList())
							{
								AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
								OnAppStatusChanged(appInfo);
								LogcatTools.debug(TAG, "onEventMainThread->packageName:" + packageName);
							}
						}
						else
						{
							OnAppStatusChanged(null);
						}
					}
				}
			}
		});
	}
}
